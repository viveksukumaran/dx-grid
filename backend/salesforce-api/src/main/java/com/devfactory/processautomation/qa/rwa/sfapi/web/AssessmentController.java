package com.devfactory.processautomation.qa.rwa.sfapi.web;

import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.qa.rwa.service.AssessmentService;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentRequest;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.KillResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assessments")
public class AssessmentController {

    private final @NonNull AssessmentService assessmentService;

    @PostMapping("/salesforce")
    public AssessmentResponse createAssessment(@RequestBody AssessmentRequest request) {
        return assessmentService.createAssessment(request);
    }

    @GetMapping("")
    public ItemListResponse<AssessmentDto> getAllAssessments() {
        return assessmentService.getAllAssessments();
    }

    @PutMapping("/{id}")
    public AssessmentDto updateAssessmentStatus(@PathVariable("id") String uuid, @RequestBody String status) {
        return assessmentService.updateStatus(uuid, status);
    }

    @PostMapping("/{id}/deprovision")
    public AssessmentDto deProvision(@PathVariable("id") String uuid) {
        return assessmentService.kill(uuid);
    }

    @PostMapping("/{id}/retry-provisioning")
    public AssessmentDto retryProvisioning(@PathVariable("id") String uuid) {
        return assessmentService.retryFailedProvisioning(uuid);
    }

    @PostMapping("/load")
    public PagedResponse<AssessmentDto> loadAssessments(@RequestBody LoadOptions request) {
        return assessmentService.getAllAssessments(request);
    }

    @PostMapping("/deprovision")
    public KillResponse deProvisionEmail(@RequestBody String emailId) {
        return assessmentService.killByEmail(emailId);
    }
}
