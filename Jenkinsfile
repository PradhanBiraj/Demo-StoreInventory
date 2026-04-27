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

        JENKINS_URL        = "http://localhost:8080"
        REPORT_DIR         = "monitoring-reports"
    }

//Test pipeline automation
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
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
                    bat 'mvn verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
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

        stage('Monitoring Init') {
            steps {
                script {
                    def tsOutput = powershell(
                        script: '''
                            [string](Get-Date -Format "yyyy-MM-dd_HH-mm-ss")
                        ''',
                        returnStdout: true
                    ).trim()

                    env.REPORT_TIMESTAMP = tsOutput

                    bat """
                        if not exist "${REPORT_DIR}" mkdir "${REPORT_DIR}"
                    """

                    echo "Monitoring report directory: ${REPORT_DIR}"
                    echo "Monitoring timestamp: ${env.REPORT_TIMESTAMP}"
                    echo "Workspace: ${WORKSPACE}"
                }
            }
        }

        stage('Collect Jenkins Metrics') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    script {
                        echo "Collecting Jenkins Metrics Plugin data..."

                        withCredentials([string(credentialsId: 'METRICS_ACCESS_KEY', variable: 'METRICS_KEY')]) {

                            def metricsJson = powershell(
                                script: '''
                                    $url = "$env:JENKINS_URL/metrics/$env:METRICS_KEY/metrics"

                                    try {
                                        Write-Host "Fetching Jenkins metrics..."
                                        $response = Invoke-RestMethod -Uri $url -Method Get -UseBasicParsing -ErrorAction Stop
                                        $response | ConvertTo-Json -Depth 10
                                    } catch {
                                        Write-Error "Failed to collect Jenkins metrics: $_"
                                        exit 1
                                    }
                                ''',
                                returnStdout: true
                            ).trim()

                            if (!metricsJson || metricsJson.isEmpty()) {
                                echo "Metrics endpoint returned an empty response."

                                env.M_EXECUTOR_TOTAL = 'N/A'
                                env.M_EXECUTOR_FREE  = 'N/A'
                                env.M_EXECUTOR_USED  = 'N/A'
                                env.M_QUEUE          = 'N/A'
                                env.M_JOBS           = 'N/A'
                                env.M_NODES          = 'N/A'
                                env.M_HEAP_PCT       = 'N/A'
                                env.M_CPU            = 'N/A'

                                return
                            }

                            def metrics = readJSON text: metricsJson

                            def executorTotal = metrics.gauges?.get('jenkins.executor.count.value')?.value ?: 'N/A'
                            def executorFree  = metrics.gauges?.get('jenkins.executor.free.value')?.value ?: 'N/A'
                            def executorUsed  = metrics.gauges?.get('jenkins.executor.in-use.value')?.value ?: 'N/A'

                            def queueSize = metrics.gauges?.get('jenkins.queue.size.value')?.value
                            if (queueSize == null) {
                                queueSize = 'N/A'
                            }

                            def jobCount   = metrics.gauges?.get('jenkins.job.count.value')?.value ?: 'N/A'
                            def nodeOnline = metrics.gauges?.get('jenkins.node.online.value')?.value ?: 'N/A'
                            def nodeTotal  = metrics.gauges?.get('jenkins.node.count.value')?.value ?: 'N/A'
                            def heapUsed   = metrics.gauges?.get('vm.memory.heap.used')?.value ?: 'N/A'
                            def heapMax    = metrics.gauges?.get('vm.memory.heap.max')?.value ?: 'N/A'
                            def cpuLoad    = metrics.gauges?.get('vm.cpu.load')?.value ?: 'N/A'

                            def heapPct = 'N/A'

                            if (heapUsed != 'N/A' && heapMax != 'N/A') {
                                try {
                                    def percentage = ((heapUsed as Double) / (heapMax as Double) * 100)
                                    heapPct = (Math.round(percentage * 100) / 100.0).toString()
                                } catch (e) {
                                    heapPct = 'N/A'
                                }
                            }

                            echo """
==================================================
JENKINS METRICS SNAPSHOT
==================================================
Executors Used / Total : ${executorUsed}/${executorTotal}
Executors Free        : ${executorFree}
Build Queue Size      : ${queueSize}
Total Jenkins Jobs    : ${jobCount}
Nodes Online / Total   : ${nodeOnline}/${nodeTotal}
Heap Memory Used      : ${heapPct}%
CPU Load              : ${cpuLoad}
==================================================
"""

                            if (executorFree != 'N/A') {
                                try {
                                    if ((executorFree as Integer) == 0) {
                                        unstable("All Jenkins executors are busy.")
                                    }
                                } catch (e) { }
                            }

                            if (queueSize != 'N/A') {
                                try {
                                    if ((queueSize as Integer) > 10) {
                                        unstable("Jenkins queue is high: ${queueSize} jobs waiting.")
                                    }
                                } catch (e) { }
                            }

                            if (heapPct != 'N/A') {
                                try {
                                    if ((heapPct as Double) > 85) {
                                        unstable("Jenkins heap memory is above 85%: ${heapPct}%.")
                                    }
                                } catch (e) { }
                            }

                            writeFile file: "${REPORT_DIR}/metrics_${env.REPORT_TIMESTAMP}.json",
                                      text: metricsJson

                            env.M_EXECUTOR_TOTAL = executorTotal.toString()
                            env.M_EXECUTOR_FREE  = executorFree.toString()
                            env.M_EXECUTOR_USED  = executorUsed.toString()
                            env.M_QUEUE          = queueSize.toString()
                            env.M_JOBS           = jobCount.toString()
                            env.M_NODES          = "${nodeOnline}/${nodeTotal}"
                            env.M_HEAP_PCT       = heapPct.toString()
                            env.M_CPU            = cpuLoad.toString()
                        }
                    }
                }
            }
        }

        stage('Collect Disk Usage') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    script {
                        echo "Collecting Jenkins disk usage data..."

                        def diskInfo = powershell(
                            script: '''
                                $jenkinsHome = $env:JENKINS_HOME

                                if (-not $jenkinsHome) {
                                    $jenkinsHome = "$env:USERPROFILE\\.jenkins"
                                }

                                $homeSizeBytes = 0

                                if (Test-Path $jenkinsHome) {
                                    $homeSizeBytes = (Get-ChildItem $jenkinsHome -Recurse -ErrorAction SilentlyContinue |
                                        Measure-Object -Property Length -Sum).Sum
                                }

                                $homeSizeMB = [math]::Round([double]$homeSizeBytes / 1MB, 2)

                                $wsPath = Join-Path $jenkinsHome "workspace"
                                $wsSize = 0

                                if (Test-Path $wsPath) {
                                    $wsSizeBytes = (Get-ChildItem $wsPath -Recurse -ErrorAction SilentlyContinue |
                                        Measure-Object -Property Length -Sum).Sum
                                    $wsSize = [math]::Round([double]$wsSizeBytes / 1MB, 2)
                                }

                                $jobsPath = Join-Path $jenkinsHome "jobs"
                                $jobsSize = 0

                                if (Test-Path $jobsPath) {
                                    $jobsSizeBytes = (Get-ChildItem $jobsPath -Recurse -ErrorAction SilentlyContinue |
                                        Measure-Object -Property Length -Sum).Sum
                                    $jobsSize = [math]::Round([double]$jobsSizeBytes / 1MB, 2)
                                }

                                $drive = Split-Path -Qualifier $jenkinsHome
                                $driveLetter = $drive.TrimEnd(':')
                                $disk = Get-PSDrive $driveLetter -ErrorAction SilentlyContinue

                                $freeGB = [math]::Round([double]$disk.Free / 1GB, 2)
                                $usedGB = [math]::Round([double]$disk.Used / 1GB, 2)
                                $totalGB = $freeGB + $usedGB

                                if ($totalGB -gt 0) {
                                    $usedPct = [math]::Round(($usedGB / $totalGB) * 100, 1)
                                } else {
                                    $usedPct = 0
                                }

                                Write-Output "HOME_MB=$homeSizeMB"
                                Write-Output "WS_MB=$wsSize"
                                Write-Output "JOBS_MB=$jobsSize"
                                Write-Output "DISK_FREE_GB=$freeGB"
                                Write-Output "DISK_USED_PCT=$usedPct"
                            ''',
                            returnStdout: true
                        ).trim()

                        def diskData = [:]

                        diskInfo.split('\n').each { line ->
                            def parts = line.trim().split('=', 2)
                            if (parts.size() == 2) {
                                diskData[parts[0].trim()] = parts[1].trim()
                            }
                        }

                        def homeSize = diskData.HOME_MB ?: 'N/A'
                        def wsSize   = diskData.WS_MB ?: 'N/A'
                        def jobSize  = diskData.JOBS_MB ?: 'N/A'
                        def diskFree = diskData.DISK_FREE_GB ?: 'N/A'
                        def diskUsed = diskData.DISK_USED_PCT ?: 'N/A'

                        echo """
==================================================
DISK USAGE SNAPSHOT
==================================================
Jenkins Home Size : ${homeSize} MB
Jobs Folder Size  : ${jobSize} MB
Workspace Size    : ${wsSize} MB
Disk Free Space   : ${diskFree} GB
Disk Used         : ${diskUsed}%
==================================================
"""

                        if (diskUsed != 'N/A') {
                            try {
                                def usedPct = diskUsed.replace('%', '').toDouble()

                                if (usedPct > 80) {
                                    unstable("Disk usage is above 80%: ${diskUsed}%.")
                                }
                            } catch (e) { }
                        }

                        writeFile file: "${REPORT_DIR}/disk_${env.REPORT_TIMESTAMP}.txt",
                                  text: diskInfo

                        env.D_HOME = "${homeSize} MB"
                        env.D_WS   = "${wsSize} MB"
                        env.D_JOBS = "${jobSize} MB"
                        env.D_FREE = "${diskFree} GB"
                        env.D_USED = diskUsed
                    }
                }
            }
        }

        stage('Collect JVM Stats') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    script {
                        echo "Collecting Jenkins JVM process data..."

                        def jvmStats = powershell(
                            script: '''
                                $jenkinsProc = $null

                                $jenkinsProc = Get-Process -Name "java" -ErrorAction SilentlyContinue |
                                    Where-Object { $_.MainWindowTitle -like "*jenkins*" } |
                                    Select-Object -First 1

                                if (-not $jenkinsProc) {
                                    $jenkinsProc = Get-Process -Name "java" -ErrorAction SilentlyContinue |
                                        Select-Object -First 1
                                }

                                if ($jenkinsProc) {
                                    $javaMemMB = [math]::Round([double]$jenkinsProc.WorkingSet64 / 1MB, 2)
                                    $javaCpuSec = [math]::Round($jenkinsProc.CPU, 2)
                                    $javaThreads = $jenkinsProc.Threads.Count
                                    $javaProcessId = $jenkinsProc.Id

                                    Write-Output "JAVA_PID=$javaProcessId"
                                    Write-Output "JAVA_MEM_MB=$javaMemMB"
                                    Write-Output "JAVA_CPU_SEC=$javaCpuSec"
                                    Write-Output "JAVA_THREADS=$javaThreads"
                                } else {
                                    Write-Output "JAVA_PID=NOT_FOUND"
                                    Write-Output "JAVA_MEM_MB=N/A"
                                    Write-Output "JAVA_CPU_SEC=N/A"
                                    Write-Output "JAVA_THREADS=N/A"
                                }
                            ''',
                            returnStdout: true
                        ).trim()

                        def jvmData = [:]

                        jvmStats.split('\n').each { line ->
                            def parts = line.trim().split('=', 2)
                            if (parts.size() == 2) {
                                jvmData[parts[0].trim()] = parts[1].trim()
                            }
                        }

                        def procId  = jvmData.JAVA_PID ?: 'N/A'
                        def procMem = jvmData.JAVA_MEM_MB ?: 'N/A'
                        def procCpu = jvmData.JAVA_CPU_SEC ?: 'N/A'
                        def procThr = jvmData.JAVA_THREADS ?: 'N/A'

                        echo """
==================================================
JVM MONITORING SNAPSHOT
==================================================
Jenkins Process ID  : ${procId}
Working Set Memory  : ${procMem} MB
CPU Time            : ${procCpu} seconds
Thread Count        : ${procThr}
==================================================
"""

                        writeFile file: "${REPORT_DIR}/jvm_${env.REPORT_TIMESTAMP}.txt",
                                  text: jvmStats

                        env.J_PID     = procId
                        env.J_MEM     = "${procMem} MB"
                        env.J_CPU     = procCpu
                        env.J_THREADS = procThr
                    }
                }
            }
        }

        stage('Generate Monitoring Report') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    script {
                        def ts = env.REPORT_TIMESTAMP ?: 'unknown-time'

                        def htmlReport = """<!DOCTYPE html>
<html>
<head>
    <title>Inventory Store Jenkins Monitoring Report</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f4f4;
            color: #222;
            padding: 30px;
        }

        h2 {
            color: #1f4e79;
        }

        h3 {
            color: #333;
            margin-top: 25px;
        }

        table {
            border-collapse: collapse;
            width: 80%;
            background: white;
            margin-top: 10px;
            margin-bottom: 25px;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #1f4e79;
            color: white;
        }

        .status {
            font-weight: bold;
        }
    </style>
</head>
<body>
    <h2>Inventory Store Jenkins Monitoring Report</h2>

    <p><b>Generated:</b> ${ts}</p>
    <p><b>Build Number:</b> #${BUILD_NUMBER}</p>
    <p><b>Build Status:</b> <span class="status">${currentBuild.currentResult}</span></p>

    <h3>Executor and Queue Metrics</h3>
    <table>
        <tr>
            <th>Metric</th>
            <th>Value</th>
        </tr>
        <tr>
            <td>Executors Used / Total</td>
            <td>${env.M_EXECUTOR_USED ?: 'N/A'} / ${env.M_EXECUTOR_TOTAL ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Executors Free</td>
            <td>${env.M_EXECUTOR_FREE ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Build Queue Size</td>
            <td>${env.M_QUEUE ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Total Jenkins Jobs</td>
            <td>${env.M_JOBS ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Agent Nodes Online / Total</td>
            <td>${env.M_NODES ?: 'N/A'}</td>
        </tr>
    </table>

    <h3>JVM and Memory Health</h3>
    <table>
        <tr>
            <th>Metric</th>
            <th>Value</th>
        </tr>
        <tr>
            <td>Heap Memory Usage</td>
            <td>${env.M_HEAP_PCT ?: 'N/A'}%</td>
        </tr>
        <tr>
            <td>CPU Load</td>
            <td>${env.M_CPU ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>JVM Working Set</td>
            <td>${env.J_MEM ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>JVM CPU Time</td>
            <td>${env.J_CPU ?: 'N/A'} seconds</td>
        </tr>
        <tr>
            <td>JVM Thread Count</td>
            <td>${env.J_THREADS ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Jenkins Process ID</td>
            <td>${env.J_PID ?: 'N/A'}</td>
        </tr>
    </table>

    <h3>Disk Health</h3>
    <table>
        <tr>
            <th>Metric</th>
            <th>Value</th>
        </tr>
        <tr>
            <td>Jenkins Home Size</td>
            <td>${env.D_HOME ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Jobs Directory Size</td>
            <td>${env.D_JOBS ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Workspace Size</td>
            <td>${env.D_WS ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Disk Free Space</td>
            <td>${env.D_FREE ?: 'N/A'}</td>
        </tr>
        <tr>
            <td>Disk Used Percentage</td>
            <td>${env.D_USED ?: 'N/A'}%</td>
        </tr>
    </table>

    <p>
        This report was generated automatically after the Inventory Store CI/CD pipeline execution.
        It summarizes Jenkins executor status, queue status, JVM health, CPU load, and disk usage.
    </p>
</body>
</html>"""

                        writeFile file: "${REPORT_DIR}/report_${ts}.html",
                                  text: htmlReport

                        echo "Monitoring HTML report generated: ${REPORT_DIR}/report_${ts}.html"
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            archiveArtifacts artifacts: 'target/*.jar',
                             fingerprint: true,
                             allowEmptyArchive: true

            archiveArtifacts artifacts: 'target/site/jacoco/**/*',
                             allowEmptyArchive: true

            archiveArtifacts artifacts: 'monitoring-reports/**/*',
                             allowEmptyArchive: true

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'monitoring-reports',
                reportFiles: 'report_*.html',
                reportName: 'Monitoring Report'
            ])
        }

        success {
            echo "Pipeline passed with monitoring data collected - Build: #${env.BUILD_NUMBER}"
        }

        failure {
            echo "Pipeline failed - Build: #${env.BUILD_NUMBER}"
            echo "Check build logs, SonarQube quality gate, Metrics credential, and monitoring plugin setup."
        }

        unstable {
            echo "Pipeline unstable - check monitoring thresholds and Monitoring Report."
        }
    }
}