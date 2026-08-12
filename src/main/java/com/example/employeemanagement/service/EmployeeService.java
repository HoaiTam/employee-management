package com.example.employeemanagement.service;

import java.util.List;
import java.util.Optional;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.dto.DepartmentResponse;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.UpdateEmployeeRequest;
import com.example.employeemanagement.model.Department;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UtilityService utilityService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            UtilityService utilityService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.utilityService = utilityService;
    }

    public List<EmployeeResponse> findAll(
            String name,
            Long departmentId) {

        String keyword =
                name == null ? "" : name.trim();

        List<Employee> employees;

        if (!keyword.isBlank() && departmentId != null) {
            employees = employeeRepository
                    .findByNameContainingIgnoreCaseAndDepartment_IdOrderByIdAsc(
                            keyword,
                            departmentId);
        } else if (!keyword.isBlank()) {
            employees = employeeRepository
                    .findByNameContainingIgnoreCaseOrderByIdAsc(
                            keyword);
        } else if (departmentId != null) {
            employees = employeeRepository
                    .findByDepartment_IdOrderByIdAsc(
                            departmentId);
        } else {
            employees = employeeRepository
                    .findAllByOrderByIdAsc();
        }

        return employees.stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<EmployeeResponse> findById(long id) {
        return employeeRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional
    public Optional<EmployeeResponse> create(
            CreateEmployeeRequest request) {

        return departmentRepository
                .findById(request.departmentId())
                .map(department -> new Employee(
                        utilityService.normalizeEmployeeName(
                                request.name()),
                        request.email(),
                        department))
                .map(employeeRepository::save)
                .map(this::toResponse);
    }

    @Transactional
    public Optional<EmployeeResponse> update(
            long id,
            UpdateEmployeeRequest request) {

        Optional<Employee> employeeResult =
                employeeRepository.findById(id);

        Optional<Department> departmentResult =
                departmentRepository.findById(
                        request.departmentId());

        if (employeeResult.isEmpty()
                || departmentResult.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = employeeResult.get();

        employee.updateDetails(
                utilityService.normalizeEmployeeName(
                        request.name()),
                request.email(),
                departmentResult.get());

        return Optional.of(toResponse(employee));
    }

    @Transactional
    public boolean delete(long id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employeeRepository.delete(employee);
                    return true;
                })
                .orElse(false);
    }

    private EmployeeResponse toResponse(Employee employee) {
        Department department =
                employee.getDepartment();

        DepartmentResponse departmentResponse =
                new DepartmentResponse(
                        department.getId(),
                        department.getName());

        return new EmployeeResponse(
                employee.getId(),
                utilityService.formatEmployeeCode(
                        employee.getId()),
                employee.getName(),
                employee.getEmail(),
                departmentResponse);
    }
}