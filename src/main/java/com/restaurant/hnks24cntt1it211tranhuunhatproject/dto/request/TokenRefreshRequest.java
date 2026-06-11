package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh Token không được để trống")
    private String refreshToken;
}