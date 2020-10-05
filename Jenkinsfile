node{
    stage('SCM Checkout'){
        git branch: 'cicd', credentialsId: 'gitlab', url: 'git@git.swmgit.org:swmaestro/recorder-1.git'
    }

    stage('build & test'){
        sh './gradlew build'
    }

    stage ('build image'){
        app = docker.build("644637824921.dkr.ecr.ap-northeast-2.amazonaws.com/backend-spring","-f ./Dockerfile .")
    }

    stage('Push image') {
         sh 'rm  ~/.dockercfg || true'
         sh 'rm ~/.docker/config.json || true'

         docker.withRegistry('https://644637824921.dkr.ecr.ap-northeast-2.amazonaws.com', 'ecr:ap-northeast-2:ecr-credential') {
             app.push("latest")
        }
    }
}