pipeline {
    agent any
    
    tools {
        maven 'Maven3.9.12'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Obteniendo código desde GitHub...'
                checkout scm
            }
        }
        
        stage('Compile') {
            steps {
                echo 'Compilando el proyecto...'
                sh 'mvn clean compile -B -ntp'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Ejecutando pruebas...'
                sh 'mvn test -B -ntp'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Empaquetando aplicación...'
                sh 'mvn package -DskipTests -B -ntp'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline finalizado'
            cleanWs()
        }
        success {
            echo '🎉 Pipeline completado con éxito'
        }
        failure {
            echo '❌ Pipeline falló'
        }
    }
}
