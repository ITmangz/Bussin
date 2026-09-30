package com.bussin.bussin_api.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.repository.UserRepository;
import com.bussin.bussin_api.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseAuthenticationFilter
        extends OncePerRequestFilter {

    private final FirebaseAuthService firebaseAuthService;
    private final UserRepository userRepository;

    public FirebaseAuthenticationFilter(
            FirebaseAuthService firebaseAuthService,
            UserRepository userRepository) {

        this.firebaseAuthService = firebaseAuthService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // --------------------------------------------------------
        // No Firebase token
        // --------------------------------------------------------

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String idToken = authorizationHeader.substring(7);

        try {

            // ----------------------------------------------------
            // Verify Firebase token
            // ----------------------------------------------------

            FirebaseToken firebaseToken = firebaseAuthService.verifyIdToken(idToken);

            String firebaseUid = firebaseToken.getUid();

            // ----------------------------------------------------
            // Find BUSSIN user
            // ----------------------------------------------------

            Optional<User> userOptional = userRepository.findByFirebaseUid(firebaseUid);

            /*
             * A Firebase account may exist before a BUSSIN
             * application profile has been created.
             *
             * This is allowed because POST /api/users is used
             * to create the BUSSIN profile.
             */
            if (userOptional.isEmpty()) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        firebaseToken,
                        null,
                        List.of());

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                filterChain.doFilter(request, response);
                return;
            }

            // ----------------------------------------------------
            // BUSSIN user exists
            // ----------------------------------------------------

            User user = userOptional.get();

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    "ROLE_" + user.getRole().name());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    firebaseToken,
                    null,
                    List.of(authority));

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception exception) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json");

            response.getWriter().write(
                    """
                            {
                                "status": 401,
                                "message": "Invalid authentication token"
                            }
                            """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}