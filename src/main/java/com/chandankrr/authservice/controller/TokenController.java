package com.chandankrr.authservice.controller;

import com.chandankrr.authservice.dto.AuthenticationResponse;
import com.chandankrr.authservice.dto.RefreshTokenRequest;
import com.chandankrr.authservice.service.JwtService;
import com.chandankrr.authservice.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Operation(summary = "Generate a new access token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generated new access token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        logger.info("Attempting to generate access token from refresh token: {}", refreshTokenRequest.getRefreshToken());
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    String token = jwtService.generateToken(refreshToken.getUserInfo().getEmail());
                    logger.info("Generated access token: {}", token);
                    return ResponseEntity.ok(new AuthenticationResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
