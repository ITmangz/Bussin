package com.bussin.bussin_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.bussin_api.dto.CreateUserRequest;
import com.bussin.bussin_api.dto.UpdateUserRequest;
import com.bussin.bussin_api.dto.UpdateUserRoleRequest;
import com.bussin.bussin_api.dto.UserResponse;
import com.bussin.bussin_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ============================================================
    // GET CURRENT USER
    // ============================================================

    @GetMapping("/me")
    public UserResponse getCurrentUser() {

        return userService.getCurrentUser();
    }

    // ============================================================
    // UPDATE CURRENT USER
    // ============================================================

    @PutMapping("/me")
    public UserResponse updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request) {

        return userService.updateCurrentUser(request);
    }

    // ============================================================
    // GET ALL USERS
    // ADMIN ONLY
    // ============================================================

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    // ============================================================
    // CREATE USER PROFILE
    // ============================================================

    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request) {

        return userService.createUser(request);
    }

    // ============================================================
    // UPDATE USER ROLE
    // ADMIN ONLY
    // ============================================================

    @PutMapping("/{userId}/role")
    public UserResponse updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        return userService.updateUserRole(
                userId,
                request);
    }
}