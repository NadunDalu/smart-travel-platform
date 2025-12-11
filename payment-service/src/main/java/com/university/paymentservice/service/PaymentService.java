package com.university.paymentservice.service;

import com.university.paymentservice.dto.PaymentDTO;
import com.university.paymentservice.dto.PaymentRequest;
import com.university.paymentservice.entity.Payment;
import com.university.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${booking.service.url}")
    private String bookingServiceUrl;

    public PaymentDTO processPayment(PaymentRequest request) {
        log.info("Processing payment for booking: {}", request.getBookingId());

        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("PENDING");
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentDate(LocalDateTime.now());

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentGateway(request);

        if (paymentSuccess) {
            payment.setStatus("COMPLETED");
            Payment savedPayment = paymentRepository.save(payment);

            // Update booking status via WebClient
            updateBookingStatus(request.getBookingId(), "CONFIRMED");

            log.info("Payment completed successfully with transaction id: {}", payment.getTransactionId());
            return convertToDTO(savedPayment);
        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);

            // Update booking status to CANCELLED
            updateBookingStatus(request.getBookingId(), "CANCELLED");

            throw new RuntimeException("Payment processing failed");
        }
    }

    private boolean simulatePaymentGateway(PaymentRequest request) {
        // Simulate payment gateway processing
        // In real application, integrate with payment gateways like Stripe, PayPal
        try {
            Thread.sleep(1000); // Simulate processing time
            return true; // Simulate successful payment
        } catch (InterruptedException e) {
            log.error("Payment processing interrupted", e);
            return false;
        }
    }

    private void updateBookingStatus(Long bookingId, String status) {
        try {
            String response = webClientBuilder.build()
                    .put()
                    .uri(bookingServiceUrl + "/api/bookings/" + bookingId + "/status?status=" + status)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Booking status updated successfully: {}", response);
        } catch (Exception e) {
            log.error("Failed to update booking status", e);
            throw new RuntimeException("Failed to update booking status: " + e.getMessage());
        }
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return convertToDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBookingId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}