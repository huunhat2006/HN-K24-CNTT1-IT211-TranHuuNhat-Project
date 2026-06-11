package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long id;
    private String username;
    private String role;
}