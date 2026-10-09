package com.education.marketplace_3D.service;

import com.education.marketplace_3D.dto.JwtAuthResponse;
import com.education.marketplace_3D.dto.UserLoginDto;
import com.education.marketplace_3D.dto.UserProfileDto;
import com.education.marketplace_3D.dto.UserRegistrationDto;
import com.education.marketplace_3D.entities.RefreshToken;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.entities.Wallet;
import com.education.marketplace_3D.enums.UserRole;
import com.education.marketplace_3D.event.UserRegisterEvent;
import com.education.marketplace_3D.repository.UserRepository;
import com.education.marketplace_3D.repository.WalletRepository;
import com.education.marketplace_3D.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void registerUser(UserRegistrationDto dto){

        if(userRepository.findByEmail(dto.email()).isPresent()){
            throw new IllegalArgumentException("Користувач з таким емейлом вже існує.");
        }
        User newUser = new User(
                dto.displayName(),
                dto.email(),
                passwordEncoder.encode(dto.password()),
                UserRole.CUSTOMER);
        userRepository.save(newUser);
        eventPublisher.publishEvent(new UserRegisterEvent(newUser));
    }

    public JwtAuthResponse loginUser(UserLoginDto dto){
        User user = userRepository
                .findByEmail(dto.email())
                .orElseThrow(()->new IllegalArgumentException("Невірний email або пароль."));
        if(!(passwordEncoder.matches(dto.password(), user.getPassword()))){
            throw new IllegalArgumentException("Невірний email або пароль.");
        }
        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtAuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User not found"));
        return new UserProfileDto(user.getId(), user.getEmail(), user.getDisplayName(), walletService.getBalanceByUserId(user.getId()), user.getRole());
    }

}
