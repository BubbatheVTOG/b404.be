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
            sh "mvn clean build test verify package"
        }
      }
    }
  }
}
