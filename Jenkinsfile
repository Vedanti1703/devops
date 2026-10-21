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
    }
}