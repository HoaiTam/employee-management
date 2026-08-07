package com.example.employeemanagement.util;

import java.util.concurrent.atomic.AtomicLong;

public final class EmployeeCodeGenerator {

    private final String prefix;
    private final AtomicLong sequence = new AtomicLong();

    public EmployeeCodeGenerator(String prefix) {
        this.prefix = prefix;
    }

    public String nextCode() {
        long nextValue = sequence.incrementAndGet();
        return "%s-%04d".formatted(prefix, nextValue);
    }
}