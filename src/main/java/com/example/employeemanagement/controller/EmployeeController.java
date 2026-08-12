package com.example.employeemanagement.controller;

import java.net.URI;
import java.util.List;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.UpdateEmployeeRequest;
import com.example.employeemanagement.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll(
            @RequestParam(
                    name = "name",
                    required = false)
            String name,
            @RequestParam(
                    name = "departmentId",
                    required = false)
            Long departmentId) {

        return ResponseEntity.ok(
                employeeService.findAll(
                        name,
                        departmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(
            @PathVariable("id") long id) {

        return ResponseEntity.ok(
                employeeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid
            @RequestBody
            CreateEmployeeRequest request) {

        EmployeeResponse createdEmployee =
                employeeService.create(request);

        URI location =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(
                                createdEmployee.id())
                        .toUri();

        return ResponseEntity
                .created(location)
                .body(createdEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable("id") long id,
            @Valid
            @RequestBody
            UpdateEmployeeRequest request) {

        return ResponseEntity.ok(
                employeeService.update(
                        id,
                        request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") long id) {

        employeeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}