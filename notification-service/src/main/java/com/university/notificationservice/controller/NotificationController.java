package com.university.notificationservice.controller;

import com.university.notificationservice.dto.NotificationDTO;
import com.university.notificationservice.dto.NotificationRequest;
import com.university.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<NotificationDTO> sendNotification(@RequestBody NotificationRequest request) {
        NotificationDTO notification = notificationService.sendNotification(request);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByBookingId(@PathVariable Long bookingId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByBookingId(bookingId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
}