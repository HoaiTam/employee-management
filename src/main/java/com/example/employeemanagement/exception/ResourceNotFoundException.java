package com.example.employeemanagement.exception;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(
            String resourceName,
            Object identifier) {

        super("%s with id %s was not found"
                .formatted(
                        resourceName,
                        identifier));
    }
}