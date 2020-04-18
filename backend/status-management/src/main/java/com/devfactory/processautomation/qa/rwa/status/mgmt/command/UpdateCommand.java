package com.devfactory.processautomation.qa.rwa.status.mgmt.command;

import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.qa.rwa.service.AssessmentService;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "update")
class UpdateCommand extends AbstractCommand {

    private final @NonNull Logger logger;
    private final @NonNull AssessmentService service;

    public UpdateCommand(@NonNull Logger log, @NonNull AssessmentService assessmentService) {
        super(log);
        logger = log;
        service = assessmentService;
    }

    @Override
    public Integer call() {
        logger.info("In update cmd");
        return result(service.updateAssessments());
    }
}
