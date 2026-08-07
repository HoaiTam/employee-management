package com.example.employeemanagement.controller;

import java.util.List;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> findAll(
            @RequestParam(
                    name = "name",
                    required = false)
            String name) {

        return ResponseEntity.ok(
                employeeService.findAll(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(
            @PathVariable("id") long id) {

        return employeeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> create(
            @RequestBody CreateEmployeeRequest request) {

        Employee createdEmployee =
                employeeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEmployee);
    }
}