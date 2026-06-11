package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng chuẩn")
    private String email;

    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @NotBlank(message = "Vai trò (Role) không được để trống")
    private String role; // ADMIN, MANAGER, CUSTOMER

    @NotNull(message = "Trạng thái kích hoạt không được để trống")
    private Boolean isEnabled;
}