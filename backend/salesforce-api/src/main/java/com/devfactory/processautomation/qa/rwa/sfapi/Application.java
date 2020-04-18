package com.devfactory.processautomation.qa.rwa.sfapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.devfactory.processautomation.qa.rwa" })
@EnableJpaRepositories("com.devfactory.processautomation.qa.rwa.repository")
@EntityScan("com.devfactory.processautomation.qa.rwa.domain")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
