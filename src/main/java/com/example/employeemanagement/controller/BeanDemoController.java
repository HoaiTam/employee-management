package com.example.employeemanagement.controller;

import java.util.Map;

import com.example.employeemanagement.service.UtilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/module2")
public class BeanDemoController {

    private final UtilityService utilityService;

    public BeanDemoController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @GetMapping("/employee-preview")
    public Map<String, String> employeePreview() {
        return Map.of(
                "code", utilityService.generateEmployeeCode(),
                "name", utilityService.normalizeEmployeeName(
                        "  nGUYỄN   vĂN a "));
    }
}