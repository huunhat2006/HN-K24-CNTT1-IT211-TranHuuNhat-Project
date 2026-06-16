package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.AdminUserCreateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUserByAdmin_Success() {
        AdminUserCreateRequest request = AdminUserCreateRequest.builder()
                .username("manager_vip")
                .password("raw_pass")
                .fullName("Chu San VIP")
                .email("vip@gmail.com")
                .role("MANAGER")
                .build();

        Mockito.when(userRepository.existsByUsername("manager_vip")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("vip@gmail.com")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("raw_pass")).thenReturn("hashed_pass_bcrypt");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.createUserByAdmin(request);

        assertNotNull(response);
        assertEquals("manager_vip", response.getUsername());
        assertEquals("MANAGER", response.getRole());
    }

    // Bổ sung hàm này vào file UserServiceImplTest.java ở trên
    @Test
    void testUpdateUser_Success() {
        com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest updateRequest =
                new com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest();
        updateRequest.setFullName("Ten Thay Doi");
        updateRequest.setEmail("giu_nguyen@gmail.com");
        updateRequest.setRole("CUSTOMER");
        updateRequest.setIsEnabled(true);

        User oldUser = User.builder().id(1L).username("nhat").email("giu_nguyen@gmail.com").build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Ten Thay Doi", response.getFullName());
    }
}