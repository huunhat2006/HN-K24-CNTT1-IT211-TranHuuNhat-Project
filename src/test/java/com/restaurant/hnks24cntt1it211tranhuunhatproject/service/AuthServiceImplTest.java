package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.LoginRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.TokenRefreshRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.JwtResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenRefreshResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt.JwtUtils;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    // ĐÃ SỬA: Thay thế Repository cũ bằng Service quản lý Redis mới để InjectMocks không bị lỗi
    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLogin_Success() {
        // 1. Arrange (Chuẩn bị dữ liệu giả lập)
        LoginRequest request = new LoginRequest();
        request.setUsername("nhat2006");
        request.setPassword("password123");

        User fakeUser = User.builder().id(1L).username("nhat2006").role("CUSTOMER").build();
        Authentication mockAuth = Mockito.mock(Authentication.class);

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        Mockito.when(jwtUtils.generateJwtToken(mockAuth)).thenReturn("fakeAccess");
        Mockito.when(jwtUtils.generateRefreshToken("nhat2006")).thenReturn("fakeRefresh");
        Mockito.when(userRepository.findByUsername("nhat2006")).thenReturn(Optional.of(fakeUser));

        // 2. Act (Thực thi hàm)
        JwtResponse response = authService.login(request);

        // 3. Assert (Đối chiếu kết quả)
        assertNotNull(response);
        assertEquals("fakeAccess", response.getAccessToken());
        assertEquals("nhat2006", response.getUsername());
    }

    @Test
    void testLogin_UserNotFound_ThrowsException() {
        // 1. Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("wrongUser");
        request.setPassword("password123");
        Authentication mockAuth = Mockito.mock(Authentication.class);

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        Mockito.when(userRepository.findByUsername("wrongUser")).thenReturn(Optional.empty());

        // 2. Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void testRefreshToken_Success() {
        // 1. Arrange
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("valid_refresh_token");

        Mockito.when(jwtUtils.validateJwtToken("valid_refresh_token")).thenReturn(true);
        Mockito.when(jwtUtils.getUserNameFromJwtToken("valid_refresh_token")).thenReturn("nhat2006");
        Mockito.when(jwtUtils.generateJwtToken("nhat2006")).thenReturn("new_access_token");
        Mockito.when(jwtUtils.generateRefreshToken("nhat2006")).thenReturn("new_refresh_token");

        // 2. Act
        TokenRefreshResponse response = authService.refreshToken(request);

        // 3. Assert
        assertNotNull(response);
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
    }
}