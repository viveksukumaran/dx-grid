package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.cqa.CqaQeClient;
import com.devfactory.processautomation.cqa.CqaQeClientImpl;
import com.devfactory.processautomation.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QeClientConfiguration extends BaseConfiguration {

    private static final String KEY_BASE_URL = "baseUrl";
    private static final String KEY_AUTH_TOKEN = "authToken";

    public QeClientConfiguration(@Qualifier("cqaQeSettings") final SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Bean
    public CqaQeClient qeClient() {
        QeConfig config = qeConfig();
        return new CqaQeClientImpl(config.getAuthToken(), config.getBaseUrl());
    }

    @Bean
    public QeConfig qeConfig() {
        return QeConfig.builder()
                .authToken(getSetting(KEY_AUTH_TOKEN))
                .baseUrl(getSetting(KEY_BASE_URL))
                .build();
    }
}
