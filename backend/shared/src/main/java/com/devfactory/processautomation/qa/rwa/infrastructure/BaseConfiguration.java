package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.settings.SettingsManager;
import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseConfiguration {

    private final @NonNull SettingsManager settingsManager;

    protected String getSetting(final String key) {
        return settingsManager.getSetting(key, String.class).getValue();
    }

    protected int getIntSetting(final String key) {
        return settingsManager.getSetting(key, Integer.class).getValue();
    }

    protected long getLongSetting(final String key) {
        return settingsManager.getSetting(key, Long.class).getValue();
    }

    protected JsonObject getJsonObjectSetting(final String key) {
        return settingsManager.getSetting(key, JsonObject.class).getValue();
    }

    protected boolean getBooleanSetting(final String key) {
        return settingsManager.getSetting(key, Boolean.class).getValue();
    }
}
