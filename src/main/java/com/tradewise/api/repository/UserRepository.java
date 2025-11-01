package com.tradewise.api.repository;

import com.tradewise.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Spring Data JPA will automatically create a query for us
    // that finds a User by their email column.
    Optional<User> findByEmail(String email);
}