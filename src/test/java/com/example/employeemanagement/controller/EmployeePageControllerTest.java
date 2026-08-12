package com.example.employeemanagement.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;

import com.example.employeemanagement.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeePageControllerTest {

    private final MockMvc mockMvc;
    private final DepartmentRepository departmentRepository;

    @Autowired
    EmployeePageControllerTest(
            MockMvc mockMvc,
            DepartmentRepository departmentRepository) {
        this.mockMvc = mockMvc;
        this.departmentRepository = departmentRepository;
    }

    @Test
    void listPageRendersEmployees()
            throws Exception {

        mockMvc.perform(get("/employees/list"))
                .andExpect(status().isOk())
                .andExpect(view().name(
                        "employees/list"))
                .andExpect(model().attributeExists(
                        "employees",
                        "departments",
                        "searchName"))
                .andExpect(content().string(
                        containsString(
                                "Danh sách nhân viên")));
    }

    @Test
    void addPageRendersForm()
            throws Exception {

        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name(
                        "employees/form"))
                .andExpect(model().attributeExists(
                        "employeeForm",
                        "departments"))
                .andExpect(content().string(
                        containsString(
                                "Thêm nhân viên")));
    }

    @Test
    void invalidFormRendersFieldErrors()
            throws Exception {

        mockMvc.perform(
                        post("/employees/add")
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", " ")
                                .param(
                                        "email",
                                        "not-an-email")
                                .param(
                                        "departmentId",
                                        ""))
                .andExpect(status().isOk())
                .andExpect(view().name(
                        "employees/form"))
                .andExpect(model()
                        .attributeHasFieldErrors(
                                "employeeForm",
                                "name",
                                "email",
                                "departmentId"));
    }

    @Test
    void createThenSearchEmployee()
            throws Exception {

        Long departmentId = engineeringId();

        String email =
                "mvc-%s@example.com"
                        .formatted(UUID.randomUUID());

        mockMvc.perform(
                        post("/employees/add")
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED)
                                .param(
                                        "name",
                                        "pham thi lan")
                                .param("email", email)
                                .param(
                                        "departmentId",
                                        departmentId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/employees/list"))
                .andExpect(flash().attribute(
                        "successMessage",
                        "Employee created successfully"));

        mockMvc.perform(
                        get("/employees/list")
                                .param("name", "pham")
                                .param(
                                        "departmentId",
                                        departmentId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(email)));
    }

    @Test
    void duplicateEmailRendersFormError()
            throws Exception {

        Long departmentId = engineeringId();

        String email =
                "duplicate-mvc-%s@example.com"
                        .formatted(UUID.randomUUID());

        createEmployee(
                "Nguyen Van An",
                email,
                departmentId);

        mockMvc.perform(
                        post("/employees/add")
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED)
                                .param(
                                        "name",
                                        "Tran Thi Binh")
                                .param("email", email)
                                .param(
                                        "departmentId",
                                        departmentId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(
                        "employees/form"))
                .andExpect(model()
                        .attributeHasFieldErrors(
                                "employeeForm",
                                "email"))
                .andExpect(content().string(
                        containsString(
                                "Employee email already exists")));
    }

    private void createEmployee(
            String name,
            String email,
            Long departmentId)
            throws Exception {

        mockMvc.perform(
                        post("/employees/add")
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", name)
                                .param("email", email)
                                .param(
                                        "departmentId",
                                        departmentId.toString()))
                .andExpect(status().is3xxRedirection());
    }

    private Long engineeringId() {
        return departmentRepository
                .findByNameIgnoreCase("Engineering")
                .orElseThrow()
                .getId();
    }
}