package com.example.employeemanagement.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class ProfileConfigurationTest {

    private final YamlPropertySourceLoader loader =
            new YamlPropertySourceLoader();

    @Test
    void devIsTheDefaultProfile()
            throws IOException {

        PropertySource<?> base =
                load("application.yml");

        assertThat(
                base.getProperty(
                        "spring.profiles.default"))
                .isEqualTo("dev");

        assertThat(
                base.getProperty(
                        "spring.jpa.open-in-view"))
                .isEqualTo(false);
    }

    @Test
    void devAndProdUseDifferentConfigurations()
            throws IOException {

        PropertySource<?> dev =
                load("application-dev.yml");

        PropertySource<?> prod =
                load("application-prod.yml");

        assertThat(
                dev.getProperty(
                                "spring.datasource.url")
                        .toString())
                .startsWith("jdbc:h2:");

        assertThat(
                dev.getProperty(
                        "spring.h2.console.enabled"))
                .isEqualTo(true);

        assertThat(
                dev.getProperty(
                        "spring.jpa.hibernate.ddl-auto"))
                .isEqualTo("create-drop");

        assertThat(
                dev.getProperty(
                        "logging.level.com.example.employeemanagement"))
                .isEqualTo("DEBUG");

        assertThat(
                prod.getProperty(
                                "spring.datasource.url")
                        .toString())
                .startsWith("jdbc:postgresql:");

        assertThat(
                prod.getProperty(
                        "spring.h2.console.enabled"))
                .isEqualTo(false);

        assertThat(
                prod.getProperty(
                        "spring.jpa.hibernate.ddl-auto"))
                .isEqualTo("validate");

        assertThat(
                prod.getProperty(
                        "logging.level.com.example.employeemanagement"))
                .isEqualTo("INFO");
    }

    private PropertySource<?> load(String fileName)
            throws IOException {

        return loader.load(
                        fileName,
                        new ClassPathResource(fileName))
                .get(0);
    }
}