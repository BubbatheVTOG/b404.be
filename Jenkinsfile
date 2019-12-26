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
      stages {
        stage ('Stage X.1: Build') {
          steps {
            def app = docker.build("znl2181/b404.be:"+env.BRANCH_NAME)
          }
        }

        stage ('Stage X.2: Publish') {
          steps {
            docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
              app.push(env.BRANCH_NAME)
            }
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
