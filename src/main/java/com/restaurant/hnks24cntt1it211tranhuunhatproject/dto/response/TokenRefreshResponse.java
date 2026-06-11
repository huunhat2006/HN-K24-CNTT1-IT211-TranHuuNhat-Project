package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}