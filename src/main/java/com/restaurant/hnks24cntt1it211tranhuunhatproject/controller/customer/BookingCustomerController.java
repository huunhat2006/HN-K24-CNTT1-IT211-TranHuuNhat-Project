package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.customer;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer/bookings")
@RequiredArgsConstructor
public class BookingCustomerController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            BookingResponse response = bookingService.createBooking(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // MỚI: API xem lịch sử đặt lịch cá nhân của Khách hàng
    // GET /api/v1/customer/bookings?page=0&size=5
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyBookingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String username = authentication.getName();
        Map<String, Object> response = bookingService.getCustomerBookingHistory(username, page, size);
        return ResponseEntity.ok(response);
    }
}