package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.LoginRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.TokenRefreshRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.ChangePasswordRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.ForgotPasswordRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.JwtResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenRefreshResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.BadRequestException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.ResourceNotFoundException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt.JwtUtils;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.AuthService;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.TokenBlacklistService; // Đổi sang gọi Service
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService; // TIÊM SERVICE REDIS MỚI
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + loginRequest.getUsername()));

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (requestRefreshToken != null && jwtUtils.validateJwtToken(requestRefreshToken)) {
            String username = jwtUtils.getUserNameFromJwtToken(requestRefreshToken);

            String newAccessToken = jwtUtils.generateJwtToken(username);
            String newRefreshToken = jwtUtils.generateRefreshToken(username);

            return TokenRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .build();
        }

        throw new BadRequestException("Refresh Token không hợp lệ hoặc đã hết hạn!");
    }

    @Override
    @Transactional
    public void logout(String headerAuth) {
        String jwt = null;

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            // 1. Lấy ngày hết hạn của Token, tính toán thời gian sống (TTL) còn lại bằng mili-giây
            Date expirationDate = jwtUtils.getExpirationDateFromToken(jwt);
            long remainingTimeMs = expirationDate.getTime() - System.currentTimeMillis();

            // 2. Đẩy thẳng chuỗi token sang lưu trữ bảo mật trên RAM Redis
            tokenBlacklistService.blacklistToken(jwt, remainingTimeMs);

            // 3. Xóa ngữ cảnh phiên làm việc hiện tại
            SecurityContextHolder.clearContext();
        } else {
            throw new BadRequestException("Yêu cầu đăng xuất không hợp lệ hoặc thiếu Token!");
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng hiện tại!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ nhập vào không chính xác!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Tên đăng nhập không tồn tại trên hệ thống!"));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email xác thực không trùng khớp với tài khoản này!");
        }

        user.setPassword(passwordEncoder.encode("Ptit@2026"));
        userRepository.save(user);
    }
}