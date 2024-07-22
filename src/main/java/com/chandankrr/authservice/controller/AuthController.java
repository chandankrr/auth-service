package com.chandankrr.authservice.controller;

import com.chandankrr.authservice.dto.AuthenticationRequest;
import com.chandankrr.authservice.dto.AuthenticationResponse;
import com.chandankrr.authservice.dto.UserInfoDto;
import com.chandankrr.authservice.entity.RefreshToken;
import com.chandankrr.authservice.service.JwtService;
import com.chandankrr.authservice.service.RefreshTokenService;
import com.chandankrr.authservice.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Sign up a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User already exists")
    })
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

    @Operation(summary = "Log in an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
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

    @Operation(summary = "Ping to check if the user is logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("Attempting to ping user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String userId = userDetailsService.getUserByUsername(userDetails.getUsername());
            logger.info("Logged in user: {} with user id: {}", userDetails.getUsername(), userId);
            return ResponseEntity.ok(userId);
        } else {
            logger.info("Logged in user not found");
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

}
