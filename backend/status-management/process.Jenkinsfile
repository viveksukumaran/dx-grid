pipeline {

    agent any
    
    options {
        timeout(time: 5, unit: 'MINUTES') 
    }        
    stages {
        stage('ReplaceRundeckConfig') {
            steps {
                println "Replacing Rundeck Properties"
                configFileProvider([configFile(fileId: "DF-PA-Projects-QA-RWA-Rundeck-Settings", variable: 'rundeckConfigFile')]) {
                   sh "cp \$rundeckConfigFile backend/shared/src/main/resources/run_deck_settings.json"
                }
            }
        }
        stage('ReplaceCqaQeConfig') {
            steps {
                println "Replacing CQA QE Properties"
                configFileProvider([configFile(fileId: "DF-PA-Projects-QA-RWA-QE-Settings", variable: 'qeConfigFile')]) {
                   sh "cp \$qeConfigFile backend/shared/src/main/resources/cqa_qe_settings.json"
                }
            }
        }
        stage('Build') {
            steps {
                println "Building the executable jar"
                sh 'cd backend && chmod a+x ./gradlew && ./gradlew clean build'                
                sh 'cp backend/status-management/build/libs/DF-PA-Projects-QA-RWA-Backend-StatusMgmt-*.jar backend/status-management/build/libs/DF-PA-Projects-QA-RWA-Backend-StatusMgmt.jar'
            }
        }        
        stage('Running the application') {
            steps {         
                withCredentials([usernamePassword(credentialsId: 'DF-PA-QA-RWA-Mysql', passwordVariable: 'password', usernameVariable: 'username')]) {                           
                    sh "java -Dlogging.level.ROOT=debug -Dspring.datasource.username=$username -Dspring.datasource.password=$password -Dspring.datasource.url='jdbc:mysql://aurora1.aureacentral.com:3306/df_pa_qa_rwa?useUnicode=true&characterEncoding=UTF-8' -jar backend/status-management/build/libs/DF-PA-Projects-QA-RWA-Backend-StatusMgmt.jar update"
                }              
            }
        }
    }
    post {
        unsuccessful {
            script {
                response = httpRequest consoleLogResponseBody: true, httpMode: 'POST', requestBody: '{"text": "QA RWA Status Management Job Failed\nCheck [here](https://df-product-jenkins.devfactory.com/job/process-automation/job/DF-PA-Projects-QA-RWA/job/Status-Mangement-Job/)"}', url: 'https://xo.chat.crossover.com/hooks/wsyq8p8tcjfw9xdczy7k9b6iec'
            }
        }
    }
}