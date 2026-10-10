package com.education.marketplace_3D.service;

import com.education.marketplace_3D.dto.JwtAuthResponse;
import com.education.marketplace_3D.entities.RefreshToken;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.exception.TokenRefreshException;
import com.education.marketplace_3D.repository.RefreshTokenRepository;
import com.education.marketplace_3D.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken (User user){
        RefreshToken userToken = new RefreshToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusDays(7),
                user
        );
        refreshTokenRepository.save(userToken);
        return userToken;
    }

    @Transactional
    public JwtAuthResponse refreshAccessToken (String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException("Refresh token was expired. Please make a new signin request"));
        if(refreshToken.getExpiredAt().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }
        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        RefreshToken newRefreshToken = createRefreshToken(user);

        return new JwtAuthResponse(accessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void logoutUser (String requestRefreshToken){
        refreshTokenRepository.findByToken(requestRefreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void deleteByUser (User user){
        refreshTokenRepository.deleteByUser(user);
    }

}
