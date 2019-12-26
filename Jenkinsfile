pipeline {

  agent any

  stages {

    stage ('Stage 1: Get Latest Code') {
      steps {
        step([$class: 'WsCleanup'])
        checkout scm
      }
    }

    stage ('Stage 2: Build Software') {
      steps {
        withMaven {
          sh "mvn -v"
          sh "mvn clean compile test verify package"
        }
      }
    }

    stage ('Stage 3: Build Docker Image'){
      steps {
        app = docker.build("znl2181/b404.be:"+env.BRANCH_NAME)
      }
    }

    stage ('Stage 4: Publish Docker Image'){
      steps {
        docker.withRegistry("https://registry.hub.docker.com", "dockerhub") {
          app.push(env.BRANCH_NAME)
        }
      }
    }
  }

  post {
    success {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
    }
  }
}
