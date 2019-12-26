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

    stage ('Stage X: Docker'){
      steps {
        step ('Stage X.1: Build') {
            def app = docker.build("znl2181/b404.be:"+env.BRANCH_NAME)
        }
        step ('Stage X.2: Publish') {
          docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
            app.push(env.BRANCH_NAME)
          }
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
