package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.customer;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/bookings")
@RequiredArgsConstructor
public class BookingCustomerController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            BookingResponse response = bookingService.createBooking(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
        }
    }
}
