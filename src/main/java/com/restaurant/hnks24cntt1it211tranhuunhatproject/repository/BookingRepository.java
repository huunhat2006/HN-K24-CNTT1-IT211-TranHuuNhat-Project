package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(Long courtId, LocalDate bookingDate, String timeSlot, List<String> statuses);
}