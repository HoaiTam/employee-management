package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeCountResponse;
import com.example.employeemanagement.service.EmployeeReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/employees")
public class EmployeeReportController {

    private final EmployeeReportService employeeReportService;

    public EmployeeReportController(
            EmployeeReportService employeeReportService) {
        this.employeeReportService = employeeReportService;
    }

    @GetMapping("/total")
    public ResponseEntity<EmployeeCountResponse>
    getTotalEmployees() {

        return ResponseEntity.ok(
                employeeReportService.getTotalEmployees());
    }
}