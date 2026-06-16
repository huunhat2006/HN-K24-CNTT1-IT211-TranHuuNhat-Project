package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.admin;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.AdminUserCreateRequest; // IMPORT MỚI
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.UserUpdateRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.UserResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.UserService;
import jakarta.validation.Valid; // Bật tính năng kiểm toán dữ liệu đầu vào
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

    // 1. CREATE: Admin chủ động tạo tài khoản mới (Chỉ định rõ vai trò MANAGER hoặc ADMIN)
    // POST http://localhost:8080/api/v1/admin/users
    @PostMapping
    public ResponseEntity<?> createUserByAdmin(@Valid @RequestBody AdminUserCreateRequest request) {
        try {
            UserResponse response = userService.createUserByAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // Trả về mã 201 Created chuẩn RESTful
        } catch (RuntimeException e) {
            // Bộ ExceptionHandler sẽ tự động bắt nếu trùng tên/email, ở đây bẫy thêm đề phòng lỗi runtime
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 2. READ: Lấy danh sách + Tìm kiếm + Phân trang
    // GET http://localhost:8080/api/v1/admin/users
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = userService.getUsersPaginatedAndSearch(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    // 3. UPDATE: Cập nhật thông tin người dùng theo ID
    // PUT http://localhost:8080/api/v1/admin/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        try {
            UserResponse response = userService.updateUser(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 4. DELETE: Xóa người dùng ra khỏi hệ thống theo ID
    // DELETE http://localhost:8080/api/v1/admin/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // Trả về đúng mã 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}