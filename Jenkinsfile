pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        APP_NAME = 'store-0.0.1-SNAPSHOT.jar'
        DEPLOY_DIR = 'C:\\deployment\\inventory-store'
    }

    triggers {
        githubPush()
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

        stage('Clean and Test') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('Package and Coverage') {
            steps {
                bat 'mvn verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sonar-token-new', variable: 'SONAR_TOKEN')]) {
                        bat 'mvn sonar:sonar -Dsonar.projectKey=inventory-store -Dsonar.projectName=inventory-store -Dsonar.token=%sqa_6af2c49e9217ed43ccf35acf2889145fbaf6e299%'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 20, unit: 'MINUTES') {
                    script {
                        def qualityGate = waitForQualityGate()
                        if (qualityGate.status != 'OK') {
                            error "Pipeline stopped because Quality Gate failed: ${qualityGate.status}"
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
                    copy /Y "target\\%APP_NAME%" "%DEPLOY_DIR%\\%APP_NAME%"
                '''

                bat '''
                    powershell -Command "Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -match '%APP_NAME%' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }"
                '''

                bat '''
                    cd /d "%DEPLOY_DIR%"
                    start /B java -jar "%APP_NAME%" --server.port=8081
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
        }

        success {
            echo 'Build, test, coverage, SonarQube analysis, quality gate, and deployment completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check Jenkins console output and SonarQube results.'
        }

        aborted {
            echo 'Pipeline was aborted. Check webhook, quality gate, or timeout configuration.'
        }
    }
}