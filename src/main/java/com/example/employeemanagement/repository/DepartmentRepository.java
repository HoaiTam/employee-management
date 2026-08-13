package com.example.employeemanagement.repository;

import java.util.List;
import java.util.Optional;

import com.example.employeemanagement.dto.DepartmentEmployeeCountResponse;
import com.example.employeemanagement.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    Optional<Department> findByNameIgnoreCase(String name);

    List<Department> findAllByOrderByNameAsc();

    @Query("""
            select new com.example.employeemanagement.dto.DepartmentEmployeeCountResponse(
                    department.id,
                    department.name,
                    count(employee.id))
            from Department department
            left join Employee employee
                    on employee.department = department
            group by department.id, department.name
            order by department.name
            """)
    List<DepartmentEmployeeCountResponse>
    countEmployeesByDepartment();
}
