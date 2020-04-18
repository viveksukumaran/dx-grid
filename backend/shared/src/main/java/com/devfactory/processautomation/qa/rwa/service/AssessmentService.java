package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentRequest;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.KillResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import com.devfactory.processautomation.validator.ValidationError;
import java.util.List;

public interface AssessmentService {

    AssessmentResponse createAssessment(AssessmentRequest request);

    List<ValidationError> updateAssessments();

    ItemListResponse<AssessmentDto> getAllAssessments();

    AssessmentDto updateStatus(String uuid, String status);

    AssessmentDto retryFailedProvisioning(String uuid);

    AssessmentDto kill(String uuid);

    PagedResponse<AssessmentDto> getAllAssessments(LoadOptions options);

    KillResponse killByEmail(String emailId);
}
