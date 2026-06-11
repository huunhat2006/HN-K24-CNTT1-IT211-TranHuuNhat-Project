package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private LocalDate bookingDate;
    private String timeSlot;
    private Double totalPrice;
    private String status;
    private String courtName;
}