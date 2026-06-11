package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotNull(message = "ID sân không được để trống")
    private Long courtId;

    @NotNull(message = "Ngày đặt sân không được để trống")
    @FutureOrPresent(message = "Ngày đặt sân phải là ngày hiện tại hoặc tương lai")
    private LocalDate bookingDate;

    @NotBlank(message = "Khung giờ đặt sân không được để trống")
    private String timeSlot;
}