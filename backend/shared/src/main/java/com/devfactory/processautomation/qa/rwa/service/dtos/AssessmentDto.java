package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Value
public class AssessmentDto extends ValidationBase {
    private String candidateName;
    private String candidateEmail;
    private String uuid;
    private String salesforceOrderId;
    private String provisionJobLink;
    private String provisioningJobId;
    private boolean manualRetryEnabled;
    private String rwaStatus;
    private String status;
    private LocalDateTime created;
    private String username;
    private RegionDto provisioningRegion;
}
