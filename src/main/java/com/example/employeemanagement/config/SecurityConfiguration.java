package com.example.employeemanagement.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration
                .getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter
                    jwtAuthenticationConverter)
            throws Exception {

        http
                .securityMatcher("/api/**")

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/api/auth/**")
                                .permitAll()

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/**")
                                .hasAnyRole(
                                        "USER",
                                        "ADMIN")

                                .anyRequest()
                                .hasRole("ADMIN"))

                .httpBasic(withDefaults())

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter)));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/",
                                        "/login",
                                        "/register",
                                        "/error")
                                .permitAll()

                                .requestMatchers(
                                        PathRequest
                                                .toStaticResources()
                                                .atCommonLocations())
                                .permitAll()

                                .requestMatchers(
                                        PathRequest.toH2Console())
                                .permitAll()

                                .requestMatchers(
                                        EndpointRequest.to(
                                                "health",
                                                "info"))
                                .permitAll()

                                .requestMatchers(
                                        EndpointRequest
                                                .toAnyEndpoint())
                                .hasRole("ADMIN")

                                .requestMatchers(
                                        "/employees",
                                        "/employees/list")
                                .hasAnyRole(
                                        "USER",
                                        "ADMIN")

                                .requestMatchers(
                                        "/employees/**")
                                .hasRole("ADMIN")

                                .anyRequest()
                                .authenticated())

                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .defaultSuccessUrl(
                                        "/employees/list",
                                        true)
                                .permitAll())

                .logout(logout ->
                        logout
                                .logoutSuccessUrl(
                                        "/login?logout")
                                .permitAll())

                .csrf(csrf ->
                        csrf.ignoringRequestMatchers(
                                PathRequest.toH2Console()))

                .headers(headers ->
                        headers.frameOptions(
                                frame ->
                                        frame.sameOrigin()));

        return http.build();
    }
}