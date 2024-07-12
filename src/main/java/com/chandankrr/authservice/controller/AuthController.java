package com.chandankrr.authservice.controller;

import com.chandankrr.authservice.dto.AuthenticationRequest;
import com.chandankrr.authservice.dto.AuthenticationResponse;
import com.chandankrr.authservice.dto.UserInfoDto;
import com.chandankrr.authservice.entity.RefreshToken;
import com.chandankrr.authservice.service.JwtService;
import com.chandankrr.authservice.service.RefreshTokenService;
import com.chandankrr.authservice.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserInfoDto userInfoDto) {
        logger.info("Attempting to sign up user with username: {}", userInfoDto.getUsername());
        Boolean isSignedUp = userDetailsService.signupUser(userInfoDto);
        if (isSignedUp) {
            logger.info("User signed up successfully with username: {}", userInfoDto.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } else {
            logger.warn("Signup failed for username: {}", userInfoDto.getUsername());
            return ResponseEntity.badRequest().body("User already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            logger.info("Attempting to login user with username: {}", authenticationRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(userDetails.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

            logger.info("User logged in successfully with username: {}", authenticationRequest.getUsername());
            return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken.getToken()));
        } catch (AuthenticationException e) {
            logger.error("Login failed for username: {}", authenticationRequest.getUsername(), e);
            return ResponseEntity.status(401).body(null);
        }
    }

}
