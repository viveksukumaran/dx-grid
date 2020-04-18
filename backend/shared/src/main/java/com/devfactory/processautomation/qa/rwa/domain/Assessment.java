package com.devfactory.processautomation.qa.rwa.domain;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private int id;
    @Setter(value = AccessLevel.NONE)
    private String uuid;
    private String requestJson;
    @Setter(value = AccessLevel.NONE)
    private LocalDateTime created;
    private int provisioningJobId;
    private String provisioningStatus;
    private int provisioningAttempts;
    private boolean onboardedQeTool;
    private String status;
    private String deprovisionJobId;
    private String deprovisionJobStatus;
    private String salesforceOrderId;
    private String candidateName;
    private String candidateEmail;
    private String username;
    private String vendorId;
    private String testId;
    private String rwaStatus;
    private String candidateFirstName;
    private String candidateLastName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_country")
    private Country candidateCountry;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provisioning_region")
    private Region provisioningRegion; 

    public Assessment() {
        uuid = UUID.randomUUID().toString();
    }

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now(ZoneOffset.UTC);
    }
}
