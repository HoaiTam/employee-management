package com.example.employeemanagement.controller;

import java.util.List;

import com.example.employeemanagement.dto.CreateEmployeeRequest;
import com.example.employeemanagement.dto.DepartmentResponse;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.service.DepartmentService;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeePageController {

    private static final String FORM_VIEW =
            "employees/form";

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public EmployeePageController(
            EmployeeService employeeService,
            DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @ModelAttribute("departments")
    public List<DepartmentResponse> departments() {
        return departmentService.findAll();
    }

    @GetMapping
    public String redirectToList() {
        return "redirect:/employees/list";
    }

    @GetMapping("/list")
    public String list(
            @RequestParam(
                    name = "name",
                    required = false)
            String name,
            @RequestParam(
                    name = "departmentId",
                    required = false)
            Long departmentId,
            Model model) {

        model.addAttribute(
                "employees",
                employeeService.findAll(
                        name,
                        departmentId));

        model.addAttribute(
                "searchName",
                name == null ? "" : name);

        model.addAttribute(
                "selectedDepartmentId",
                departmentId);

        return "employees/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute(
                "employeeForm",
                new CreateEmployeeRequest(
                        "",
                        "",
                        null));

        return FORM_VIEW;
    }

    @PostMapping("/add")
    public String create(
            @Valid
            @ModelAttribute("employeeForm")
            CreateEmployeeRequest employeeForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }

        try {
            employeeService.create(employeeForm);
        } catch (DuplicateResourceException exception) {
            bindingResult.rejectValue(
                    "email",
                    "duplicate",
                    exception.getMessage());

            return FORM_VIEW;
        } catch (ResourceNotFoundException exception) {
            bindingResult.rejectValue(
                    "departmentId",
                    "notFound",
                    exception.getMessage());

            return FORM_VIEW;
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Employee created successfully");

        return "redirect:/employees/list";
    }
}