package com.example.employeemanagement.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemStatusScheduler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(SystemStatusScheduler.class);

    @Scheduled(
            fixedRateString =
                    "${app.scheduling.system-status.fixed-rate-ms:30000}",
            initialDelayString =
                    "${app.scheduling.system-status.initial-delay-ms:30000}")
    public void logSystemStatus() {
        LOGGER.info("System running");
    }
}