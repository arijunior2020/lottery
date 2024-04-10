package com.lottery.marketplace.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private UUID id_user;
    private String name;
    private String email;
    private UserRole role;
    private String token;
}
