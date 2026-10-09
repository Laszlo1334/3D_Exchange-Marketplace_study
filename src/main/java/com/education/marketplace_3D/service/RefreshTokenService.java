package com.education.marketplace_3D.service;

import com.education.marketplace_3D.entities.RefreshToken;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken (User user){
        RefreshToken userToken = new RefreshToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusDays(7),
                user
        );
        refreshTokenRepository.save(userToken);
        return userToken;
    }
}
