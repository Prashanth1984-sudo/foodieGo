package com.foodiego.foodiego.repository;

import com.foodiego.foodiego.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Search users by name or email
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email);

    // Count admins
    long countByRole(String role);

    // Count active/blocked users
    long countByEnabled(boolean enabled);
}