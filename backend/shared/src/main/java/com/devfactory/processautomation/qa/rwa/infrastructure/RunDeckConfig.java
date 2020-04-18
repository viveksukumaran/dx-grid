package com.devfactory.processautomation.qa.rwa.infrastructure;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RunDeckConfig {
    private String runDeckUrl;
    private String runDeckToken;
    private String runDeckProvisionJob;
    private int maxProvisioningAttempts;
    private boolean limitRetries;
    private String runDeckDeprovisionJob;
    private String runDeckExecutionUrl;
}
