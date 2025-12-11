package com.university.bookingservice.service;

import com.university.bookingservice.client.FlightClient;
import com.university.bookingservice.client.HotelClient;
import com.university.bookingservice.dto.*;
import com.university.bookingservice.entity.Booking;
import com.university.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightClient flightClient;
    private final HotelClient hotelClient;
    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public BookingDTO createBooking(BookingRequest request) {
        log.info("Creating booking for user: {}", request.getUserId());

        // Step 1: Validate user via WebClient
        UserDTO user = validateUser(request.getUserId());
        log.info("User validated: {}", user.getName());

        // Step 2: Check flight availability via Feign Client
        FlightDTO flight = flightClient.checkAvailability(request.getFlightId());
        log.info("Flight available: {}", flight.getFlightNumber());

        // Step 3: Check hotel availability via Feign Client
        HotelDTO hotel = hotelClient.checkAvailability(request.getHotelId());
        log.info("Hotel available: {}", hotel.getName());

        // Step 4: Calculate total cost
        BigDecimal totalCost = flight.getPrice().add(hotel.getPricePerNight());
        log.info("Total cost calculated: {}", totalCost);

        // Step 5: Store booking as PENDING
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setFlightId(request.getFlightId());
        booking.setHotelId(request.getHotelId());
        booking.setTravelDate(request.getTravelDate());
        booking.setTotalCost(totalCost);
        booking.setStatus("PENDING");
        booking.setBookingDate(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with id: {}", savedBooking.getId());

        // Step 6: Send notification via WebClient
        sendNotification(savedBooking.getUserId(), savedBooking.getId(),
                "Your booking has been created and is pending payment confirmation.");

        return convertToDTO(savedBooking);
    }

    private UserDTO validateUser(Long userId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/users/" + userId)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to validate user", e);
            throw new RuntimeException("User validation failed: " + e.getMessage());
        }
    }

    private void sendNotification(Long userId, Long bookingId, String message) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(userId);
            notificationRequest.setBookingId(bookingId);
            notificationRequest.setMessage(message);
            notificationRequest.setType("EMAIL");

            webClientBuilder.build()
                    .post()
                    .uri(notificationServiceUrl + "/api/notifications/send")
                    .bodyValue(notificationRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            // Don't throw exception, notification failure shouldn't break booking
        }
    }

    public void updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        booking.setStatus(status);
        bookingRepository.save(booking);

        log.info("Booking {} status updated to: {}", bookingId, status);

        // Send notification about status change
        String message = status.equals("CONFIRMED") ?
                "Your booking has been confirmed! Payment received." :
                "Your booking has been " + status.toLowerCase();
        sendNotification(booking.getUserId(), booking.getId(), message);
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        return convertToDTO(booking);
    }

    public List<BookingDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setFlightId(booking.getFlightId());
        dto.setHotelId(booking.getHotelId());
        dto.setTravelDate(booking.getTravelDate());
        dto.setTotalCost(booking.getTotalCost());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());
        return dto;
    }
}