pipeline {
    agent any
    tools {
        maven 'maven-3.8.6' 
    }
    environment {
        DATE = new Date().format('yy.M')
        TAG = "${DATE}.${BUILD_NUMBER}"
    }
    stages {
        stage('Cloning Git') {
            steps {
                git([url: 'https://github.com/roykung1127/helloworldjavalin.git', branch: 'master'])
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Docker Build') {
            steps {
                    sh 'docker build --file Dockerfile-cdns -t roykdev/helloworldjenkins:${TAG} .'
            }
        }
	    stage('Pushing Docker Image to Dockerhub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker_credential') {
                        docker.image("roykdev/helloworldjenkins:${TAG}").push()
                        docker.image("roykdev/helloworldjenkins:${TAG}").push("latest")
                    }
                }
            }
        }
        // stage('Deploy'){
        //     steps {
        //         sh "docker stop hello-world | true"
        //         sh "docker rm hello-world | true"
        //         sh "docker run --name hello-world -d -p 9004:8080 roykdev/helloworldjenkins:${TAG}"
        //     }
        // }
    }
}