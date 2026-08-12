package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.UpdateEmployeeRequest;
import com.example.employeemanagement.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class EmployeeServiceLoggingTest {

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;

    @Autowired
    EmployeeServiceLoggingTest(
            EmployeeService employeeService,
            DepartmentRepository departmentRepository) {
        this.employeeService = employeeService;
        this.departmentRepository = departmentRepository;
    }

    @Test
    void logsEmployeeLifecycleWithoutPersonalData(
            CapturedOutput output) {

        Long engineeringId = departmentRepository
                .findByNameIgnoreCase("Engineering")
                .orElseThrow()
                .getId();

        Long financeId = departmentRepository
                .findByNameIgnoreCase("Finance")
                .orElseThrow()
                .getId();

        String email =
                "logging-%s@example.com"
                        .formatted(UUID.randomUUID());

        EmployeeResponse created =
                employeeService.create(
                        new CreateEmployeeRequest(
                                "pham thi lan",
                                email,
                                engineeringId));

        employeeService.findAll(
                "pham",
                engineeringId);

        employeeService.update(
                created.id(),
                new UpdateEmployeeRequest(
                        "pham thi lan",
                        email,
                        financeId));

        employeeService.delete(created.id());

        assertThat(output)
                .contains(
                        "Employee created: employeeId=%d, departmentId=%d"
                                .formatted(
                                        created.id(),
                                        engineeringId))
                .contains(
                        "Searching employees: nameFilterPresent=true, departmentId="
                                + engineeringId)
                .contains(
                        "Employee updated: employeeId=%d, departmentId=%d"
                                .formatted(
                                        created.id(),
                                        financeId))
                .contains(
                        "Employee deleted: employeeId="
                                + created.id())
                .doesNotContain(email);
    }
}