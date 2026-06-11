package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.BookingRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponse createBooking(BookingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Sân không tồn tại"));

        boolean isConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
                request.getCourtId(), request.getBookingDate(), request.getTimeSlot(), Arrays.asList("PENDING", "CONFIRMED"));

        if (isConflict) {
            throw new RuntimeException("Khung giờ này đã có người đặt!");
        }

        Booking booking = Booking.builder()
                .bookingDate(request.getBookingDate())
                .timeSlot(request.getTimeSlot())
                .totalPrice(120000.0) // Giá tượng trưng mặc định
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .user(user)
                .court(court)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingResponse.builder()
                .id(savedBooking.getId())
                .bookingDate(savedBooking.getBookingDate())
                .timeSlot(savedBooking.getTimeSlot())
                .totalPrice(savedBooking.getTotalPrice())
                .status(savedBooking.getStatus())
                .courtName(court.getCourtName())
                .build();
    }
}