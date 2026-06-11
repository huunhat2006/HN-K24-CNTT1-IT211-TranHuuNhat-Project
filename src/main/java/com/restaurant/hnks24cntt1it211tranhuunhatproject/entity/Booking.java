package com.restaurant.hnks24cntt1it211tranhuunhatproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "time_slot", nullable = false, length = 50)
    private String timeSlot;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, CONFIRMED, CHECKED_IN, CANCELED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;
}