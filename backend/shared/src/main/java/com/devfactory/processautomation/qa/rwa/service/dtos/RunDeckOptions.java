package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RunDeckOptions extends ValidationBase {
    @SerializedName("given_name")
    private String givenName;
    @SerializedName("last_name")
    private String lastName;
    private String email;
    private String username;
    @SerializedName("candidate_region")
    private String candidateRegion;
}
