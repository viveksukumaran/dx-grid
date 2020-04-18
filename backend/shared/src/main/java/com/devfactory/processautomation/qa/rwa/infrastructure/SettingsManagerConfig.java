package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.settings.FileSystemSettingsManagerImpl;
import com.devfactory.processautomation.settings.SettingsManager;
import com.devfactory.processautomation.settings.dtos.SettingsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SettingsManagerConfig {

    private static final String CQA_QE_SETTINGS = "/cqa_qe_settings.json";
    private static final String RUN_DECK_SETTINGS = "/run_deck_settings.json";
    private static final String WEB_SETTINGS = "/web_settings.json";

    @Bean("cqaQeSettings")
    public SettingsManager cqaQeSettings() {
        final SettingsConfiguration settingsConfiguration = new SettingsConfiguration(CQA_QE_SETTINGS);
        return new FileSystemSettingsManagerImpl(settingsConfiguration);
    }

    @Bean("runDeckSettings")
    public SettingsManager runDeckSettings() {
        final SettingsConfiguration settingsConfiguration = new SettingsConfiguration(RUN_DECK_SETTINGS);
        return new FileSystemSettingsManagerImpl(settingsConfiguration);
    }

    @Bean("webSettings")
    public SettingsManager webSettings() {
        final SettingsConfiguration settingsConfiguration = new SettingsConfiguration(WEB_SETTINGS);
        return new FileSystemSettingsManagerImpl(settingsConfiguration);
    }
}
