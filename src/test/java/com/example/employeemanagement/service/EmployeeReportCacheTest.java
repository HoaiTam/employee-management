package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Objects.requireNonNull;

import java.util.UUID;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.dto.EmployeeCountResponse;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.model.Department;
import com.example.employeemanagement.repository.DepartmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmployeeReportCacheTest {

    @Autowired
    private EmployeeReportService employeeReportService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    @AfterEach
    void clearCache() {
        employeeCountCache().clear();
    }

    @Test
    void cachesCountAndEvictsAfterCreateAndDelete() {

        Cache cache = employeeCountCache();

        EmployeeCountResponse first =
                employeeReportService.getTotalEmployees();

        EmployeeCountResponse second =
                employeeReportService.getTotalEmployees();

        assertThat(second).isSameAs(first);
        assertThat(
                cache.get(
                        "total",
                        EmployeeCountResponse.class))
                .isSameAs(first);

        Department department =
                departmentRepository
                        .findAllByOrderByNameAsc()
                        .get(0);

        EmployeeResponse created =
                employeeService.create(
                        new CreateEmployeeRequest(
                                "Cache Test",
                                UUID.randomUUID()
                                        + "@example.com",
                                department.getId()));

        assertThat(cache.get("total")).isNull();

        EmployeeCountResponse afterCreate =
                employeeReportService
                        .getTotalEmployees();

        assertThat(afterCreate.totalEmployees())
                .isEqualTo(
                        first.totalEmployees() + 1);

        employeeService.delete(created.id());

        assertThat(cache.get("total")).isNull();

        EmployeeCountResponse afterDelete =
                employeeReportService
                        .getTotalEmployees();

        assertThat(afterDelete.totalEmployees())
                .isEqualTo(first.totalEmployees());
    }

    private Cache employeeCountCache() {
        return requireNonNull(
                cacheManager.getCache(
                        "employeeCount"));
    }
}