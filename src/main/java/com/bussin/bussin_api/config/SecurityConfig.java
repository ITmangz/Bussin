package com.bussin.bussin_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;

        public SecurityConfig(
                        FirebaseAuthenticationFilter firebaseAuthenticationFilter) {

                this.firebaseAuthenticationFilter = firebaseAuthenticationFilter;
        }

        @Bean
        public AuthenticationManager authenticationManager() {

                return authentication -> {
                        throw new UnsupportedOperationException(
                                        "BUSSIN uses Firebase Authentication");
                };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http

                                // Firebase handles authentication.
                                .csrf(csrf -> csrf.disable())

                                // No server-side sessions.
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                // Disable Spring Security's default authentication methods.
                                .formLogin(formLogin -> formLogin.disable())

                                .httpBasic(httpBasic -> httpBasic.disable())

                                .logout(logout -> logout.disable())

                                // Endpoint authorization.
                                .authorizeHttpRequests(auth -> auth

                                                // Public endpoint.
                                                .requestMatchers(
                                                                "/api/health")
                                                .permitAll()

                                                // Firebase-authenticated users can create
                                                // their BUSSIN profile.
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/users")
                                                .authenticated()

                                                // Any authenticated BUSSIN user.
                                                .requestMatchers(
                                                                "/api/users/me")
                                                .authenticated()

                                                // ADMIN only.
                                                .requestMatchers(
                                                                "/api/users/**")
                                                .hasRole("ADMIN")

                                                // Bus management.
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/buses/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                "/api/buses/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

                                                // All remaining API endpoints require authentication.
                                                .anyRequest().authenticated())

                                // Our Firebase authentication filter runs before
                                // Spring Security's username/password authentication filter.
                                .addFilterBefore(
                                                firebaseAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}