pipeline {
    agent any
    tools {
        jdk 'JDK-25' 
        maven 'Maven-3.6.3' 
    }
    environment{
        DOCKER_REGISTRY = "docker-registry"
        DOCKER_REPO = "docker-repo"
        IMAGE_TAG = "latest"
        PROJECT_NAME = "project-name"
        SLACK_CHANNEL = "#slack-channel"
        EMAIL_TO = "email@example.com"
        IS_PR = "${env.CHANGE_ID ?:''}"
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timestamps()
        ansiColor('xterm')
        disableConcurrentBuilds()
    }
    stages {
        stage('Checkout') {
            steps{
                if (env.BRANCH_NAME == 'main') {
                    checkout scm
                } else {
                    git branch: 'main', url:'https://github.com/repo/project.git'
            }
        }
        Script{
            def shortsha = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
            env.IMAGE_TAG = "${env.BUILD_NUMBER}-${shortsha}"
        }
        }
    
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        Stage('Integration Tests') {
            when {
                expression { return env.IS_PR == '' }
            }
            steps {
                sh 'mvn verify -Pintegration-tests'
            }
            post {
                always {
                    junit 'target/failsafe-reports/*.xml'
                }
            }
        }
        Stage('Dependency Vulnerability Scan') {
            when {
                expression { return env.IS_PR == '' }
            }
            steps {
                sh 'mvn dependency-check:check'
            }
            post {
                always {
                    dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
                }
            }
        }
        stage('Docker Build & Push') {
            when{
                allof{
                    branch 'main'
                    not{changeRequest()}
                    }
                }
            steps {
                withcredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script{
                        def imageFull = "${DOCKER_REGISTRY}/${DOCKER_REPO}/${PROJECT_NAME}:${IMAGE_TAG}"
                        def imageLatest = "${DOCKER_REGISTRY}/${DOCKER_REPO}/${PROJECT_NAME}:latest"
                        sh """
                        echo Logging into ${DOCKER_REGISTRY}....
                        echo '${DOCKER_PASS}' | docker login -u '${DOCKER_USER}' --password-stdin ${DOCKER_REGISTRY}
                        echo Building Docker image ${imageFull}....
                        docker build -t ${imageFull} -t ${imageLatest} .
                        echo Pushing Docker image ${imageFull}....
                        docker push ${imageFull}
                        docker push ${imageLatest}
                        """
                    }

                }
            }
        }
        stage('Container vulnerability Scan') {
            when {
                allof{
                    branch 'main'
                    not{changeRequest()}
                    }
                }
            steps {
                withcredentials([string(credentialsId: 'trivy-token', variable: 'TRIVY_TOKEN')]) {
                    script{
                        def imageFull = "${DOCKER_REGISTRY}/${DOCKER_REPO}/${PROJECT_NAME}:${IMAGE_TAG}"
                        sh """
                        echo Scanning Docker image ${imageFull} for vulnerabilities....
                        trivy image --exit-code 1 --severity HIGH,CRITICAL --token ${TRIVY_TOKEN} ${imageFull}
                        """
                    }
                }
            }
            post{
                always{
                    archiveArtifacts artifacts: 'trivy-report.txt', fingerprint: true, allowEmptyArchive: true
                }
            }
        }
        stage('Deploy to Staging') {
            when {
                allof{
                    branch 'main'
                    not{changeRequest()}
                    }
                }
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-staging', variable: 'KUBECONFIG')]) {
                    sh'''
                    echo Deploying to Staging environment...
                    kublectl --kubeconfig="$KUBECONFIG" apply -f k8s/staging/
                    kubectl --kubeconfig="$KUBECONFIG"  rollout status deployment/your-app -n your-namespace
                    '''
                }
            }
        }
        stage('Approve Production Deployment') {
            when {
                allof{
                    branch 'main'
                    not{changeRequest()}
                    }
                }
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    input message: 'Approve deployment to Production?', ok: 'Deploy'
                }
            }
        }
        stage('Deploy to Production') {
            when {
                allof{
                    branch 'main'
                    not{changeRequest()}
                    }
                }
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-production', variable: 'KUBECONFIG')]) {
                    sh'''
                    echo Deploying to Production environment...
                    kubectl --kubeconfig="$KUBECONFIG" apply -f k8s/production/
                    kubectl --kubeconfig="$KUBECONFIG" rollout status deployment/your-app -n your-namespace
                    '''
                }
            }
        }
}
post {
        success {
            slackSend(channel: "${SLACK_CHANNEL}", color: 'good', message: "Build #${env.BUILD_NUMBER} of ${PROJECT_NAME} succeeded. <${env.BUILD_URL}|View Build>")
        }
        failure {
            slackSend(channel: "${SLACK_CHANNEL}", color: 'danger', message: "Build #${env.BUILD_NUMBER} of ${PROJECT_NAME} failed. <${env.BUILD_URL}|View Build>")
            mail to: "${EMAIL_TO}",
                 subject: "Build #${env.BUILD_NUMBER} of ${PROJECT_NAME} Failed",
                 body: "The build has failed. Please check the Jenkins build log at ${env.BUILD_URL}"
        }
    }
}