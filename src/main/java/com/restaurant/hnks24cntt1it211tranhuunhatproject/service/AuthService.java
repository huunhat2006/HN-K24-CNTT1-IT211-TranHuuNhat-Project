package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.LoginRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.TokenRefreshRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.ChangePasswordRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.ForgotPasswordRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.JwtResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenRefreshResponse;

public interface AuthService {
    JwtResponse login(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    void logout(String headerAuth);

    // MỚI
    void changePassword(String username, ChangePasswordRequest request);
    // MỚI
    void forgotPassword(ForgotPasswordRequest request);
}