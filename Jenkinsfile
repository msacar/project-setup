@Library('retter@main') _

pipeline {
    agent any

    environment {
        SHELL = '/bin/bash'
        PATH = "${env.HOME}/bin:${env.PATH}"
    }

     tools {
        nodejs "node22.8.x"  // This should match the name you gave in the Global Tool Configuration
     }

    stages {
         stage('Install Tools') {
             steps {
                 installKubectl()
                 installHelm()
                 configureKubectlForK8s()
             }
         }

         stage('Install Deployer Dependencies') {
             steps {
                 dir('deployer') {
                     sh 'ls -all -h'
                     sh 'npm -v'
                     sh 'pnpm -v'
                     sh 'pnpm install --verbose --no-frozen-lockfile'
                 }
             }
         }

        stage('Run script') {
            steps {
                script {
                    dir('deployer') {
                        def secretValue = sh(script: "kubectl get secret mongo-secrets -o jsonpath='{.data.MONGO_CONNECTION_STRING}'", returnStdout: true).trim()
                        def decodedValue = sh(script: "echo ${secretValue} | base64 --decode", returnStdout: true).trim()

                        nodejs(nodeJSInstallationName: 'node22.8.x') {
                            withEnv(["MONGO_CONNECTION_STRING=${decodedValue}"]) {
                            sh """
                                ts-node ./projects.ts
                            """
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Deployment of version ${params.VERSION} to ${params.STAGE} successful!"
            // Add any post-deployment tasks or notifications
        }
        failure {
            echo "Deployment of version ${params.VERSION} to ${params.STAGE} failed!"
            // Add any failure handling or notifications
        }
        always {
            echo " always !"

        }
    }
}
