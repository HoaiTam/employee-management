package com.example.employeemanagement.service;

import java.util.List;

import com.example.employeemanagement.dto.DepartmentEmployeeCountResponse;
import com.example.employeemanagement.dto.EmployeeCountResponse;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeReportService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmployeeReportService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeReportService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Cacheable(
            cacheNames = "employeeCount",
            key = "'total'")
    public EmployeeCountResponse getTotalEmployees() {

        LOGGER.debug(
                "Calculating total employee count from database");

        long totalEmployees =
                employeeRepository.count();

        return new EmployeeCountResponse(totalEmployees);
    }

    public List<DepartmentEmployeeCountResponse>
    getEmployeesByDepartment() {

        LOGGER.debug(
                "Calculating employee count by department");

        return departmentRepository
                .countEmployeesByDepartment();
    }
}
