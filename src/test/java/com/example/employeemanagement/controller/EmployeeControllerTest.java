package com.example.employeemanagement.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

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

    @Autowired
    EmployeeControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void createThenFindEmployee() throws Exception {
        MvcResult createResult = mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "  nGUYỄN   vĂN an ",
                                          "email": "an@example.com",
                                          "department": "Engineering"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath(
                        "$.code",
                        matchesPattern("EMP-\\d{4}")))
                .andExpect(jsonPath("$.name")
                        .value("Nguyễn Văn An"))
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

        mockMvc.perform(
                        get("/api/employees")
                                .param("name", "Nguyễn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name")
                        .value("Nguyễn Văn An"));

        mockMvc.perform(
                        get("/api/employees/{id}", 999999))
                .andExpect(status().isNotFound());
    }
}