package com.lottery.marketplace.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    @Query("select u from User u where u.email = ?1")
    Optional<User> findByUserEmailOpt(String email);
}