node{
    stage('SCM Checkout'){
        git branch: 'cicd', credentialsId: 'gitlab-ssh-key', url: 'git@git.swmgit.org:swmaestro/recorder-1.git'
    }

    stage('build & test'){
        sh './gradlew build'
    }

    stage ('make zip file'){
        sh 'mkdir -p before-deploy'
        sh 'cp build/libs/*.jar before-deploy/'
        sh 'cp scripts/*.sh before-deploy/'
        sh 'cp appspec.yml before-deploy/'
        sh 'cd before-deploy && zip -r before-deploy *'
        sh 'cd ../'
        sh 'mkdir -p deploy'
        sh 'mv before-deploy/before-deploy.zip deploy/sprint1.zip'
    }

    stage ('upload to AWS S3'){
        sh 'aws s3 cp deploy/sprint1.zip s3://cicd-spring/sprint1.zip --region ap-northeast-2'
    }

    stage('deploy'){
        sh 'aws deploy create-deployment \
            --application-name backend-spring \
            --deployment-group-name backend-spring-group \
            --region ap-northeast-2 \
            --s3-location bucket=cicd-spring,bundleType=zip,key=sprint1.zip'
    }
}