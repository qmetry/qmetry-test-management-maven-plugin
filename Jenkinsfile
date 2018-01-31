node {
   stage('Clean') {
        deleteDir()
    }
    stage('Checkout') {
        checkout scm
    }
    stage('Build') {    
		if (isUnix()) {
			sh "mvn package"
		}
		else{
			bat "mvn package"
		}	
    }
}