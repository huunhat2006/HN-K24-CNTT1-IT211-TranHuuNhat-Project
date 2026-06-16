package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.admin;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // IMPORT MỚI
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
public class BookingAdminController {

    private final BookingService bookingService;

    // 1. READ ALL: Admin xem toàn bộ danh sách đặt sân của hệ thống (Phân trang)
    // GET /api/v1/admin/bookings?page=0&size=10
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = bookingService.getAllBookingsForAdmin(page, size);
        return ResponseEntity.ok(response);
    }

    // 2. APPROVE / REJECT: Admin phê duyệt hoặc từ chối lịch bất kỳ của Khách hàng
    // PUT /api/v1/admin/bookings/1/status?status=CONFIRMED
    @PutMapping("/{id}/status")
    public ResponseEntity<?> approveOrRejectBooking(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) { // ĐÃ SỬA: Thêm đối tượng Authentication để lấy danh tính Admin
        try {
            // Trích xuất username của Admin đang đăng nhập từ Token
            String adminUsername = authentication.getName();

            // ĐÃ SỬA: Truyền đủ 3 tham số vào hàm service theo đúng cấu trúc mới nâng cấp
            BookingResponse response = bookingService.updateBookingStatus(id, status, adminUsername);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}