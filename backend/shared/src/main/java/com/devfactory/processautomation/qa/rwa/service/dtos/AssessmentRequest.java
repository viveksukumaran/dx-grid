package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssessmentRequest extends ValidationBase {
    @SerializedName("test_id")
    private String testId;
    @SerializedName("callback_url")
    private String callbackUrl;
    private Candidate candidate;
}
