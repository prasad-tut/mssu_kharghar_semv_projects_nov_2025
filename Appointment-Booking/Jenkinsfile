pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }
    
    environment {
        // Application settings
        APP_NAME = 'appointment-management'
        
        // AWS settings
        AWS_REGION = 'us-east-1'
        AWS_ACCOUNT_ID = credentials('aws-account-id')
        ECR_REPO_NAME = 'appointment-management'
        
        // Docker settings
        DOCKER_IMAGE = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}"
        
        // Build info
        BUILD_VERSION = "${env.BUILD_NUMBER}"
        GIT_COMMIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '========================================='
                echo 'Stage 1: Checking out code from Git'
                echo '========================================='
                checkout scm
                sh 'git log -1'
            }
        }
        
        stage('Build') {
            steps {
                echo '========================================='
                echo 'Stage 2: Building application with Maven'
                echo '========================================='
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo '========================================='
                echo 'Stage 3: Running unit tests'
                echo '========================================='
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    echo 'Test results published'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '========================================='
                echo 'Stage 4: Packaging application'
                echo '========================================='
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                echo '========================================='
                echo 'Stage 5: Running code quality checks'
                echo '========================================='
                sh 'mvn verify'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '========================================='
                echo 'Stage 6: Building Docker image'
                echo '========================================='
                script {
                    docker.build("${DOCKER_IMAGE}:${BUILD_VERSION}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }
        
        stage('Push to ECR') {
            steps {
                echo '========================================='
                echo 'Stage 7: Pushing Docker image to AWS ECR'
                echo '========================================='
                script {
                    withAWS(credentials: 'aws-credentials', region: "${AWS_REGION}") {
                        sh """
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                            docker push ${DOCKER_IMAGE}:${BUILD_VERSION}
                            docker push ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
        
        stage('Deploy to AWS ECS') {
            steps {
                echo '========================================='
                echo 'Stage 8: Deploying to AWS ECS'
                echo '========================================='
                script {
                    withAWS(credentials: 'aws-credentials', region: "${AWS_REGION}") {
                        sh """
                            aws ecs update-service \
                                --cluster appointment-cluster \
                                --service appointment-service \
                                --force-new-deployment \
                                --region ${AWS_REGION}
                        """
                    }
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo '========================================='
                echo 'Stage 9: Performing health check'
                echo '========================================='
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    // Add your health check endpoint here
                    sh '''
                        echo "Waiting for application to be ready..."
                        # Add actual health check when deployed
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '✓ Pipeline completed successfully!'
            echo '========================================='
            echo "Build: ${BUILD_VERSION}"
            echo "Commit: ${GIT_COMMIT_SHORT}"
            echo "Docker Image: ${DOCKER_IMAGE}:${BUILD_VERSION}"
            
            // Send notification (configure email/Slack)
            // emailext (
            //     subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body: "Build succeeded: ${env.BUILD_URL}",
            //     to: "team@example.com"
            // )
        }
        
        failure {
            echo '========================================='
            echo '✗ Pipeline failed!'
            echo '========================================='
            
            // Send notification (configure email/Slack)
            // emailext (
            //     subject: "FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body: "Build failed: ${env.BUILD_URL}",
            //     to: "team@example.com"
            // )
        }
        
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
