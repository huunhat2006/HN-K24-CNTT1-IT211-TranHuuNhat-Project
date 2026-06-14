package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import java.util.Map;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);

    Map<String, Object> getCustomerBookingHistory(String username, int page, int size);

    Map<String, Object> getAllBookingsForAdmin(int page, int size);

    Map<String, Object> getBookingsForManager(String managerUsername, int page, int size);

    // ĐÃ SỬA: Thêm managerUsername để kiểm toán chủ quyền đơn đặt
    BookingResponse updateBookingStatus(Long bookingId, String status, String managerUsername);
}