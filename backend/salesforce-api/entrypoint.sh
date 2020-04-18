#!/bin/bash
set -e

# Generate settings from Env properties
echo "{  
  \"runDeckUrl\": \"${run_deck_url}\",
  \"runDeckToken\": \"${run_deck_token}\",
  \"runDeckProvisionJob\": \"${run_deck_provision_job}\",
  \"limitRetries\": ${limit_retries},
  \"maxProvisioningAttempts\": ${max_attempts},
  \"runDeckDeprovisionJob\": \"${run_deck_deprovision_job}\",
  \"runDeckExecutionUrl\": \"${run_deck_execution_url}\"
}" > /run_deck_settings.json

echo "{
  \"baseUrl\": \"${cqa_qe_url}\",
  \"authToken\": \"${cqa_qe_token}\"
}" > /cqa_qe_settings.json

echo "{
  \"frontendUrl\": \"${frontend_url}\"
}" > /web_settings.json

cd /
mkdir -p BOOT-INF/classes/
cp cqa_qe_settings.json run_deck_settings.json web_settings.json BOOT-INF/classes/
zip -r DF-PA-Projects-QA-RWA-Backend-Api.jar BOOT-INF/classes/cqa_qe_settings.json
zip -r DF-PA-Projects-QA-RWA-Backend-Api.jar BOOT-INF/classes/run_deck_settings.json
zip -r DF-PA-Projects-QA-RWA-Backend-Api.jar BOOT-INF/classes/web_settings.json
java -jar -Dlogging.level.ROOT=$log_level -Dspring.datasource.username=$db_username -Dspring.datasource.password=$db_password -Dspring.datasource.url=$db_url DF-PA-Projects-QA-RWA-Backend-Api.jar
