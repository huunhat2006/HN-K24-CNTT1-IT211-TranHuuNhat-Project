package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.AdminUserCreateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
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
@AutoConfigureMockMvc(addFilters = false)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testCreateUserByAdmin_Success() throws Exception {
        AdminUserCreateRequest request = AdminUserCreateRequest.builder()
                .username("new_manager")
                .password("password123")
                .fullName("Nguyen Van Manager")
                .email("manager_new@gmail.com")
                .role("MANAGER")
                .build();

        Mockito.when(userService.createUserByAdmin(any()))
                .thenReturn(UserResponse.builder().id(5L).username("new_manager").build());

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()); // Kỳ vọng mã trả về là 201 Created
    }

    // Thêm hàm test này vào file UserAdminControllerTest ở trên
    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/admin/users/1"))
                .andExpect(status().isNoContent()); // Mã trả về phải là 204
    }
}