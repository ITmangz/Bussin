package com.bussin.bussin_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String firebaseUid;
    private String email;

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private Integer age;
    private LocalDate dateOfBirth;
    private String contactNumber;

    private String role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse(
            Long id,
            String firebaseUid,
            String email,
            String firstName,
            String middleName,
            String lastName,
            String gender,
            Integer age,
            LocalDate dateOfBirth,
            String contactNumber,
            String role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.firebaseUid = firebaseUid;
        this.email = email;

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;

        this.gender = gender;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;

        this.role = role;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}