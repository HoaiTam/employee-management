package com.example.employeemanagement.controller;

import com.example.employeemanagement.service.EmployeeReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees/statistics")
public class EmployeeStatisticsPageController {

    private final EmployeeReportService employeeReportService;

    public EmployeeStatisticsPageController(
            EmployeeReportService employeeReportService) {
        this.employeeReportService = employeeReportService;
    }

    @GetMapping
    public String statistics(Model model) {

        model.addAttribute(
                "totalEmployees",
                employeeReportService
                        .getTotalEmployees()
                        .totalEmployees());

        model.addAttribute(
                "departmentStatistics",
                employeeReportService
                        .getEmployeesByDepartment());

        return "employees/statistics";
    }
}
