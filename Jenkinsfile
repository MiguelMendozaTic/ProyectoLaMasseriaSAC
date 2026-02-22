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
                    echo '🔨 INICIANDO COMPILACIÓN AUTOMÁTICA'
                    echo '========================================'
                    
                    // Mostrar información del entorno
                    sh 'java -version'
                    sh 'mvn -version'
                    
                    echo 'Limpiando compilaciones anteriores...'
                    sh 'mvn clean -B -ntp'
                    
                    echo 'Compilando código fuente...'
                    sh 'mvn compile -B -ntp'
                }
            }
            post {
                success {
                    echo 'COMPILACIÓN EXITOSA'
                    echo 'El código se compiló correctamente'
                }
                failure {
                    echo 'ERROR DE COMPILACIÓN'
                    echo 'Revisa los logs para más detalles'
                }
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
            echo 'Pipeline completado con éxito'
        }
        failure {
            echo 'Pipeline falló'
        }
    }
}
