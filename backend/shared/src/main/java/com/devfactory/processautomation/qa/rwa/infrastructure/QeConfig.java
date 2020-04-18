package com.devfactory.processautomation.qa.rwa.infrastructure;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QeConfig {
    private String baseUrl;
    private String authToken;
}
