package com.example.employeemanagement.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HelloController.class)
@WithMockUser(roles = "USER")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloReturnsApplicationStatus() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application", is("employee-management")))
                .andExpect(jsonPath("$.message", is("Employee Management System is running")));
    }
}
