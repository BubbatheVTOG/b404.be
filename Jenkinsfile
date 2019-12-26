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
  }

  post {
    success {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true
      sh "docker build -f Dockerfile -t znl2181/blink-404.be:"+env.BRANCH_NAME+" ."
      sh "docker push znl2181/blink-404.be:"+env.BRANCH_NAME
    }
  }
}
