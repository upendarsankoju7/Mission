pipeline {
    agent any

    tools {
    jdk 'jdk17'
    maven 'maven3'
 }

    environment {
       SCANNER_HOME = tool 'sonar-scanner'
    }
    stages {
       stage('Git Checkout') {
        steps {
          git branch: 'main', changelog: false, poll: false, url: 'https://github.com/upendarsankoju7/Mission.git'
    }
 }

 stage('Compile') {

    steps {
      sh "mvn compile"
     }
 }

    stage('Test') {

      steps {
       sh "mvn package -DskipTests=true"
    }
 }
stage('Trivy Scan File System') {

    steps {
     sh "trivy fs --format table -o trivy-fs-report.html ."
   }
 }

stage('SonarQube Analysis') {

   steps {

     withSonarQubeEnv('sonar') {
       sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectKey=Mission -Dsonar.projectName=Mission \
       -Dsonar.java.binaries=. '''
    }
   }
 }

 stage('Build') {
   steps {
     sh "mvn package -DskipTests=true"
   }      
 }



 stage('Deploy Artifacts To Nexus') {
   steps {
     withMaven(globalMavenSettingsConfig: 'maven-setting', jdk:
     'jdk17', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
      sh "mvn deploy -DskipTests=true"
      }
   }
 }


 stage('Build & Deploy docker image') {
   steps {

    withDockerRegistry(credentialsId: 'docker', url: 'https://index.docker.io/v1/') {
                        sh 'docker build -t upendar07/mission:latest .'
                        
    }
   }      
 }


stage('Trivy Scan Image') {
 steps {
 sh "trivy image --format table -o trivy-image-report.html upendar07/mission:latest"
   }
 }

stage('docker image psuh') {
   steps {

   withDockerRegistry(credentialsId: 'docker' , url: 'https://index.docker.io/v1/') {
                        sh 'docker push upendar07/mission:latest'
    }
   }      
 }

       


eksctl utils associate-iam-oidc-provider \
    --region ap-south-1 \
    --cluster my-eks22 \
    --approve

eksctl create nodegroup --cluster=my-eks741 \
                       --region=ap-south-1 \
                       --name=node2 \
                       --node-type=t3.medium \
                       --nodes=2 \
                       --nodes-min=2 \
                       --nodes-max=3 \
                       --node-volume-size=20 \
                       --ssh-access \
                       --ssh-public-key=Key-DevOps \
                       --managed \
                       --asg-access \
                       --external-dns-access \
                       --full-ecr-access \
                       --appmesh-access \
                       --alb-ingress-access





//end 
 }
}
  