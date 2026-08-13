package com.example.employeemanagement.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.example.employeemanagement.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class EmployeeValidationTest {

    private final MockMvc mockMvc;
    private final DepartmentRepository departmentRepository;

    @Autowired
    EmployeeValidationTest(
            MockMvc mockMvc,
            DepartmentRepository departmentRepository) {
        this.mockMvc = mockMvc;
        this.departmentRepository = departmentRepository;
    }

    @Test
    void invalidEmployeeReturnsFieldErrors()
            throws Exception {

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": " ",
                                          "email": "not-an-email",
                                          "departmentId": null
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath(
                        "$.fieldErrors.name")
                        .exists())
                .andExpect(jsonPath(
                        "$.fieldErrors.email")
                        .value("Email must be valid"))
                .andExpect(jsonPath(
                        "$.fieldErrors.departmentId")
                        .value("Department id is required"));
    }

    @Test
    void malformedJsonReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Nguyen Van An",
                                          "email": "an@example.com",
                                          "departmentId": "abc"
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Malformed request"))
                .andExpect(jsonPath("$.detail")
                        .value(containsString(
                                "invalid value type")));
    }

    @Test
    void missingEmployeeReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        get("/api/employees/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.title")
                        .value("Resource not found"))
                .andExpect(jsonPath("$.detail")
                        .value(containsString(
                                "Employee with id 999999")));
    }

    @Test
    void missingDepartmentReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Nguyen Van An",
                                          "email": "unknown@example.com",
                                          "departmentId": 999999
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value(containsString(
                                "Department with id 999999")));
    }

    @Test
    void duplicateEmailReturnsConflict()
            throws Exception {

        Long departmentId = departmentRepository
                .findByNameIgnoreCase("Engineering")
                .orElseThrow()
                .getId();

        String email =
                "duplicate-%s@example.com"
                        .formatted(UUID.randomUUID());

        String requestBody = """
                {
                  "name": "Nguyen Van An",
                  "email": "%s",
                  "departmentId": %d
                }
                """.formatted(
                email,
                departmentId);

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.detail")
                        .value("Employee email already exists"));
    }
}
