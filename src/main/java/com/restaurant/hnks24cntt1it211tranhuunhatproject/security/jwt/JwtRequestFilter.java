package com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.CustomUserDetailsService;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.TokenBlacklistService; // ĐÃ THAY ĐỔI: Import Service mới
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService; // ĐÃ THAY ĐỔI: Sử dụng Service thao tác RAM Redis thay cho MySQL cũ

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                // ĐÃ SỬA: Truy vấn tốc độ cao trên RAM Redis để chặn đứng lỗi tắc nghẽn cổ chai hệ thống
                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value()); // Sử dụng 401 Unauthorized chuẩn RESTful cho Token bị thu hồi
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8"); // Đảm bảo không bị lỗi font Tiếng Việt khi trả response thô
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Mã Token này đã bị vô hiệu hóa do đăng xuất!\"}");
                    return; // Chặn đứng Request ngay tại cửa ngõ Filter, không cho đi tiếp vào hệ thống
                }

                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            System.err.println("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}