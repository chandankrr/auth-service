package com.chandankrr.authservice.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthRequestDto {
    private String username;
    private String password;
}
