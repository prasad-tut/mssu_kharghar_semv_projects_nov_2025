pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk   'JDK17'
    }

    stages {
        stage('Checkout Code') { steps { checkout scm } }
        stage('Build')        { steps { sh 'mvn clean install -DskipTests' } }
        stage('Run Tests')    { steps { sh 'mvn test' } }
        stage('Package')      { steps { sh 'mvn package' } }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy (Optional)') {
            when { expression { return fileExists('target/BookInventoryManagement-0.0.1-SNAPSHOT.jar') } }
            steps { echo 'Deployment placeholder' }
        }
    }

    post {
        success { echo 'Build Successful!' }
        failure { echo 'Build Failed!' }
    }
}
