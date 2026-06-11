package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);
}