package com.bussin.bussin_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFirebaseUid(String firebaseUid);

    Optional<User> findByEmail(String email);
}