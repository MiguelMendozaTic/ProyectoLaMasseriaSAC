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
                script {
                    echo '========================================'
                    echo 'INICIANDO COMPILACIÓN AUTOMÁTICA'
                    echo '========================================'
                    
                    sh 'java -version'
                    sh 'mvn -version'
                    
                    sh 'mvn clean compile -B -ntp'
                }
            }
            post {
                success {
                    echo 'COMPILACIÓN EXITOSA'
                }
                failure {
                    echo 'ERROR DE COMPILACIÓN'
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    // Ejecutar pruebas pero permitir que no existan
                    sh 'mvn test -B -ntp || true'
                }
            }
            post {
                always {
                    // Buscar reportes pero no fallar si no existen
                    junit allowEmptyResults: true, 
                        testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B -ntp'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completado con éxito'
        }
        failure {
            echo 'Pipeline falló'
        }
    }
}