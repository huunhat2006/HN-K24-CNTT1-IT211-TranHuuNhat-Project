package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(Long courtId, LocalDate bookingDate, String timeSlot, List<String> statuses);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findAll(Pageable pageable);

    // MỚI BỔ SUNG: Truy vấn động xuyên qua các mối quan hệ (Booking -> Court -> Cluster -> Manager)
    @Query("SELECT b FROM Booking b WHERE b.court.cluster.manager.username = :managerUsername")
    Page<Booking> findByClusterManagerUsername(@Param("managerUsername") String managerUsername, Pageable pageable);
}