package com.example.employeemanagement.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import com.example.employeemanagement.repository.DepartmentRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    private final MockMvc mockMvc;
    private final DepartmentRepository departmentRepository;

    @Autowired
    EmployeeControllerTest(
            MockMvc mockMvc,
            DepartmentRepository departmentRepository) {
        this.mockMvc = mockMvc;
        this.departmentRepository = departmentRepository;
    }

    @Test
    void crudAndSearchEmployeeInDatabase()
            throws Exception {

        Long engineeringId = departmentRepository
                .findByNameIgnoreCase("Engineering")
                .orElseThrow()
                .getId();

        Long financeId = departmentRepository
                .findByNameIgnoreCase("Finance")
                .orElseThrow()
                .getId();

        MvcResult createResult = mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "  nGUYEN   van an ",
                                          "email": "an@example.com",
                                          "departmentId": %d
                                        }
                                        """.formatted(engineeringId)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        matchesPattern(
                                ".*/api/employees/\\d+")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath(
                        "$.code",
                        matchesPattern("EMP-\\d{4}")))
                .andExpect(jsonPath("$.name")
                        .value("Nguyen Van An"))
                .andExpect(jsonPath("$.department.name")
                        .value("Engineering"))
                .andReturn();

        String responseBody = createResult
                .getResponse()
                .getContentAsString(
                        StandardCharsets.UTF_8);

        Number employeeId =
                JsonPath.read(responseBody, "$.id");

        mockMvc.perform(get(
                        "/api/employees/{id}",
                        employeeId.longValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("an@example.com"));

        // Kiểm tra endpoint không có query param.
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[*].email",
                        hasItem("an@example.com")));

        mockMvc.perform(
                        get("/api/employees")
                                .param("name", "nguyen")
                                .param(
                                        "departmentId",
                                        engineeringId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[*].email",
                        hasItem("an@example.com")));

        mockMvc.perform(
                        put(
                                "/api/employees/{id}",
                                employeeId.longValue())
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "  tran thi binh ",
                                          "email": "binh@example.com",
                                          "departmentId": %d
                                        }
                                        """.formatted(financeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Tran Thi Binh"))
                .andExpect(jsonPath("$.department.name")
                        .value("Finance"));

        mockMvc.perform(
                        delete(
                                "/api/employees/{id}",
                                employeeId.longValue()))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/employees/{id}",
                                employeeId.longValue()))
                .andExpect(status().isNotFound());
    }
}