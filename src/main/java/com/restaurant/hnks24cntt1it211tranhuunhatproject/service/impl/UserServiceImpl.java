package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.RegisterRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.AdminUserCreateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.DataConflictException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.ResourceNotFoundException;
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
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataConflictException("Tên đăng nhập (Username) này đã tồn tại trên hệ thống!");
        }
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
    @Transactional
    // MỚI BỔ SUNG: Admin tạo tài khoản cho phép chỉ định quyền linh hoạt (MANAGER/ADMIN)
    public UserResponse createUserByAdmin(AdminUserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataConflictException("Tên đăng nhập (Username) này đã tồn tại trên hệ thống!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataConflictException("Địa chỉ Email này đã được đăng ký bởi tài khoản khác!");
        }

        String targetRole = request.getRole().toUpperCase();
        if (!targetRole.equals("ADMIN") && !targetRole.equals("MANAGER") && !targetRole.equals("CUSTOMER")) {
            throw new IllegalArgumentException("Vai trò hệ thống cấp phát không hợp lệ!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Băm mật khẩu chuẩn BCrypt
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(targetRole) // Nhận Role động từ Admin quyết định
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cập nhật thất bại: Không tìm thấy người dùng với ID: " + id));

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