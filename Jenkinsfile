// ============================================================================
// FlowX Enterprise SaaS Platform - Jenkinsfile
// Declarative Pipeline
// ============================================================================

pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    environment {
        // Docker
        DOCKER_REGISTRY = 'registry.flowx.com'
        DOCKER_IMAGE    = 'flowx/flowx-platform'
        DOCKER_TAG      = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(8) ?: 'unknown'}"

        // Maven
        MAVEN_OPTS = '-Xms512m -Xmx1024m'

        // SonarQube (optional)
        // SONAR_SERVER = 'http://sonar.flowx.com'
    }

    options {
        // Build timeout
        timeout(time: 30, unit: 'MINUTES')
        // Timestamps in console
        timestamps()
        // Disable concurrent builds
        disableConcurrentBuilds()
        // Keep last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        // ------------------------------------------------------------------
        // Stage 1: Checkout
        // ------------------------------------------------------------------
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                script {
                    env.GIT_COMMIT_MSG = sh(
                        script: 'git log -1 --pretty=%B',
                        returnStdout: true
                    ).trim()
                    echo "Commit: ${env.GIT_COMMIT}"
                    echo "Message: ${env.GIT_COMMIT_MSG}"
                }
            }
        }

        // ------------------------------------------------------------------
        // Stage 2: Build
        // ------------------------------------------------------------------
        stage('Build') {
            steps {
                echo 'Building FlowX Platform...'
                sh 'mvn clean package -DskipTests -B -T 1C'
            }
        }

        // ------------------------------------------------------------------
        // Stage 3: Test
        // ------------------------------------------------------------------
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test -B'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit allowEmptyResults: true,
                         testResults: '**/target/surefire-reports/*.xml'
                }
                failure {
                    echo 'Tests failed! Check the test reports for details.'
                }
            }
        }

        // ------------------------------------------------------------------
        // Stage 4: Code Quality (Optional)
        // ------------------------------------------------------------------
        // stage('SonarQube Analysis') {
        //     steps {
        //         withSonarQubeEnv('SonarQube') {
        //             sh 'mvn sonar:sonar -B'
        //         }
        //     }
        // }

        // ------------------------------------------------------------------
        // Stage 5: Docker Build
        // ------------------------------------------------------------------
        stage('Docker Build') {
            steps {
                echo "Building Docker image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                """
            }
        }

        // ------------------------------------------------------------------
        // Stage 6: Docker Push
        // ------------------------------------------------------------------
        stage('Docker Push') {
            when {
                branch 'main'
            }
            steps {
                echo "Pushing Docker image to registry..."
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'flowx-docker-credentials') {
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }

        // ------------------------------------------------------------------
        // Stage 7: Deploy
        // ------------------------------------------------------------------
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo 'Deploying to target environment...'
                // Placeholder: Replace with actual deployment logic
                // Example: SSH to server, docker-compose pull & up
                // sh """
                //     ssh deployer@flowx-prod-server "
                //         cd /opt/flowx && \
                //         docker-compose pull flowx-app && \
                //         docker-compose up -d flowx-app
                //     "
                // """
                echo 'Deployment stage is a placeholder. Configure as needed.'
            }
        }
    }

    // --------------------------------------------------------------------------
    // Post Actions
    // --------------------------------------------------------------------------
    post {
        always {
            echo 'Pipeline finished. Cleaning workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
            // Optional: Send success notification
            // slackSend(
            //     color: 'good',
            //     message: "FlowX Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n${env.BUILD_URL}"
            // )
        }
        failure {
            echo 'Pipeline failed!'
            // Optional: Send failure notification
            // slackSend(
            //     color: 'danger',
            //     message: "FlowX Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n${env.BUILD_URL}"
            // )
            // emailext(
            //     subject: "FlowX Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     body: "Check: ${env.BUILD_URL}",
            //     to: 'team@flowx.com'
            // )
        }
        unstable {
            echo 'Pipeline is unstable (test failures).'
        }
    }
}
