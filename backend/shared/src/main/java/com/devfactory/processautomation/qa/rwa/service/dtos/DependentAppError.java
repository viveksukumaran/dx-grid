package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.util.StringUtils;
import com.devfactory.processautomation.validator.BaseError;
import com.devfactory.processautomation.validator.ValidationError;
import java.util.List;
import lombok.Getter;

@Getter
public class DependentAppError extends ValidationError {

    private static final String CODE = "d7a37ea8-c66f-4a25-b5a6-30fd2948ce26";
    private static final String DASH = "-";

    public DependentAppError(String appName, String message, List<ValidationError> appErrors) {
        super(new BaseError(CODE, getFullMessage(message, appErrors)), appName);

    }

    private static String getFullMessage(String message, List<ValidationError> appErrors) {
        StringBuilder fullMessage = new StringBuilder(message);
        if (appErrors != null) {
            for (ValidationError error : appErrors) {
                if (StringUtils.isNotBlank(error.getKey())) {
                    fullMessage.append(DASH).append(error.getKey());
                }
                if (error.getError() != null && StringUtils.isNotBlank(error.getError().getMessage())) {
                    fullMessage.append(DASH).append(error.getError().getMessage());
                }
            }
        }
        return fullMessage.toString();
    }
}
