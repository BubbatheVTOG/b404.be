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

    stage ('Stage 3: Build and Publish Docker Image'){
      stages {
        stage ("When on Designated Branch") {
          when {
            anyOf{
              branch 'master'
              branch 'testing'
              branch 'dev'
              branch 'debug'
            }
          }
          steps {
            script {
              docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                app = docker.build("znl2181/b404.be:"+env.BRANCH_NAME)
                app.push()
              }
            }
          }
        }
      }
    }

    stage('Stage 4: SonarQube analysis') {
      stages {
        stage ("When on Designated Branch") {
          when {
            anyOf {
              branch 'dev'
            }
          }
          steps {
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
              withSonarQubeEnv(installationName: 'sonar.b404') {
                sh 'mvn sonar:sonar'
              }
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
