package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.BookingRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CourtRepository courtRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testCreateBooking_Success() {
        // 1. Arrange
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("17:00 - 18:00");

        User user = User.builder().id(1L).username("nhat2006").build();
        Court court = Court.builder().id(1L).courtName("Sân số 1").build();
        Booking savedBooking = Booking.builder().id(100L).bookingDate(request.getBookingDate()).timeSlot("17:00 - 18:00").court(court).status("PENDING").totalPrice(120000.0).build();

        Mockito.when(userRepository.findByUsername("nhat2006")).thenReturn(Optional.of(user));
        Mockito.when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        Mockito.when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(any(), any(), any(), any())).thenReturn(false);
        Mockito.when(bookingRepository.save(any())).thenReturn(savedBooking);

        // 2. Act
        BookingResponse response = bookingService.createBooking(request, "nhat2006");

        // 3. Assert
        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals("Sân số 1", response.getCourtName());
    }

    @Test
    void testCreateBooking_Conflict_ThrowsException() {
        // 1. Arrange
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("17:00 - 18:00");

        User user = User.builder().id(1L).username("nhat2006").build();
        Court court = Court.builder().id(1L).courtName("Sân số 1").build();

        Mockito.when(userRepository.findByUsername("nhat2006")).thenReturn(Optional.of(user));
        Mockito.when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        // Giả lập lịch bị trùng ca
        Mockito.when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(any(), any(), any(), any())).thenReturn(true);

        // 2. Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(request, "nhat2006"));
        assertEquals("Khung giờ này đã có người đặt!", ex.getMessage());
    }
}