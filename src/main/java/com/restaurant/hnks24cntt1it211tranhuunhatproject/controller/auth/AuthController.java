package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.auth;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.LoginRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.RegisterRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.TokenRefreshRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.JwtResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenRefreshResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.TokenBlacklist;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.TokenBlacklistRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt.JwtUtils;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo đồng thời cả 2 Token phục vụ xoay vòng
        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        JwtResponse response = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            UserResponse response = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // FR-02: Xoay vòng Token (Refresh Token)
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (requestRefreshToken != null && jwtUtils.validateJwtToken(requestRefreshToken)) {
            String username = jwtUtils.getUserNameFromJwtToken(requestRefreshToken);

            // Tạo cặp mã mới tinh để xoay vòng cấu trúc không trạng thái
            String newAccessToken = jwtUtils.generateRefreshToken(username); // Sử dụng cấu trúc mã hóa mới
            String newRefreshToken = jwtUtils.generateRefreshToken(username);

            TokenRefreshResponse response = TokenRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .build();
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh Token không hợp lệ hoặc đã hết hạn!");
    }

    // FR-03: Đăng xuất & Thu hồi Token vào Blacklist
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String jwt = null;

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Date expirationDate = jwtUtils.getExpirationDateFromToken(jwt);

            // Lưu Token hiện tại vào danh sách đen trong DB
            TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                    .token(jwt)
                    .expiryTime(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .user(user)
                    .build();

            tokenBlacklistRepository.save(blacklistEntry);
            SecurityContextHolder.clearContext(); // Xóa sạch cấu hình phiên đăng nhập hiện tại

            return ResponseEntity.ok("Đăng xuất thành công. Token đã được thu hồi!");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Yêu cầu đăng xuất không hợp lệ!");
    }
}