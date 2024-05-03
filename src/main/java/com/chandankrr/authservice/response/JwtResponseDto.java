package com.chandankrr.authservice.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class JwtResponseDto {
    private String accessToken;
    private String token;
}
