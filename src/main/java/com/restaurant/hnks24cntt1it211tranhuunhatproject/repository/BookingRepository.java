package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(Long courtId, LocalDate bookingDate, String timeSlot, List<String> statuses);

    // MỚI: Lấy lịch sử đặt sân của một User cụ thể (Phân trang)
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    // MỚI: Lấy toàn bộ lịch sử hệ thống phục vụ Admin phê duyệt (Phân trang)
    Page<Booking> findAll(Pageable pageable);
}