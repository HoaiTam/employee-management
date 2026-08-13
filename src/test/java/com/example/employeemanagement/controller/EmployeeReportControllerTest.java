package com.example.employeemanagement.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
class EmployeeReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsTotalEmployeeCount()
            throws Exception {

        mockMvc.perform(
                        get("/api/reports/employees/total"))
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .contentTypeCompatibleWith(
                                        APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.totalEmployees")
                                .isNumber());
    }
}
