package com.university.notificationservice.service;

import com.university.notificationservice.dto.NotificationDTO;
import com.university.notificationservice.dto.NotificationRequest;
import com.university.notificationservice.entity.Notification;
import com.university.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationDTO sendNotification(NotificationRequest request) {
        log.info("Sending notification to user: {}, booking: {}", request.getUserId(), request.getBookingId());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setBookingId(request.getBookingId());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setStatus("SENT");
        notification.setSentAt(LocalDateTime.now());

        // Simulate sending notification (email, SMS, etc.)
        simulateSendingNotification(notification);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification sent successfully with id: {}", savedNotification.getId());

        return convertToDTO(savedNotification);
    }

    private void simulateSendingNotification(Notification notification) {
        // In real application, integrate with email service (SendGrid, AWS SES)
        // or SMS service (Twilio, AWS SNS)
        log.info("Simulating {} notification: {}", notification.getType(), notification.getMessage());
    }

    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByBookingId(Long bookingId) {
        return notificationRepository.findByBookingId(bookingId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setBookingId(notification.getBookingId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setStatus(notification.getStatus());
        dto.setSentAt(notification.getSentAt());
        return dto;
    }
}