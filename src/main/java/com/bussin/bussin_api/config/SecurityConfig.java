package com.bussin.bussin_api.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                                .csrf(csrf -> csrf.disable())

                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .formLogin(formLogin -> formLogin.disable())

                                .httpBasic(httpBasic -> httpBasic.disable())

                                .logout(logout -> logout.disable())

                                .authorizeHttpRequests(auth -> auth

                                                // ------------------------------------------------
                                                // CORS preflight
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                // ------------------------------------------------
                                                // Public
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                "/api/health")
                                                .permitAll()

                                                // Public trip listing
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/trips",
                                                                "/api/routes",
                                                                "/api/bookings/trip/*/seats")
                                                .permitAll()

                                                // Public individual route
                                                // Required by commuter/guest booking map
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/routes/*")
                                                .permitAll()

                                                // Guest booking
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/bookings/guest")
                                                .permitAll()

                                                // Guest fare calculation
                                                // Required when a guest selects a
                                                // drop-off point on the map.
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/routes/*/fare-quote")
                                                .permitAll()

                                                // ------------------------------------------------
                                                // User registration
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/users")
                                                .authenticated()

                                                // ------------------------------------------------
                                                // Current user
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                "/api/users/me")
                                                .authenticated()

                                                // ------------------------------------------------
                                                // Admin user management
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                "/api/users/**")
                                                .hasRole("ADMIN")

                                                // ------------------------------------------------
                                                // Dashboard
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                "/api/dashboard/**")
                                                .hasRole("ADMIN")

                                                // ------------------------------------------------
                                                // Buses
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/buses/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                "/api/buses/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                // ------------------------------------------------
                                                // Routes
                                                // ------------------------------------------------

                                                // Delete routes
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/routes/**")
                                                .hasRole("ADMIN")

                                                // Route preview is an admin/employee
                                                // route-management operation.
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/routes/preview")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                // Create routes
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/routes/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                // Update routes
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/routes/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                // Any remaining route operation
                                                // requires authentication/role.
                                                .requestMatchers(
                                                                "/api/routes/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE",
                                                                "COMMUTER")

                                                // ------------------------------------------------
                                                // Trips
                                                // ------------------------------------------------
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/trips/employee")
                                                .hasRole("EMPLOYEE")


                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/trips/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/trips/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/trips/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                .requestMatchers(
                                                                "/api/trips/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE",
                                                                "COMMUTER")

                                                // ------------------------------------------------
                                                // Employee and administrator booking views
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                "/api/bookings/admin",
                                                                "/api/bookings/admin/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/bookings/employee")
                                                .hasRole("EMPLOYEE")

                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/bookings/employee/*/payment-status")
                                                .hasRole("EMPLOYEE")

                                                // ------------------------------------------------
                                                // Queue
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/queue/employee")
                                                .hasRole("EMPLOYEE")

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/queue")
                                                .hasRole("COMMUTER")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/queue/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/queue/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE",
                                                                "COMMUTER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/queue/**")
                                                .hasRole("COMMUTER")

                                                // ------------------------------------------------
                                                // AI
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/ai/chat")
                                                .hasRole("COMMUTER")

                                                // ------------------------------------------------
                                                // Everything else
                                                // ------------------------------------------------

                                                .anyRequest()
                                                .authenticated())

                                .addFilterBefore(
                                                firebaseAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                String configuredOrigins = System.getenv().getOrDefault(
                                "CORS_ALLOWED_ORIGINS",
                                "http://localhost:5173");

                List<String> allowedOrigins = Arrays.stream(
                                configuredOrigins.split(","))
                                .map(String::trim)
                                .filter(origin -> !origin.isBlank())
                                .toList();

                configuration.setAllowedOrigins(allowedOrigins);

                configuration.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"));

                configuration.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }
}