package com.university.bookingservice.client;

import com.university.bookingservice.dto.HotelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hotel-service", url = "${hotel.service.url}")
public interface HotelClient {

    @GetMapping("/api/hotels/{id}/check-availability")
    HotelDTO checkAvailability(@PathVariable("id") Long id);
}