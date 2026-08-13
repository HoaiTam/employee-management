package com.example.employeemanagement.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationFlowTest {

    private final MockMvc mockMvc;

    @Autowired
    AuthenticationFlowTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void authenticatesApiUsingHttpBasic()
            throws Exception {

        mockMvc.perform(
                        get("/api/employees")
                                .with(httpBasic(
                                        "user",
                                        "User123!")))
                .andExpect(status().isOk());
    }

    @Test
    void issuesAndAcceptsJwt()
            throws Exception {

        String responseBody = mockMvc.perform(
                        post("/api/auth/token")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "user",
                                          "password": "User123!"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.tokenType")
                                .value("Bearer"))
                .andExpect(
                        jsonPath("$.expiresIn")
                                .value(900))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken =
                JsonPath.read(
                        responseBody,
                        "$.accessToken");

        mockMvc.perform(
                        get("/api/employees")
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatesBrowserUsingFormLogin()
            throws Exception {

        mockMvc.perform(
                        post("/login")
                                .with(csrf())
                                .param(
                                        "username",
                                        "user")
                                .param(
                                        "password",
                                        "User123!"))
                .andExpect(
                        status()
                                .is3xxRedirection())
                .andExpect(
                        redirectedUrl(
                                "/employees/list"));
    }

    @Test
    void rejectsInvalidCredentialsWithoutLeakingDetails()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/token")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "user",
                                          "password": "wrong-password"
                                        }
                                        """))
                .andExpect(
                        status()
                                .isUnauthorized())
                .andExpect(
                        jsonPath("$.title")
                                .value(
                                        "Authentication failed"))
                .andExpect(
                        jsonPath("$.detail")
                                .value(
                                        "Username or password is invalid"));
    }
}
