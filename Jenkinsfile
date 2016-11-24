node() {
     stage('Checkout from GitHub') 
         git 'https://github.com/qaware/gradle-cloud-deployer'
    
     stage('Build project')
        sh 'chmod +x gradlew'
        sh './gradlew jacocoTestReport --info --no-daemon' 
}
