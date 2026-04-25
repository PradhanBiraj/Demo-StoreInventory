pipeline {
    agent any

    tools {
        jdk   'JDK17'
        maven 'Maven3'
    }

    environment {
        REPO_URL           = "https://github.com/PradhanBiraj/Demo-StoreInventory.git"
        APP_DIR            = "."
        JAR_NAME           = "store-0.0.1-SNAPSHOT.jar"
        SONAR_PROJECT_KEY  = "inventory-store"
        SONAR_PROJECT_NAME = "Inventory Store"
        DEPLOY_PATH        = "C:\\deployment\\inventory-store"
        PORT               = "8081"
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'week-6-security',
                    credentialsId: 'OBS-token',
                    url: "${REPO_URL}"
            }
        }

        stage('Build') {
            steps {
                dir("${APP_DIR}") {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                dir("${APP_DIR}") {
                    bat 'mvn test'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    dir("${APP_DIR}") {
                        bat '''
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=%SONAR_PROJECT_KEY% ^
                        -Dsonar.projectName="%SONAR_PROJECT_NAME%" ^
                        -Dsonar.java.binaries=target/classes
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy Prod') {
            steps {
                bat """
                    if not exist ${DEPLOY_PATH} mkdir ${DEPLOY_PATH}
                    copy /Y target\\${JAR_NAME} ${DEPLOY_PATH}
                """
                bat """
                    powershell -Command "Get-CimInstance Win32_Process | Where-Object { \$_.Name -eq 'java.exe' -and \$_.CommandLine -match '${JAR_NAME}' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force }"
                """
                bat """
                    cd /d ${DEPLOY_PATH}
                    start "inventory-store" java -jar ${JAR_NAME} --server.port=${PORT}
                """
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
        }

        success  { echo "Pipeline passed - Build: #${env.BUILD_NUMBER}" }
        failure  { echo "Pipeline failed - Build: #${env.BUILD_NUMBER}" }
        unstable { echo "Pipeline unstable - deployment skipped" }
    }
}