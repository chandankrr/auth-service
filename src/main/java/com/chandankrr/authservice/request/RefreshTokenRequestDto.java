package com.chandankrr.authservice.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RefreshTokenRequestDto {
    private String token;
}
