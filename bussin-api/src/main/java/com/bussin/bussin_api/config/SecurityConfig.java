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
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .formLogin(formLogin -> formLogin.disable())

                                .httpBasic(httpBasic -> httpBasic.disable())

                                .logout(logout -> logout.disable())

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/api/health")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/users")
                                                .authenticated()

                                                .requestMatchers(
                                                                "/api/users/me")
                                                .authenticated()

                                                .requestMatchers(
                                                                "/api/users/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/buses/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                "/api/buses/**")
                                                .hasAnyRole("ADMIN", "EMPLOYEE")

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
                                                .hasAnyRole("ADMIN", "EMPLOYEE", "COMMUTER")

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
                                                .hasAnyRole("ADMIN", "EMPLOYEE", "COMMUTER")

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
                                                .hasAnyRole("ADMIN", "EMPLOYEE", "COMMUTER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/queue/**")
                                                .hasRole("COMMUTER")

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/ai/chat")
                                                .hasRole("COMMUTER")

                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                firebaseAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}