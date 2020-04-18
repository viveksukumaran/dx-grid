package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.rundeck.RunDeckClient;
import com.devfactory.processautomation.rundeck.RunDeckClientImpl;
import com.devfactory.processautomation.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RunDeckConfiguration extends BaseConfiguration {

    private static final String KEY_RUN_DECK_URL = "runDeckUrl";
    private static final String KEY_RUN_DECK_TOKEN = "runDeckToken";
    private static final String KEY_RUN_DECK_PROVISION_JOB = "runDeckProvisionJob";
    private static final String KEY_RUN_DECK_DEPROVISION_JOB = "runDeckDeprovisionJob";
    private static final String KEY_MAX_ATTEMPTS = "maxProvisioningAttempts";
    private static final String KEY_RUN_DECK_EXECUTION_URL = "runDeckExecutionUrl";
    private static final String KEY_LIMIT_RETRIES = "limitRetries";

    public RunDeckConfiguration(@Qualifier("runDeckSettings") final SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Bean
    public RunDeckClient runDeckClient() {
        RunDeckConfig config = runDeckConfig();
        return new RunDeckClientImpl(config.getRunDeckToken(), config.getRunDeckUrl());
    }

    @Bean
    public RunDeckConfig runDeckConfig() {
        return RunDeckConfig.builder()
                .runDeckProvisionJob(getSetting(KEY_RUN_DECK_PROVISION_JOB))
                .runDeckToken(getSetting(KEY_RUN_DECK_TOKEN))
                .runDeckUrl(getSetting(KEY_RUN_DECK_URL))
                .runDeckDeprovisionJob(getSetting(KEY_RUN_DECK_DEPROVISION_JOB))
                .maxProvisioningAttempts(getIntSetting(KEY_MAX_ATTEMPTS))
                .runDeckExecutionUrl(getSetting(KEY_RUN_DECK_EXECUTION_URL))
                .limitRetries(getBooleanSetting(KEY_LIMIT_RETRIES))
                .build();
    }
}
