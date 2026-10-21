pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK 21'
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'test'],
            description: 'Target deployment environment'
        )
        booleanParam(
            name: 'DEPLOY_TO_TOMCAT',
            defaultValue: false,
            description: 'Deploy WAR copy to local Tomcat 10.1 (Task 8)'
        )
    }

    environment {
        TOMCAT_HOME = 'C:\\Program Files\\apache-tomcat-10.1.61'
        IMAGE_NAME = 'vedanti17/telecom-tower-portal'
        IMAGE_TAG = "1.0.${BUILD_NUMBER}"
        CONTAINER_NAME = "tower-portal-${params.ENVIRONMENT}"
        HOST_PORT = "${params.ENVIRONMENT == 'test' ? '8083' : '8084'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                bat '''
                    @echo off
                    echo IMAGE_NAME=%IMAGE_NAME% > build-info.txt
                    echo IMAGE_TAG=%IMAGE_TAG% >> build-info.txt
                    echo BUILD_NUMBER=%BUILD_NUMBER% >> build-info.txt
                    echo ENVIRONMENT=%ENVIRONMENT% >> build-info.txt
                    echo HOST_PORT=%HOST_PORT% >> build-info.txt
                    type build-info.txt
                '''
                archiveArtifacts artifacts: 'build-info.txt', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%IMAGE_TAG% -t %IMAGE_NAME%:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    bat '''
                        echo %DH_PASS%| docker login -u %DH_USER% --password-stdin
                        docker push %IMAGE_NAME%:%IMAGE_TAG%
                        docker push %IMAGE_NAME%:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy Container') {
            steps {
                bat '''
                    @echo off
                    echo Inspecting existing container image for rollback tracking...
                    docker inspect --format="{{.Config.Image}}" %CONTAINER_NAME% > previous-image.txt 2>nul || echo None > previous-image.txt
                    echo Previous container image:
                    type previous-image.txt
                    echo Stopping and removing old container if present...
                    docker stop %CONTAINER_NAME% 2>nul || verify > nul
                    docker rm -f %CONTAINER_NAME% 2>nul || verify > nul
                    echo Launching fresh container %CONTAINER_NAME% on host port %HOST_PORT%...
                    docker run -d --name %CONTAINER_NAME% -p %HOST_PORT%:8080 --restart unless-stopped %IMAGE_NAME%:%IMAGE_TAG%
                '''
            }
        }

        stage('Health Check') {
            steps {
                bat '''
                    @echo off
                    set /a ATTEMPTS=0
                    :HEALTH_LOOP
                    set /a ATTEMPTS+=1
                    echo [Health Check] Polling container on port %HOST_PORT% (attempt %ATTEMPTS% of 10)...
                    curl.exe -s -f http://localhost:%HOST_PORT%/actuator/health | findstr /I "UP" > nul
                    if %ERRORLEVEL% EQU 0 (
                        echo ==========================================================
                        echo Container is UP and healthy!
                        echo Response:
                        curl.exe -s http://localhost:%HOST_PORT%/actuator/health
                        echo.
                        echo ==========================================================
                        goto :HEALTH_SUCCESS
                    )
                    if %ATTEMPTS% GEQ 10 (
                        echo ==========================================================
                        echo [ERROR] Container health check failed after 10 attempts!
                        echo Dumping container logs for %CONTAINER_NAME%:
                        docker logs %CONTAINER_NAME%
                        echo ==========================================================
                        exit /b 1
                    )
                    timeout /t 3 /nobreak > nul
                    goto :HEALTH_LOOP
                    :HEALTH_SUCCESS
                '''
            }
        }

        stage('Deploy to Tomcat') {
            when {
                expression { params.DEPLOY_TO_TOMCAT == true }
            }
            steps {
                bat 'copy /Y target\\*.war "%TOMCAT_HOME%\\webapps\\tower-portal-%ENVIRONMENT%.war"'
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo "PIPELINE & DEPLOYMENT SUCCESSFUL!"
            echo "Deployed Image: ${IMAGE_NAME}:${IMAGE_TAG}"
            echo "Container Name: ${CONTAINER_NAME}"
            echo "Application URL: http://localhost:${HOST_PORT}"
            echo "Health Endpoint: http://localhost:${HOST_PORT}/actuator/health"
            echo "=========================================================="
        }
        failure {
            echo "=========================================================="
            echo "PIPELINE OR DEPLOYMENT FAILED!"
            echo "Deployment was blocked or health check failed."
            echo "Dumping container logs for ${CONTAINER_NAME}..."
            echo "=========================================================="
            bat 'docker logs %CONTAINER_NAME% 2>nul || echo Container was not running'
        }
    }
}