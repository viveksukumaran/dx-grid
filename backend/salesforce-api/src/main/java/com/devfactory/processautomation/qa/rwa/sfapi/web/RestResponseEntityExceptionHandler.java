package com.devfactory.processautomation.qa.rwa.sfapi.web;

import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.validator.BaseError;
import com.devfactory.processautomation.validator.ValidationBase;
import com.devfactory.processautomation.validator.ValidationError;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler
    extends ResponseEntityExceptionHandler implements ResponseBodyAdvice<Object> {

    private final @NonNull Logger logger;

    private static final String BASE_ERROR_UUID = "4333776f-af4f-4891-b547-a5c230fb258b";
    private static final String BASE_ERROR_MSG = "Unhandled Exception. Please try again later.";
    private static final String DEFAULT_ERROR_MESSAGE = "Unexpected Error";

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> unexpectedExceptionHandler(RuntimeException ex, WebRequest request) {
        logger.error(ex.getMessage());

        ValidationError response = new ValidationError(
            new BaseError(BASE_ERROR_UUID, BASE_ERROR_MSG),
            DEFAULT_ERROR_MESSAGE);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {
        if (body instanceof ValidationBase && !((ValidationBase) body).isValid()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        }
        return body;
    }
}
