package com.university.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long flightId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private LocalDate travelDate;

    @Column(nullable = false)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime bookingDate;
}
