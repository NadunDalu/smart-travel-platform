package com.university.bookingservice.client;

import com.university.bookingservice.dto.FlightDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service", url = "${flight.service.url}")
public interface FlightClient {

    @GetMapping("/api/flights/{id}/check-availability")
    FlightDTO checkAvailability(@PathVariable("id") Long id);
}