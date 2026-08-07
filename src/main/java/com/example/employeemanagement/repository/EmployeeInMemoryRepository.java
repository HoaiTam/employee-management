package com.example.employeemanagement.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.example.employeemanagement.model.Employee;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeInMemoryRepository {

    private final Map<Long, Employee> employees =
            new ConcurrentHashMap<>();

    private final AtomicLong idSequence = new AtomicLong();

    public List<Employee> findAll() {
        return employees.values()
                .stream()
                .sorted(Comparator.comparing(Employee::id))
                .toList();
    }

    public Optional<Employee> findById(long id) {
        return Optional.ofNullable(employees.get(id));
    }

    public Employee save(Employee employee) {
        long id = idSequence.incrementAndGet();

        Employee savedEmployee = new Employee(
                id,
                employee.code(),
                employee.name(),
                employee.email(),
                employee.department());

        employees.put(id, savedEmployee);
        return savedEmployee;
    }
}