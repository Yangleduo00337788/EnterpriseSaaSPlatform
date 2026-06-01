# ============================================================================
# FlowX Enterprise SaaS Platform - Dockerfile
# Multi-stage build for Spring Boot application
# ============================================================================

# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom.xml first for dependency caching
COPY pom.xml .
COPY flowx-common/pom.xml flowx-common/
COPY flowx-infrastructure/pom.xml flowx-infrastructure/
COPY flowx-auth/pom.xml flowx-auth/
COPY flowx-user/pom.xml flowx-user/
COPY flowx-system/pom.xml flowx-system/
COPY flowx-workflow/pom.xml flowx-workflow/
COPY flowx-approval/pom.xml flowx-approval/
COPY flowx-message/pom.xml flowx-message/
COPY flowx-file/pom.xml flowx-file/
COPY flowx-ai/pom.xml flowx-ai/
COPY flowx-report/pom.xml flowx-report/
COPY flowx-admin/pom.xml flowx-admin/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY flowx-common/src flowx-common/src
COPY flowx-infrastructure/src flowx-infrastructure/src
COPY flowx-auth/src flowx-auth/src
COPY flowx-user/src flowx-user/src
COPY flowx-system/src flowx-system/src
COPY flowx-workflow/src flowx-workflow/src
COPY flowx-approval/src flowx-approval/src
COPY flowx-message/src flowx-message/src
COPY flowx-file/src flowx-file/src
COPY flowx-ai/src flowx-ai/src
COPY flowx-report/src flowx-report/src
COPY flowx-admin/src flowx-admin/src

# Build the application (skip tests for Docker build)
RUN mvn clean package -pl flowx-admin -am -DskipTests -B

# Stage 2: Run with Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="FlowX Team <team@flowx.com>"
LABEL description="FlowX Enterprise SaaS Platform"
LABEL version="1.0.0"

# Install curl for health check
RUN apk add --no-cache curl tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# Create non-root user
RUN addgroup -g 1001 -S flowx \
    && adduser -u 1001 -S flowx -G flowx

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/flowx-admin/target/*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R flowx:flowx /app

# Switch to non-root user
USER flowx

# Expose application port
EXPOSE 8080

# JVM configuration
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heapdump.hprof \
    -Djava.security.egd=file:/dev/./urandom \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Shanghai"

# Spring profiles (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
