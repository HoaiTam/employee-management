package com.example.employeemanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import com.example.employeemanagement.dto.DepartmentEmployeeCountResponse;
import com.example.employeemanagement.model.Department;
import com.example.employeemanagement.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DepartmentStatisticsRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void countsEmployeesAndIncludesEmptyDepartments() {

        String suffix = UUID.randomUUID().toString();

        Department populatedDepartment =
                departmentRepository.save(
                        new Department(
                                "Statistics A " + suffix));

        Department emptyDepartment =
                departmentRepository.save(
                        new Department(
                                "Statistics B " + suffix));

        employeeRepository.saveAll(List.of(
                new Employee(
                        "Employee One",
                        "one-" + suffix + "@example.com",
                        populatedDepartment),
                new Employee(
                        "Employee Two",
                        "two-" + suffix + "@example.com",
                        populatedDepartment)));

        List<DepartmentEmployeeCountResponse> statistics =
                departmentRepository
                        .countEmployeesByDepartment();

        assertThat(statistics)
                .filteredOn(statistic ->
                        statistic.departmentId().equals(
                                populatedDepartment.getId()))
                .singleElement()
                .extracting(
                        DepartmentEmployeeCountResponse
                                ::employeeCount)
                .isEqualTo(2L);

        assertThat(statistics)
                .filteredOn(statistic ->
                        statistic.departmentId().equals(
                                emptyDepartment.getId()))
                .singleElement()
                .extracting(
                        DepartmentEmployeeCountResponse
                                ::employeeCount)
                .isEqualTo(0L);
    }
}
