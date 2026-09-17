package com.vityarthi.cove.repository;

import com.vityarthi.cove.model.Role;
import com.vityarthi.cove.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for User entities.
 * Demonstrates:
 * - CSE2006 Unit 5: JPA Architecture, Interface inheritance, and query method derivation.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
}
