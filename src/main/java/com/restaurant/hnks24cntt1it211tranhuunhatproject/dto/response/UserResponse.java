package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String email;
    private String phoneNumber;
    private Boolean isEnabled;
}