package com.example.employeemanagement.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeInMemoryRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeInMemoryRepository repository;
    private final UtilityService utilityService;

    public EmployeeService(
            EmployeeInMemoryRepository repository,
            UtilityService utilityService) {
        this.repository = repository;
        this.utilityService = utilityService;
    }

    public List<Employee> findAll(String name) {
        List<Employee> employees = repository.findAll();

        if (name == null || name.isBlank()) {
            return employees;
        }

        String keyword = name.trim()
                .toLowerCase(Locale.ROOT);

        return employees.stream()
                .filter(employee ->
                        employee.name()
                                .toLowerCase(Locale.ROOT)
                                .contains(keyword))
                .toList();
    }

    public Optional<Employee> findById(long id) {
        return repository.findById(id);
    }

    public Employee create(CreateEmployeeRequest request) {
        Employee employee = new Employee(
                null,
                utilityService.generateEmployeeCode(),
                utilityService.normalizeEmployeeName(
                        request.name()),
                request.email(),
                request.department());

        return repository.save(employee);
    }
}