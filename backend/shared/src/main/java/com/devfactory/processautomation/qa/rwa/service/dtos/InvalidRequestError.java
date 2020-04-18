package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.BaseError;
import com.devfactory.processautomation.validator.ValidationError;
import lombok.Getter;

@Getter
public class InvalidRequestError extends ValidationError {

    private static final String CODE = "d7a37ea8-c66f-4a25-b5a6-30fd2948ce29";

    public InvalidRequestError(String message) {
        super(new BaseError(CODE, message), "Invalid request");
    }
}
