package com.rahulsmgv.cbs.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central Spring Security auto-configuration for the
 * RahulSMGV Core Banking System.
 *
 * This configuration is packaged inside the shared
 * rahulsmgv-cbs-security library.
 *
 * Any CBS microservice that includes the security library
 * receives this configuration automatically.
 *
 * This prevents every microservice from maintaining its own
 * duplicate security configuration.
 */
@AutoConfiguration
public class CbsSecurityAutoConfiguration {

    /**
     * Creates the common Spring Security filter chain.
     *
     * Current development behavior:
     *
     * - CSRF is disabled for REST APIs.
     * - CBS API endpoints are temporarily permitted.
     * - Health endpoints are publicly accessible.
     *
     * Authentication and authorization will be enabled
     * when the CBS identity/security infrastructure is integrated.
     *
     * @param http Spring Security HTTP configuration
     * @return configured security filter chain
     * @throws Exception when security configuration fails
     */
    @Bean
    public SecurityFilterChain cbsSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                /*
                 * Disable CSRF for REST APIs.
                 *
                 * The current CBS services communicate through
                 * REST APIs rather than browser session forms.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * Configure authorization rules.
                 */
                .authorizeHttpRequests(auth -> auth

                        /*
                         * CBS REST APIs are temporarily accessible
                         * without authentication during development.
                         */
                        .requestMatchers("/api/**")
                        .permitAll()

                        /*
                         * Health endpoints must be accessible by
                         * monitoring systems and Kubernetes/OpenShift
                         * health probes.
                         */
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**")
                        .permitAll()

                        /*
                         * Anything else requires authentication.
                         */
                        .anyRequest()
                        .authenticated());

        return http.build();
    }
}