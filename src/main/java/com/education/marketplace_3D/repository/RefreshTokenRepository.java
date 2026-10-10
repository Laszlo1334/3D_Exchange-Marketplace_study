package com.education.marketplace_3D.repository;

import com.education.marketplace_3D.entities.RefreshToken;
import com.education.marketplace_3D.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String requestRefreshToken);
    void deleteByUser (User user);
}
