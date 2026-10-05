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
import com.bussin.bussin_api.exception.ConflictException;
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
        // GET EMPLOYEES
        // ADMIN ONLY
        // ============================================================

        public List<UserResponse> getEmployees() {
                return userRepository.findAll()
                                .stream()
                                .filter(user -> user.getRole() == Role.EMPLOYEE)
                                .map(this::toResponse)
                                .toList();
        }

        // ============================================================
        // UPDATE MANAGED USER
        // ADMIN ONLY
        // ============================================================

        public UserResponse updateManagedUser(
                        Long userId,
                        UpdateUserRequest request) {

                User user = userRepository
                                .findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                user.setFirstName(request.getFirstName());
                user.setMiddleName(request.getMiddleName());
                user.setLastName(request.getLastName());
                user.setContactNumber(request.getContactNumber());
                user.setAddress(request.getAddress());
                user.setUpdatedAt(LocalDateTime.now());

                return toResponse(userRepository.save(user));
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

                if (currentRole == Role.ADMIN &&
                                newRole != Role.ADMIN) {

                        long adminCount = userRepository.findAll()
                                        .stream()
                                        .filter(existingUser -> existingUser.getRole() == Role.ADMIN)
                                        .count();

                        if (adminCount <= 1) {

                                throw new ConflictException(
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

                System.out.println("========================================");
                System.out.println("DEBUG: createUser() WAS CALLED");
                System.out.println("DEBUG REQUEST VALUES:");
                System.out.println("firstName     = [" + request.getFirstName() + "]");
                System.out.println("middleName    = [" + request.getMiddleName() + "]");
                System.out.println("lastName      = [" + request.getLastName() + "]");
                System.out.println("gender        = [" + request.getGender() + "]");
                System.out.println("age           = [" + request.getAge() + "]");
                System.out.println("dateOfBirth   = [" + request.getDateOfBirth() + "]");
                System.out.println("contactNumber = [" + request.getContactNumber() + "]");
                System.out.println("address       = [" + request.getAddress() + "]");
                System.out.println("========================================");

                FirebaseToken firebaseToken = getAuthenticatedFirebaseToken();

                String firebaseUid = firebaseToken.getUid();

                String email = firebaseToken.getEmail();

                validateUserDoesNotExist(firebaseUid);

                User user = new User();

                user.setFirebaseUid(firebaseUid);
                user.setEmail(email);

                user.setFirstName(
                                request.getFirstName());

                user.setMiddleName(
                                request.getMiddleName());

                user.setLastName(
                                request.getLastName());

                user.setGender(
                                request.getGender());

                user.setAge(
                                request.getAge());

                user.setDateOfBirth(
                                request.getDateOfBirth());

                user.setContactNumber(
                                request.getContactNumber());

                user.setAddress(
                                request.getAddress());

                user.setRole(
                                Role.COMMUTER);

                LocalDateTime now = LocalDateTime.now();

                user.setCreatedAt(now);
                user.setUpdatedAt(now);

                System.out.println("DEBUG ENTITY VALUES BEFORE SAVE:");
                System.out.println("firstName     = [" + user.getFirstName() + "]");
                System.out.println("middleName    = [" + user.getMiddleName() + "]");
                System.out.println("lastName      = [" + user.getLastName() + "]");
                System.out.println("gender        = [" + user.getGender() + "]");
                System.out.println("age           = [" + user.getAge() + "]");
                System.out.println("dateOfBirth   = [" + user.getDateOfBirth() + "]");
                System.out.println("contactNumber = [" + user.getContactNumber() + "]");
                System.out.println("address       = [" + user.getAddress() + "]");
                System.out.println("========================================");

                User savedUser = userRepository.save(user);

                System.out.println("DEBUG ENTITY VALUES AFTER SAVE:");
                System.out.println("firstName     = [" + savedUser.getFirstName() + "]");
                System.out.println("middleName    = [" + savedUser.getMiddleName() + "]");
                System.out.println("lastName      = [" + savedUser.getLastName() + "]");
                System.out.println("gender        = [" + savedUser.getGender() + "]");
                System.out.println("age           = [" + savedUser.getAge() + "]");
                System.out.println("dateOfBirth   = [" + savedUser.getDateOfBirth() + "]");
                System.out.println("contactNumber = [" + savedUser.getContactNumber() + "]");
                System.out.println("address       = [" + savedUser.getAddress() + "]");
                System.out.println("========================================");

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

                user.setFirstName(
                                request.getFirstName());

                user.setMiddleName(
                                request.getMiddleName());

                user.setLastName(
                                request.getLastName());

                user.setContactNumber(
                                request.getContactNumber());

                user.setAddress(
                                request.getAddress());

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

        private UserResponse toResponse(
                        User user) {

                return new UserResponse(
                                user.getId(),
                                user.getFirebaseUid(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getMiddleName(),
                                user.getLastName(),
                                user.getGender(),
                                user.getAge(),
                                user.getDateOfBirth(),
                                user.getContactNumber(),
                                user.getAddress(),
                                user.getRole().name(),
                                user.getCreatedAt(),
                                user.getUpdatedAt());
        }
}