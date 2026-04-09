pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Clean') {
            steps {
                bat 'mvn clean'
            }
        }

        stage('Test & Coverage') {
            steps {
                bat 'mvn clean test jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Assuming sonar settings are configured in Jenkins or via pom.
                bat 'mvn sonar:sonar'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar, target/site/jacoco/**', fingerprint: true, allowEmptyArchive: true
        }
        success {
            echo 'Spring Boot Maven build completed successfully.'
        }
        failure {
            echo 'Build failed. Check console output.'
        }
    }
}