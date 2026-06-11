package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.RegisterRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse registerUser(RegisterRequest request);
    Map<String, Object> getUsersPaginatedAndSearch(String keyword, int page, int size);
    UserResponse updateUser(Long id, UserUpdateRequest request); // MỚI BỔ SUNG
    void deleteUser(Long id); // MỚI BỔ SUNG
}