package com.devfactory.processautomation.qa.rwa.status.mgmt.command;

import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.validator.ValidationBase;
import com.devfactory.processautomation.validator.ValidationError;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractCommand implements Callable<Integer> {

    private final @NonNull Logger logger;

    private static final String BLANK = "";
    private static final Integer FAILURE = 1;
    private static final Integer SUCCESS = 0;

    Integer result(ValidationBase result) {
        return result(result.getErrors());
    }

    Integer result(List<ValidationError> errors) {
        if (errors.isEmpty()) {
            return SUCCESS;
        } else {
            logErrors(errors);
            return FAILURE;
        }
    }

    Integer success() {
        return SUCCESS;
    }

    Integer failure(List<ValidationError> errors) {
        logErrors(errors);
        return FAILURE;
    }

    Integer failure(String reason) {
        logger.error("Failure reason: {}", reason);
        return FAILURE;
    }

    private void logErrors(List<ValidationError> errors) {
        logger.error("One or more errors occured during execution of the command: \n{}",
            errors.stream().map(err -> err.getError() == null ? BLANK : err.getError().getMessage())
                .collect(Collectors.joining(System.lineSeparator())));
    }
}
