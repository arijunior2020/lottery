package com.lottery.marketplace.domain.password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
  @Query("select p from PasswordResetToken p where p.tokenId = ?1 and p.userId.id= ?2")
  Optional<PasswordResetToken> findByTokenIdAndAndUserId(UUID tokenId, UUID userId);
}
