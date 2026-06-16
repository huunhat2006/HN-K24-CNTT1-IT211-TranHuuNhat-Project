package com.restaurant.hnks24cntt1it211tranhuunhatproject.config;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Bật tính năng bảo vệ tầng method bằng @PreAuthorize nếu cần
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Cấu hình mã hóa mật khẩu bằng thuật toán BCrypt với độ mạnh Strength = 12 (Đảm bảo tiêu chuẩn 10+)
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        // QUY TẮC: ƯU TIÊN KIỂM TRA THẰNG CÓ ĐƯỜNG DẪN CHI TIẾT TRƯỚC

                        // 1. Ép buộc API đổi mật khẩu và đăng xuất bắt buộc phải có Token hợp lệ (.authenticated)
                        auth.requestMatchers("/api/v1/auth/change-password").authenticated()
                                .requestMatchers("/api/v1/auth/logout").authenticated()

                                // 2. Mở cổng tự do cho các hành động đăng nhập, đăng ký, quên mật khẩu và làm mới token
                                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/forgot-password", "/api/v1/auth/refresh").permitAll()
                                .requestMatchers("/error").permitAll()

                                // 3. Phân chia ranh giới Role nghiêm ngặt theo Ma trận phân quyền hệ thống
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER")

                                // ĐÃ SỬA: Khóa chặt luồng Customer, chỉ cho tài khoản có Role CUSTOMER chui vào đặt sân
                                .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")

                                .anyRequest().authenticated()
                );

        // Thêm lớp lọc bảo mật JWT Request Filter đứng trước UsernamePasswordAuthenticationFilter mặc định
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}