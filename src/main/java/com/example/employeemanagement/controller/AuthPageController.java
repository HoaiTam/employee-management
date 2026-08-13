package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.RegisterUserRequest;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPageController {

    private final UserAccountService userAccountService;

    public AuthPageController(
            UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(
            Model model) {

        model.addAttribute(
                "registrationForm",
                new RegisterUserRequest(
                        "",
                        ""));

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid
            @ModelAttribute("registrationForm")
            RegisterUserRequest registrationForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userAccountService.register(
                    registrationForm);
        } catch (DuplicateResourceException exception) {
            bindingResult.rejectValue(
                    "username",
                    "duplicate",
                    exception.getMessage());

            return "auth/register";
        }

        redirectAttributes.addFlashAttribute(
                "registrationSuccess",
                true);

        return "redirect:/login?registered";
    }
}