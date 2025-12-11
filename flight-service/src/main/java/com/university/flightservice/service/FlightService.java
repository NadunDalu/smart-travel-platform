package com.university.flightservice.service;

import com.university.flightservice.dto.FlightDTO;
import com.university.flightservice.entity.Flight;
import com.university.flightservice.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightDTO createFlight(FlightDTO flightDTO) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightDTO.getFlightNumber());
        flight.setAirline(flightDTO.getAirline());
        flight.setOrigin(flightDTO.getOrigin());
        flight.setDestination(flightDTO.getDestination());
        flight.setDepartureTime(flightDTO.getDepartureTime());
        flight.setArrivalTime(flightDTO.getArrivalTime());
        flight.setPrice(flightDTO.getPrice());
        flight.setAvailableSeats(flightDTO.getAvailableSeats());
        flight.setAvailable(true);

        Flight savedFlight = flightRepository.save(flight);
        return convertToDTO(savedFlight);
    }

    public FlightDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
        return convertToDTO(flight);
    }

    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FlightDTO checkAvailability(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));

        if (!flight.getAvailable() || flight.getAvailableSeats() <= 0) {
            throw new RuntimeException("Flight is not available");
        }

        return convertToDTO(flight);
    }

    public FlightDTO updateFlight(Long id, FlightDTO flightDTO) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));

        flight.setFlightNumber(flightDTO.getFlightNumber());
        flight.setAirline(flightDTO.getAirline());
        flight.setOrigin(flightDTO.getOrigin());
        flight.setDestination(flightDTO.getDestination());
        flight.setDepartureTime(flightDTO.getDepartureTime());
        flight.setArrivalTime(flightDTO.getArrivalTime());
        flight.setPrice(flightDTO.getPrice());
        flight.setAvailableSeats(flightDTO.getAvailableSeats());

        Flight updatedFlight = flightRepository.save(flight);
        return convertToDTO(updatedFlight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    private FlightDTO convertToDTO(Flight flight) {
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setAirline(flight.getAirline());
        dto.setOrigin(flight.getOrigin());
        dto.setDestination(flight.getDestination());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setPrice(flight.getPrice());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setAvailable(flight.getAvailable());
        return dto;
    }
}