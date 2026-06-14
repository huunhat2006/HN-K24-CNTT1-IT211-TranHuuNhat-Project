package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.LoginRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.RegisterRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.JwtResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.AuthService;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Vô hiệu hóa bảo mật Spring Security để test mượt
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Test
    void testLoginEndpoint_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        Mockito.when(authService.login(any())).thenReturn(JwtResponse.builder().accessToken("token").build());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterEndpoint_ValidationError_BadRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername(""); // Gây lỗi trống @NotBlank
        invalidRequest.setEmail("sai_dinh_dang_email"); // Gây lỗi định dạng @Email

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Phải trả về 400 Bad Request
    }

    @Test
    void testChangePasswordEndpoint_Success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"123\",\"newPassword\":\"123456\"}")
                        .principal(() -> "admin")) // Giả lập người dùng đăng nhập tên admin
                .andExpect(status().isOk());
    }
}