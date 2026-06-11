package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import java.util.Map;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);

    // MỚI: Xem lịch sử đặt lịch của cá nhân khách hàng
    Map<String, Object> getCustomerBookingHistory(String username, int page, int size);

    // MỚI: Xem toàn bộ lịch sử hệ thống (Dành cho Admin)
    Map<String, Object> getAllBookingsForAdmin(int page, int size);

    // MỚI: Phê duyệt hoặc Từ chối lịch đặt sân
    BookingResponse updateBookingStatus(Long bookingId, String status);
}