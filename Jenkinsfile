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

    post {
      always {
        archiveArtifacts artifacts: 'target/*.war', fingerprint: true
        # junit 'build/reports/**/*.xml'
      }
    }
  }
}
