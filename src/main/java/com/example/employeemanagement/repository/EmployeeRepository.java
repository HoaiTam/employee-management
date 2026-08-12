package com.example.employeemanagement.repository;

import java.util.List;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    List<Employee> findAllByOrderByIdAsc();

    List<Employee> findByNameContainingIgnoreCaseOrderByIdAsc(
            String name);

    List<Employee> findByDepartment_IdOrderByIdAsc(
            Long departmentId);

    List<Employee>
    findByNameContainingIgnoreCaseAndDepartment_IdOrderByIdAsc(
            String name,
            Long departmentId);
}