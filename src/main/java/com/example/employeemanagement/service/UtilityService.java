package com.example.employeemanagement.service;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import com.example.employeemanagement.util.EmployeeCodeGenerator;
import org.springframework.stereotype.Service;

@Service
public class UtilityService {

    private final EmployeeCodeGenerator employeeCodeGenerator;

    public UtilityService(EmployeeCodeGenerator employeeCodeGenerator) {
        this.employeeCodeGenerator = employeeCodeGenerator;
    }

    public String generateEmployeeCode() {
        return employeeCodeGenerator.nextCode();
    }

    public String normalizeEmployeeName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "";
        }

        return Arrays.stream(
                        rawName.trim().toLowerCase(Locale.ROOT).split("\\s+"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining(" "));
    }
}