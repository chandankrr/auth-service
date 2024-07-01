package com.chandankrr.authservice.controller;

import com.chandankrr.authservice.entity.RefreshToken;
import com.chandankrr.authservice.dto.AuthRequestDto;
import com.chandankrr.authservice.dto.RefreshTokenRequestDto;
import com.chandankrr.authservice.dto.JwtResponseDto;
import com.chandankrr.authservice.service.JwtService;
import com.chandankrr.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class TokenController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.username(), authRequestDto.password()));
            if (authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.username());
                return ResponseEntity.ok(new JwtResponseDto(
                        jwtService.generateToken(authRequestDto.username()),
                        refreshToken.getToken()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred in user service");
        }
    }

    @PostMapping("/refreshToken")
    public JwtResponseDto refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return refreshTokenService.findByToken(refreshTokenRequestDto.token())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> new JwtResponseDto(
                        jwtService.generateToken(userInfo.getUsername()),
                        refreshTokenRequestDto.token())).orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }
}
