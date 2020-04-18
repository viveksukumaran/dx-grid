package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KillResponse extends ValidationBase {
    private final List<AssessmentDto> items = new ArrayList<>();

    public KillResponse(String errorMessage) {
        super(new InvalidRequestError(errorMessage));
    }

    public KillResponse(List<AssessmentDto> input) {
        setItems(input);
    }

    public List<AssessmentDto> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<AssessmentDto> input) {
        items.clear();
        getErrors().clear();
        if (input != null) {
            items.addAll(input);
            input.forEach(assessment -> addErrors(assessment.getErrors()));
        }
    }
}
