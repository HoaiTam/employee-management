package com.example.employeemanagement.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class SystemStatusSchedulerTest {

    @Test
    void logsSystemRunning(
            CapturedOutput output) {

        SystemStatusScheduler scheduler =
                new SystemStatusScheduler();

        scheduler.logSystemStatus();

        assertThat(output)
                .contains("System running");
    }
}