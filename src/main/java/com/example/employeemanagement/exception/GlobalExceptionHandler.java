package com.example.employeemanagement.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice(
        annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(
            ResourceNotFoundException exception) {

        return createProblem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(
            DuplicateResourceException exception) {

        return createProblem(
                HttpStatus.CONFLICT,
                "Resource conflict",
                exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    String message =
                            error.getDefaultMessage() == null
                                    ? "Invalid value"
                                    : error.getDefaultMessage();

                    fieldErrors.putIfAbsent(
                            error.getField(),
                            message);
                });

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields are invalid");

        problem.setProperty(
                "fieldErrors",
                fieldErrors);

        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage(
            HttpMessageNotReadableException exception) {

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Malformed request",
                "Request body contains malformed JSON or an invalid value type");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid parameter",
                "Parameter '%s' has an invalid value"
                        .formatted(exception.getName()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException exception) {

        return createProblem(
                HttpStatus.CONFLICT,
                "Data conflict",
                "Data conflicts with an existing database constraint");
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        status,
                        detail);

        problem.setTitle(title);

        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(
            AuthenticationException exception) {

        return createProblem(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                "Username or password is invalid");
    }
}