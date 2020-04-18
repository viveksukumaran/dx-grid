package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration extends BaseConfiguration implements WebMvcConfigurer {

    private static final String KEY_FRONTEND_URL = "frontendUrl";

    public WebConfiguration(@Qualifier("webSettings") final SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins(getSetting(KEY_FRONTEND_URL))
                .allowedMethods(RequestMethod.GET.toString(),
                        RequestMethod.POST.toString(),
                        RequestMethod.DELETE.toString(),
                        RequestMethod.PUT.toString(),
                        RequestMethod.PATCH.toString());
    }

    @Bean
    public WebConfig getWebConfig() {
        return new WebConfig(getSetting(KEY_FRONTEND_URL));
    }
}
