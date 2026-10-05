package com.bussin.bussin_api.config;

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

                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .formLogin(formLogin -> formLogin.disable())

                                .httpBasic(httpBasic -> httpBasic.disable())

                                .logout(logout -> logout.disable())

                                .authorizeHttpRequests(auth -> auth

                                                // ------------------------------------------------
                                                // CORS preflight
                                                // ------------------------------------------------

                                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                                .permitAll()

                                                // ------------------------------------------------
                                                // Public
                                                // ------------------------------------------------

                                                .requestMatchers("/api/health")
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

                                                .requestMatchers("/api/dashboard/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

                                                // ------------------------------------------------
                                                // Buses
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/buses/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                "/api/buses/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

                                                // ------------------------------------------------
                                                // Routes
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/routes/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/routes/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/routes/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

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
                                                                HttpMethod.DELETE,
                                                                "/api/trips/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/trips/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/trips/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

                                                .requestMatchers(
                                                                "/api/trips/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE",
                                                                "COMMUTER")

                                                // ------------------------------------------------
                                                // Queue
                                                // ------------------------------------------------

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/queue")
                                                .hasRole("COMMUTER")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/queue/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

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

                configuration.setAllowedOrigins(List.of(
                                "http://localhost:5173"));

                configuration.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
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