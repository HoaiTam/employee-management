package com.example.employeemanagement.service;

import java.util.List;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.dto.DepartmentResponse;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.UpdateEmployeeRequest;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Department;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    EmployeeService.class);

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

        LOGGER.debug(
                "Searching employees: nameFilterPresent={}, departmentId={}",
                !keyword.isBlank(),
                departmentId);

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

        LOGGER.debug(
                "Employee search completed: resultCount={}",
                employees.size());

        return employees.stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeResponse findById(long id) {
        return toResponse(findEmployee(id));
    }

    @CacheEvict(
            cacheNames = "employeeCount",
            key = "'total'")
    @Transactional
    public EmployeeResponse create(
            CreateEmployeeRequest request) {

        Department department =
                findDepartment(request.departmentId());

        String normalizedEmail =
                utilityService.normalizeEmail(
                        request.email());

        if (employeeRepository
                .existsByEmailIgnoreCase(
                        normalizedEmail)) {
            throw new DuplicateResourceException(
                    "Employee email already exists");
        }

        Employee employee = new Employee(
                utilityService.normalizeEmployeeName(
                        request.name()),
                normalizedEmail,
                department);

        Employee savedEmployee =
                employeeRepository.save(employee);

        LOGGER.info(
                "Employee created: employeeId={}, departmentId={}",
                savedEmployee.getId(),
                department.getId());

        return toResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse update(
            long id,
            UpdateEmployeeRequest request) {

        Employee employee = findEmployee(id);

        Department department =
                findDepartment(request.departmentId());

        String normalizedEmail =
                utilityService.normalizeEmail(
                        request.email());

        if (employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        normalizedEmail,
                        id)) {
            throw new DuplicateResourceException(
                    "Employee email already exists");
        }

        employee.updateDetails(
                utilityService.normalizeEmployeeName(
                        request.name()),
                normalizedEmail,
                department);

        LOGGER.info(
                "Employee updated: employeeId={}, departmentId={}",
                employee.getId(),
                department.getId());

        return toResponse(employee);
    }

    @CacheEvict(
            cacheNames = "employeeCount",
            key = "'total'")
    @Transactional
    public void delete(long id) {
        Employee employee = findEmployee(id);

        employeeRepository.delete(employee);

        LOGGER.info(
                "Employee deleted: employeeId={}",
                employee.getId());
    }

    private Employee findEmployee(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee",
                                id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department",
                                id));
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