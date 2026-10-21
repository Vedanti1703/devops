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
            description: 'Target environment'
        )
    }

    environment {
        TOMCAT_HOME = 'C:\\Program Files\\apache-tomcat-10.1.61'
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
            }
        }

        stage('Deploy') {
            steps {
                bat 'copy /Y target\\*.war "%TOMCAT_HOME%\\webapps\\tower-portal-%ENVIRONMENT%.war"'
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded! Successfully deployed to ${params.ENVIRONMENT} environment at http://localhost:8080/tower-portal-${params.ENVIRONMENT}"
        }
        failure {
            echo 'Pipeline failed - deployment blocked'
        }
    }
}