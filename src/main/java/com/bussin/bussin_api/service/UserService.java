package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bussin.bussin_api.dto.CreateUserRequest;
import com.bussin.bussin_api.dto.UpdateUserRequest;
import com.bussin.bussin_api.dto.UpdateUserRoleRequest;
import com.bussin.bussin_api.dto.UserResponse;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ============================================================
    // GET ALL USERS
    // ============================================================

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================================
    // UPDATE USER ROLE
    // ADMIN ONLY
    // ============================================================

    public UserResponse updateUserRole(
            Long userId,
            UpdateUserRoleRequest request) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"));

        Role currentRole = user.getRole();

        Role newRole = request.getRole();

        // --------------------------------------------------------
        // Prevent removing the last administrator.
        // --------------------------------------------------------

        if (currentRole == Role.ADMIN &&
                newRole != Role.ADMIN) {

            long adminCount = userRepository.findAll()
                    .stream()
                    .filter(existingUser -> existingUser.getRole() == Role.ADMIN)
                    .count();

            if (adminCount <= 1) {

                throw new IllegalArgumentException(
                        "Cannot remove the last administrator");
            }
        }

        user.setRole(newRole);

        user.setUpdatedAt(
                LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    // ============================================================
    // GET CURRENT USER
    // ============================================================

    public UserResponse getCurrentUser() {

        FirebaseToken firebaseToken = getAuthenticatedFirebaseToken();

        String firebaseUid = firebaseToken.getUid();

        User user = userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException(
                        "BUSSIN user profile not found"));

        return toResponse(user);
    }

    // ============================================================
    // CREATE USER
    // ============================================================

    public UserResponse createUser(
            CreateUserRequest request) {

        FirebaseToken firebaseToken = getAuthenticatedFirebaseToken();

        String firebaseUid = firebaseToken.getUid();

        String email = firebaseToken.getEmail();

        validateUserDoesNotExist(firebaseUid);

        User user = new User();

        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Every newly registered user starts as a commuter.
        user.setRole(Role.COMMUTER);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    // ============================================================
    // UPDATE CURRENT USER
    // ============================================================

    public UserResponse updateCurrentUser(
            UpdateUserRequest request) {

        FirebaseToken firebaseToken = getAuthenticatedFirebaseToken();

        String firebaseUid = firebaseToken.getUid();

        User user = userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException(
                        "BUSSIN user profile not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user.setUpdatedAt(
                LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    // ============================================================
    // GET AUTHENTICATED FIREBASE USER
    // ============================================================

    private FirebaseToken getAuthenticatedFirebaseToken() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalArgumentException(
                    "User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof FirebaseToken firebaseToken)) {

            throw new IllegalArgumentException(
                    "Invalid Firebase authentication");
        }

        return firebaseToken;
    }

    // ============================================================
    // CHECK IF USER ALREADY EXISTS
    // ============================================================

    private void validateUserDoesNotExist(
            String firebaseUid) {

        if (userRepository
                .findByFirebaseUid(firebaseUid)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "A user with this Firebase UID already exists");
        }
    }

    // ============================================================
    // ENTITY → RESPONSE DTO
    // ============================================================

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirebaseUid(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}