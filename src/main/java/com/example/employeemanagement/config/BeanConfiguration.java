package com.example.employeemanagement.config;

import com.example.employeemanagement.util.EmployeeCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public EmployeeCodeGenerator employeeCodeGenerator() {
        return new EmployeeCodeGenerator("EMP");
    }
}