package com.example.employeemanagement.controller;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BeanDemoControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    BeanDemoControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void employeePreviewReturnsGeneratedCodeAndNormalizedName()
            throws Exception {

        mockMvc.perform(get("/api/module2/employee-preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",
                        matchesPattern("EMP-\\d{4}")))
                .andExpect(jsonPath("$.name")
                        .value("Nguyễn Văn An"));
    }
}