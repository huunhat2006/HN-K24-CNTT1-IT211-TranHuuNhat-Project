package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.manager;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/manager/bookings")
@RequiredArgsConstructor
public class BookingManagerController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getManagedBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String managerUsername = authentication.getName();
        Map<String, Object> response = bookingService.getBookingsForManager(managerUsername, page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> managerProcessBooking(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) { // Bổ sung đối tượng Authentication
        try {
            String managerUsername = authentication.getName();
            BookingResponse response = bookingService.updateBookingStatus(id, status, managerUsername);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}