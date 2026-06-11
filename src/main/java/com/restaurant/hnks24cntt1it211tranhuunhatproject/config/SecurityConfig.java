package com.restaurant.hnks24cntt1it211tranhuunhatproject.config;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.security.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // Sức mạnh mã hóa 10+ theo SRS
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
                        auth.requestMatchers("/api/v1/auth/**").permitAll() // Public API
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER")
                                .requestMatchers("/api/v1/customer/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
                                .anyRequest().authenticated()
                );

        // Bỏ dòng http.authenticationProvider(...) đi, chỉ cần thêm Filter của JWT là đủ
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}