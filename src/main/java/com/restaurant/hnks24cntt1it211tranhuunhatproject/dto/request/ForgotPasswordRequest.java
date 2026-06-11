package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Email xác nhận không được để trống")
    @Email(message = "Định dạng Email không hợp lệ")
    private String email;
}