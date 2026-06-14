package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.RegisterRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.DataConflictException; // IMPORT MỚI
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.ResourceNotFoundException; // IMPORT MỚI
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // ĐÃ SỬA: Thay sang DataConflictException (409)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataConflictException("Tên đăng nhập (Username) này đã tồn tại trên hệ thống!");
        }
        // ĐÃ SỬA: Thay sang DataConflictException (409)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Địa chỉ Email này đã được đăng ký bởi tài khoản khác!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role("CUSTOMER")
                .isEnabled(true)
                .build();

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUsersPaginatedAndSearch(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (keyword == null || keyword.isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(keyword, keyword, pageable);
        }

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", userResponses);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());

        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        // ĐÃ SỬA: Thay sang ResourceNotFoundException (404)
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cập nhật thất bại: Không tìm thấy người dùng với ID: " + id));

        // ĐÃ SỬA: Thay sang DataConflictException (409)
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Xung đột dữ liệu: Email sửa đổi đã được sử dụng bởi tài khoản khác!");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setIsEnabled(request.getIsEnabled());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // ĐÃ SỬA: Thay sang ResourceNotFoundException (404)
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Xóa thất bại: Không tìm thấy người dùng mang ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isEnabled(user.getIsEnabled())
                .build();
    }
}