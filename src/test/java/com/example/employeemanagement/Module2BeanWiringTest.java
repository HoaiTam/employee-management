package com.example.employeemanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.employeemanagement.service.UtilityService;
import com.example.employeemanagement.util.EmployeeCodeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Module2BeanWiringTest {

    private final ApplicationContext applicationContext;
    private final UtilityService utilityService;

    @Autowired
    Module2BeanWiringTest(
            ApplicationContext applicationContext,
            UtilityService utilityService) {
        this.applicationContext = applicationContext;
        this.utilityService = utilityService;
    }

    @Test
    void containerCreatesAndWiresModule2Beans() {
        EmployeeCodeGenerator first =
                applicationContext.getBean(
                        EmployeeCodeGenerator.class);

        EmployeeCodeGenerator second =
                applicationContext.getBean(
                        EmployeeCodeGenerator.class);

        assertThat(first).isSameAs(second);

        assertThat(
                utilityService.normalizeEmployeeName(
                        "  nGUYỄN   vĂN an "))
                .isEqualTo("Nguyễn Văn An");

        assertThat(utilityService.generateEmployeeCode())
                .matches("EMP-\\d{4}");
    }
}