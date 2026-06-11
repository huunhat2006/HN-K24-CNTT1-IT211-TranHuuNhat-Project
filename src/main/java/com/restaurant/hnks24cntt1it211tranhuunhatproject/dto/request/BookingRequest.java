package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long courtId;
    private LocalDate bookingDate;
    private String timeSlot;
}