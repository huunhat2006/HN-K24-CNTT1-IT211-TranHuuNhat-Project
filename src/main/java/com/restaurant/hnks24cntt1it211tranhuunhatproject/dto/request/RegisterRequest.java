package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
}