package com.bussin.bussin_api.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;

class SecurityConfigTest {

    @Test
    void corsAllowsPatchForStaffPaymentUpdates() {
        SecurityConfig securityConfig = new SecurityConfig(
                mock(FirebaseAuthenticationFilter.class));

        CorsConfiguration cors = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(new MockHttpServletRequest(
                        "OPTIONS",
                        "/api/bookings/admin/17/payment-status"));

        assertTrue(cors.getAllowedMethods().contains("PATCH"));
    }
}
