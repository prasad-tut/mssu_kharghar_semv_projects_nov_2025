pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-docker-registry'
        AWS_REGION = 'us-east-1'
        ECR_REPO = 'bill-management'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Backend') {
            steps {
                dir('bill_management') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                dir('bill_management') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'bill_management/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir('bill_management') {
                    sh """
                        docker build -t ${ECR_REPO}:${IMAGE_TAG} .
                        docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REPO}:latest
                    """
                }
            }
        }
        
        stage('Push to ECR') {
            steps {
                script {
                    sh """
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${DOCKER_REGISTRY}
                        docker tag ${ECR_REPO}:${IMAGE_TAG} ${DOCKER_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                        docker tag ${ECR_REPO}:${IMAGE_TAG} ${DOCKER_REGISTRY}/${ECR_REPO}:latest
                        docker push ${DOCKER_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                        docker push ${DOCKER_REGISTRY}/${ECR_REPO}:latest
                    """
                }
            }
        }
        
        stage('Deploy to AWS') {
            steps {
                script {
                    // Deploy using ECS, EKS, or EC2
                    sh """
                        # Update ECS service or deploy to EC2
                        aws ecs update-service --cluster bill-management-cluster --service bill-management-service --force-new-deployment --region ${AWS_REGION}
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            cleanWs()
        }
    }
}
