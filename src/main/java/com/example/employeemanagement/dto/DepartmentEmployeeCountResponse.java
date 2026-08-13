package com.example.employeemanagement.dto;

public record DepartmentEmployeeCountResponse(
        Long departmentId,
        String departmentName,
        long employeeCount) {
}
