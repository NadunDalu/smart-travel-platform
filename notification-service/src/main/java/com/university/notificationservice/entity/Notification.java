package com.university.notificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type; // EMAIL, SMS, PUSH

    @Column(nullable = false)
    private String status; // SENT, PENDING, FAILED

    @Column(nullable = false)
    private LocalDateTime sentAt;
}