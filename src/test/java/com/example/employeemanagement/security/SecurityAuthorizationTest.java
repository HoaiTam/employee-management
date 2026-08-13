package com.example.employeemanagement.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedApiRequestReturns401()
            throws Exception {

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedWebRequestRedirectsToLogin()
            throws Exception {

        mockMvc.perform(get("/employees/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrlPattern(
                                "**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanReadEmployees()
            throws Exception {

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateEmployee()
            throws Exception {

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenEmployeeForm()
            throws Exception {

        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk());
    }
}