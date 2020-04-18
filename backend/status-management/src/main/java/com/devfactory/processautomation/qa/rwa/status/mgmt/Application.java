package com.devfactory.processautomation.qa.rwa.status.mgmt;

import com.devfactory.processautomation.qa.rwa.status.mgmt.command.BaseCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = { "com.devfactory.processautomation.qa.rwa" })
@EnableJpaRepositories("com.devfactory.processautomation.qa.rwa.repository")
@EntityScan("com.devfactory.processautomation.qa.rwa.domain")
public class Application implements CommandLineRunner, ExitCodeGenerator {

    private final @NonNull IFactory factory;
    private final @NonNull BaseCommand command;
    private int exitCode;

    public static void main(String[] args) {
        System.exit(SpringApplication
                .exit(new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args)));
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(command, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
