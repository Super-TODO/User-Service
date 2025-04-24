pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "user-service-image"
        DOCKER_TAG = "latest"
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/username/repository.git', branch: 'main'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ./user-service'
            }
        }

        stage('Run Docker Container') {
            steps {
                sh 'docker run -d -p 8080:8080 --name user-service ${DOCKER_IMAGE}:${DOCKER_TAG}'
            }
        }

        stage('Test User Service') {
            steps {
                sh 'curl -f http://localhost:8080/healthcheck || exit 1'
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
        }
    }
}
