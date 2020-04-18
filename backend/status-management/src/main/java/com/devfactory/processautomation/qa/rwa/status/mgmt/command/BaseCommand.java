package com.devfactory.processautomation.qa.rwa.status.mgmt.command;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
        subcommands = {
                UpdateCommand.class
        })
public class BaseCommand {
    // Add any common options here
}
