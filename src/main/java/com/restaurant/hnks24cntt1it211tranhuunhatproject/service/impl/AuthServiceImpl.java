package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.LoginRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.TokenRefreshRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.ChangePasswordRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.ForgotPasswordRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.JwtResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenRefreshResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.TokenBlacklist;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.BadRequestException; // IMPORT MỚI
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.ResourceNotFoundException; // IMPORT MỚI
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.TokenBlacklistRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt.JwtUtils;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

        // ĐÃ SỬA: Thay đổi sang ResourceNotFoundException (Sẽ tự động kích hoạt mã 404)
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

        // ĐÃ SỬA: Thay sang BadRequestException (Sẽ tự động kích hoạt mã 400)
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
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            // ĐÃ SỬA: Thay sang ResourceNotFoundException (404)
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng sở hữu Token này!"));

            Date expirationDate = jwtUtils.getExpirationDateFromToken(jwt);

            TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                    .token(jwt)
                    .expiryTime(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .user(user)
                    .build();

            tokenBlacklistRepository.save(blacklistEntry);
            SecurityContextHolder.clearContext();
        } else {
            // ĐÃ SỬA: Thay sang BadRequestException (400)
            throw new BadRequestException("Yêu cầu đăng xuất không hợp lệ hoặc thiếu Token!");
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        // ĐÃ SỬA: Thay sang ResourceNotFoundException (404)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng hiện tại!"));

        // ĐÃ SỬA: Thay sang BadRequestException (400)
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ nhập vào không chính xác!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // ĐÃ SỬA: Thay sang ResourceNotFoundException (404)
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Tên đăng nhập không tồn tại trên hệ thống!"));

        // ĐÃ SỬA: Thay sang BadRequestException (400)
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email xác thực không trùng khớp với tài khoản này!");
        }

        user.setPassword(passwordEncoder.encode("Ptit@2026"));
        userRepository.save(user);
    }
}