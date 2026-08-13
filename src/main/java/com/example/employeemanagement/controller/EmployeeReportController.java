package com.example.employeemanagement.controller;

import java.util.List;

import com.example.employeemanagement.dto.DepartmentEmployeeCountResponse;
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

    @GetMapping("/by-department")
    public ResponseEntity<
            List<DepartmentEmployeeCountResponse>>
    getEmployeesByDepartment() {

        return ResponseEntity.ok(
                employeeReportService
                        .getEmployeesByDepartment());
    }
}
