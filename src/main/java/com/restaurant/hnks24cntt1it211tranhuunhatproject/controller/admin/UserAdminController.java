package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.admin;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    // 1. READ: Lấy danh sách + Tìm kiếm + Phân trang
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = userService.getUsersPaginatedAndSearch(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    // 2. UPDATE: Cập nhật thông tin người dùng theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        try {
            UserResponse response = userService.updateUser(id, request);
            return ResponseEntity.ok(response); // Trả về 200 OK kèm data sạch
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 3. DELETE: Xóa người dùng ra khỏi hệ thống theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // Trả về đúng mã 204 No Content như tài liệu yêu cầu
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}