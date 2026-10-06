package com.bussin.bussin_api.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.repository.BusRepository;
import com.bussin.bussin_api.repository.RouteRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripService tripService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void employeeTripListUsesVerifiedFirebaseUidFromPrincipal() {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("employee-firebase-uid");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(token, null, List.of()));

        User employee = mock(User.class);
        when(employee.getId()).thenReturn(27L);
        when(employee.getRole()).thenReturn(Role.EMPLOYEE);
        when(userRepository.findByFirebaseUid("employee-firebase-uid"))
                .thenReturn(Optional.of(employee));
        when(tripRepository.findAssignedToEmployee(27L)).thenReturn(List.of());

        assertTrue(tripService.getAllTrips(null, null, null, null).isEmpty());

        verify(userRepository).findByFirebaseUid("employee-firebase-uid");
        verify(tripRepository).findAssignedToEmployee(27L);
    }
}
