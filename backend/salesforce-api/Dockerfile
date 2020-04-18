FROM openjdk:8-jdk-alpine
ENV frontend_url http://localhost:4200
ENV db_url "jdbc:mysql://quicksightdb-demo.cluster-c4vjti1moh3n.us-east-1.rds.amazonaws.com:3306/df_pa_qa_rwa?useUnicode=true&characterEncoding=UTF-8"
ENV db_username null
ENV db_password null
ENV log_level info
ENV run_deck_url https://rundeck.devfactory.com/api/31
ENV run_deck_token null
ENV run_deck_provision_job "a322bb25-4a45-43ad-9c3a-263b1e37a2ae"
ENV run_deck_deprovision_job "29e0c5eb-c2bc-44a2-a5a0-c0b1e21ba11d"
ENV cqa_qe_url "http://testrail-qb-enforcement.process.aureacentral.com/rest/api"
ENV cqa_qe_token null
ENV limit_retries false
ENV max_attempts 2
ENV run_deck_execution_url "https://rundeck.devfactory.com/project/XORWA/execution/show/"

#aws cli
RUN apk -Uuv add less zip && apk --purge -v del py-pip && rm /var/cache/apk/*

#for json operations on entrypoint script
RUN apk add jq

WORKDIR /
ADD build/libs/DF-PA-Projects-QA-RWA-Backend-Api-*.jar DF-PA-Projects-QA-RWA-Backend-Api.jar
ADD entrypoint.sh entrypoint.sh
ENTRYPOINT ["sh", "entrypoint.sh"]
EXPOSE 8080
