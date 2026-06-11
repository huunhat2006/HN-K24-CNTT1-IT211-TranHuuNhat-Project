package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role; // ADMIN, MANAGER, CUSTOMER
    private Boolean isEnabled;
}