package com.devfactory.processautomation.qa.rwa.infrastructure;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.logger.LoggerImpl;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerConfiguration {

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint injectionPoint) {
        return new LoggerImpl(injectionPoint.getMethodParameter().getContainingClass());
    }
}


