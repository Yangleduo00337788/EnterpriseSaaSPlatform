# FlowX Platform - Production Deployment Guide

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Server Requirements](#2-server-requirements)
3. [Environment Configuration](#3-environment-configuration)
4. [Database Initialization](#4-database-initialization)
5. [Application Deployment](#5-application-deployment)
6. [Nginx Configuration](#6-nginx-configuration)
7. [SSL/TLS Setup](#7-ssltls-setup)
8. [Monitoring & Logging](#8-monitoring--logging)
9. [Backup Strategy](#9-backup-strategy)
10. [Scaling Considerations](#10-scaling-considerations)
11. [Troubleshooting](#11-troubleshooting)

---

## 1. Prerequisites

### Software Requirements

| Software | Version | Purpose |
|----------|---------|---------|
| JDK | 21 (Eclipse Temurin) | Application runtime |
| MySQL | 8.4 | Primary database |
| Redis | 7.x | Cache & session store |
| Apache Kafka | 3.7+ (KRaft mode) | Message queue |
| MinIO | Latest | Object storage |
| Nginx | 1.24+ | Reverse proxy & static files |
| Docker | 24+ | Container runtime (optional) |
| Docker Compose | 2.20+ | Container orchestration (optional) |

### Network Requirements

| Port | Service | Description |
|------|---------|-------------|
| 80 | Nginx | HTTP |
| 443 | Nginx | HTTPS |
| 8080 | FlowX App | Backend API |
| 3306 | MySQL | Database |
| 6379 | Redis | Cache |
| 9092 | Kafka | Message queue |
| 9000 | MinIO | Object storage API |
| 9001 | MinIO | MinIO Console |

---

## 2. Server Requirements

### Minimum (Development / Small Team < 50 users)

- **CPU**: 4 cores
- **RAM**: 8 GB
- **Disk**: 100 GB SSD
- **OS**: Ubuntu 22.04 LTS / CentOS Stream 9

### Recommended (Production / 50-500 users)

- **CPU**: 8 cores
- **RAM**: 16 GB
- **Disk**: 500 GB SSD
- **OS**: Ubuntu 22.04 LTS

### High Performance (500+ users)

- **CPU**: 16+ cores
- **RAM**: 32+ GB
- **Disk**: 1TB+ SSD (RAID 10)
- Separate servers for database, cache, and application

---

## 3. Environment Configuration

### 3.1 Install JDK 21

```bash
# Ubuntu / Debian
sudo apt update
sudo apt install -y openjdk-21-jdk

# CentOS / RHEL
sudo yum install -y java-21-openjdk-devel

# Verify
java -version
```

### 3.2 Install MySQL 8.4

```bash
# Ubuntu / Debian
sudo apt install -y mysql-server-8.4

# CentOS / RHEL
sudo yum install -y mysql-server

# Secure installation
sudo mysql_secure_installation

# Start and enable
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 3.3 Install Redis 7

```bash
# Ubuntu / Debian
sudo apt install -y redis-server

# CentOS / RHEL
sudo yum install -y redis

# Configure password
sudo vim /etc/redis/redis.conf
# Add: requirepass your_redis_password

sudo systemctl start redis
sudo systemctl enable redis
```

### 3.4 Install Apache Kafka

```bash
# Download Kafka
wget https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz
tar -xzf kafka_2.13-3.7.0.tgz
mv kafka_2.13-3.7.0 /opt/kafka

# KRaft mode setup
/opt/kafka/bin/kafka-storage.sh random-uuid
/opt/kafka/bin/kafka-storage.sh format -t <uuid> -c /opt/kafka/config/kraft/server.properties

# Start Kafka
/opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/kraft/server.properties
```

### 3.5 Install MinIO

```bash
# Download MinIO
wget https://dl.min.io/server/minio/release/linux-amd64/minio
chmod +x minio
sudo mv minio /usr/local/bin/

# Create data directory
sudo mkdir -p /data/minio

# Create systemd service
sudo tee /etc/systemd/system/minio.service <<EOF
[Unit]
Description=MinIO
After=network.target

[Service]
Type=simple
User=minio
Group=minio
Environment="MINIO_ROOT_USER=minioadmin"
Environment="MINIO_ROOT_PASSWORD=your_minio_password"
ExecStart=/usr/local/bin/minio server /data/minio --console-address ":9001"
Restart=always

[Install]
WantedBy=multi-user.target
EOF

# Create minio user
sudo useradd -r minio -s /sbin/nologin
sudo chown -R minio:minio /data/minio

sudo systemctl daemon-reload
sudo systemctl start minio
sudo systemctl enable minio
```

---

## 4. Database Initialization

### 4.1 Create Database

```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE IF NOT EXISTS flowx
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- Create application user (recommended)
CREATE USER 'flowx'@'%' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON flowx.* TO 'flowx'@'%';
FLUSH PRIVILEGES;
```

### 4.2 Import Schema

```bash
mysql -u flowx -p flowx < sql/flowx_init.sql
```

### 4.3 Verify

```sql
USE flowx;
SHOW TABLES;
-- Should display all system tables (sys_*) and business tables
```

---

## 5. Application Deployment

### 5.1 Build from Source

```bash
cd /opt/flowx

# Build
mvn clean package -DskipTests -B

# Verify JAR
ls -la flowx-admin/target/flowx-admin-*.jar
```

### 5.2 Configure Application

Create `/opt/flowx/config/application-prod.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/flowx?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: flowx
    password: your_secure_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      database: 0
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 4
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: flowx-consumer
      auto-offset-reset: earliest
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB

mybatis-flex:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    logic-delete-field: deleted
    logic-delete-value: 1
    logic-not-delete-value: 0

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: your_minio_password
  bucket-name: flowx

flowx:
  jwt:
    secret: Generate_A_Random_256Bit_Key_Here_For_Production
    access-token-expire-hours: 24
    refresh-token-expire-days: 7
    issuer: flowx

logging:
  level:
    com.flowx: info
    org.flowable: warn
  file:
    name: /opt/flowx/logs/flowx.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
      total-size-cap: 5GB
```

### 5.3 Create Systemd Service

```bash
sudo tee /etc/systemd/system/flowx.service <<EOF
[Unit]
Description=FlowX Enterprise SaaS Platform
After=network.target mysql.service redis.service kafka.service minio.service

[Service]
Type=simple
User=flowx
Group=flowx
WorkingDirectory=/opt/flowx
ExecStart=/usr/bin/java \\
    -Xms512m -Xmx1024m \\
    -XX:+UseG1GC \\
    -XX:MaxGCPauseMillis=200 \\
    -XX:+HeapDumpOnOutOfMemoryError \\
    -XX:HeapDumpPath=/opt/flowx/logs/heapdump.hprof \\
    -Djava.security.egd=file:/dev/./urandom \\
    -Dfile.encoding=UTF-8 \\
    -Duser.timezone=Asia/Shanghai \\
    -jar flowx-admin/target/flowx-admin-1.0.0-SNAPSHOT.jar \\
    --spring.profiles.active=prod \\
    --spring.config.additional-location=file:/opt/flowx/config/
ExecStop=/bin/kill -TERM \$MAINPID
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Create flowx user
sudo useradd -r flowx -s /sbin/nologin
sudo chown -R flowx:flowx /opt/flowx

# Start and enable
sudo systemctl daemon-reload
sudo systemctl start flowx
sudo systemctl enable flowx

# Check status
sudo systemctl status flowx
```

### 5.4 Docker Deployment (Alternative)

```bash
cd /opt/flowx/docker

# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f flowx-app
```

---

## 6. Nginx Configuration

### 6.1 Install Nginx

```bash
sudo apt install -y nginx
# or
sudo yum install -y nginx
```

### 6.2 Configuration

Create `/etc/nginx/conf.d/flowx.conf`:

```nginx
# Rate limiting zones
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=20r/s;
limit_req_zone $binary_remote_addr zone=login_limit:10m rate=5r/s;

upstream flowx_backend {
    server 127.0.0.1:8080;
    keepalive 32;
}

server {
    listen 80;
    server_name your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL certificates
    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload" always;

    # Gzip
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/json application/javascript application/xml;

    client_max_body_size 100m;

    # API proxy
    location /api/ {
        limit_req zone=api_limit burst=50 nodelay;
        proxy_pass http://flowx_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 120s;
        proxy_read_timeout 120s;
    }

    # Login rate limit
    location /api/auth/login {
        limit_req zone=login_limit burst=10 nodelay;
        proxy_pass http://flowx_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket
    location /ws/ {
        proxy_pass http://flowx_backend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_read_timeout 3600s;
    }

    # Static files
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        root /opt/flowx/frontend/dist;
        expires 30d;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # Frontend SPA
    location / {
        root /opt/flowx/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
}
```

### 6.3 Deploy Frontend

```bash
# Build frontend
cd /path/to/flowx-web
npm run build

# Copy to Nginx directory
sudo mkdir -p /opt/flowx/frontend
sudo cp -r dist/* /opt/flowx/frontend/

# Reload Nginx
sudo nginx -t
sudo systemctl reload nginx
```

---

## 7. SSL/TLS Setup

### Option A: Let's Encrypt (Free)

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal (add to crontab)
0 0 1 * * certbot renew --quiet
```

### Option B: Commercial Certificate

```bash
# Generate CSR
openssl req -new -newkey rsa:2048 -nodes \
    -keyout /etc/nginx/ssl/privkey.pem \
    -out /etc/nginx/ssl/csr.pem

# After receiving certificate from CA
# Place fullchain.pem and privkey.pem in /etc/nginx/ssl/

# Set permissions
chmod 600 /etc/nginx/ssl/privkey.pem
chmod 644 /etc/nginx/ssl/fullchain.pem
```

---

## 8. Monitoring & Logging

### 8.1 Application Logs

```bash
# View application logs
tail -f /opt/flowx/logs/flowx.log

# Log rotation is handled by Logback (configured in application-prod.yml)
```

### 8.2 System Monitoring

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check JVM metrics
curl http://localhost:8080/actuator/metrics

# Check memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 8.3 Database Monitoring

```bash
# MySQL slow query log
sudo tail -f /var/log/mysql/slow.log

# Check connections
mysql -u root -p -e "SHOW STATUS LIKE 'Threads_connected';"
```

### 8.4 Recommended Monitoring Stack (Optional)

- **Prometheus**: Metrics collection
- **Grafana**: Dashboards
- **ELK Stack**: Log aggregation (Elasticsearch, Logstash, Kibana)
- **Alertmanager**: Alert notifications

---

## 9. Backup Strategy

### 9.1 Database Backup

```bash
#!/bin/bash
# /opt/flowx/scripts/backup-db.sh

BACKUP_DIR="/opt/flowx/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
KEEP_DAYS=30

mkdir -p $BACKUP_DIR

# Full backup
mysqldump -u flowx -p'your_password' \
    --single-transaction \
    --routines \
    --triggers \
    flowx | gzip > $BACKUP_DIR/flowx_${DATE}.sql.gz

# Cleanup old backups
find $BACKUP_DIR -name "*.sql.gz" -mtime +$KEEP_DAYS -delete

echo "Backup completed: flowx_${DATE}.sql.gz"
```

```bash
# Add to crontab (daily at 2 AM)
echo "0 2 * * * /opt/flowx/scripts/backup-db.sh" | crontab -
```

### 9.2 File Storage Backup

```bash
#!/bin/bash
# /opt/flowx/scripts/backup-minio.sh

BACKUP_DIR="/opt/flowx/backups/minio"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Using mc (MinIO Client)
mc alias set flowx http://localhost:9000 minioadmin your_minio_password
mc mirror flowx/flowx $BACKUP_DIR/flowx_${DATE}

echo "MinIO backup completed"
```

### 9.3 Application Backup

```bash
# Backup configuration and JAR
tar -czf /opt/flowx/backups/app_$(date +%Y%m%d).tar.gz \
    /opt/flowx/config/ \
    /opt/flowx/flowx-admin/target/*.jar
```

---

## 10. Scaling Considerations

### 10.1 Vertical Scaling

Increase JVM memory for the application:

```bash
# In /etc/systemd/system/flowx.service
ExecStart=/usr/bin/java \
    -Xms2g -Xmx4g \
    ...
```

### 10.2 Horizontal Scaling

#### Multiple Application Instances

```nginx
# Nginx upstream with multiple backends
upstream flowx_backend {
    least_conn;
    server 10.0.0.1:8080;
    server 10.0.0.2:8080;
    server 10.0.0.3:8080;
    keepalive 32;
}
```

#### Database Read Replicas

```yaml
# application-prod.yml
spring:
  datasource:
    master:
      url: jdbc:mysql://master-db:3306/flowx
      username: flowx
      password: xxx
    slave:
      url: jdbc:mysql://slave-db:3306/flowx
      username: flowx_readonly
      password: xxx
```

#### Redis Cluster

```yaml
spring:
  data:
    redis:
      cluster:
        nodes:
          - redis-1:6379
          - redis-2:6379
          - redis-3:6379
```

### 10.3 Kubernetes Deployment (Future)

For large-scale deployments, consider migrating to Kubernetes:

```yaml
# Example Helm values
replicaCount: 3
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1024Mi"
    cpu: "1000m"
autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 70
```

---

## 11. Troubleshooting

### Application Won't Start

```bash
# Check logs
journalctl -u flowx -f

# Common issues:
# 1. Port already in use
sudo lsof -i :8080

# 2. Database connection failed
mysql -u flowx -p -h localhost -e "SELECT 1"

# 3. Redis connection failed
redis-cli -a your_password ping

# 4. Insufficient memory
free -h
```

### High Memory Usage

```bash
# Check JVM heap
jmap -heap $(pgrep -f flowx-admin)

# Generate heap dump
jmap -dump:live,format=b,file=heap.hprof $(pgrep -f flowx-admin)
```

### Database Connection Pool Exhaustion

```bash
# Check active connections
mysql -u root -p -e "SHOW STATUS LIKE 'Threads_connected';"

# Increase pool size in application-prod.yml
# druid:
#   max-active: 100
```

### Slow Queries

```bash
# Enable slow query log
mysql -u root -p -e "SET GLOBAL slow_query_log = 'ON';"
mysql -u root -p -e "SET GLOBAL long_query_time = 2;"

# Analyze slow queries
mysqldumpslow -s t /var/log/mysql/slow.log | head -20
```
