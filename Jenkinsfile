pipeline {
    environment {
        registryCredential = 'docker_cred'
        userServiceImage = ''
        accountServiceImage = ''
        moneyTransferServiceImage = ''
        apiGatewayServiceImage = ''
        configurationServiceImage = ''
        discoveryServiceImage = ''
    }
    agent any
    tools {
        gradle '8.5'
    }
  stages {
    stage('for main branch') {
      when {
        branch 'main'
      }
           steps {
               sh 'gradle clean test'
           }
    }
    stages('for pull request') {
      when {
        changeRequest()
      }
      steps {
                sh 'gradle clean build'
      }
      steps {
          withSonarQubeEnv("SonarQube") {
             sh 'gradle sonar -D sonar.gradle.skipCompile=true'
      }
                  }


                  steps {
                      timeout(time: 2, unit: 'MINUTES'){
                          waitForQualityGate abortPipeline: true
                      }
                  }

                  steps {
                      script {
                      userServiceImage = docker.build("marymary88/user-service:1.0", "./user-service")
                      apiGatewayServiceImage = docker.build("marymary88/api-gateway-server:1.0", "./api-gateway-server")
                      configurationServiceImage = docker.build("marymary88/configuration-server:1.0", "./configuration-server")
                      discoveryServiceImage = docker.build("marymary88/discovery-server:1.0", "./discovery-server")
                      accountServiceImage = docker.build("marymary88/account-service:1.0", "./account-service")
                      }
                  }

                                steps {
                                    script {
                                       docker.withRegistry('', registryCredential) {
                                            userServiceImage.push()
                                            apiGatewayServiceImage.push()
                                            configurationServiceImage.push()
                                            discoveryServiceImage.push()
                                            accountServiceImage.push()
                                        }
                                    }
                                }
                                steps {
                                    sh "docker system prune -f"
                                }
    }
  }
}