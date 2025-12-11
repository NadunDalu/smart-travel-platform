package com.university.hotelservice.service;

import com.university.hotelservice.dto.HotelDTO;
import com.university.hotelservice.entity.Hotel;
import com.university.hotelservice.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = new Hotel();
        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setRating(hotelDTO.getRating());
        hotel.setPricePerNight(hotelDTO.getPricePerNight());
        hotel.setAvailableRooms(hotelDTO.getAvailableRooms());
        hotel.setAmenities(hotelDTO.getAmenities());
        hotel.setAvailable(true);

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDTO(savedHotel);
    }

    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return convertToDTO(hotel);
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HotelDTO checkAvailability(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        if (!hotel.getAvailable() || hotel.getAvailableRooms() <= 0) {
            throw new RuntimeException("Hotel rooms are not available");
        }

        return convertToDTO(hotel);
    }

    public HotelDTO updateHotel(Long id, HotelDTO hotelDTO) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setRating(hotelDTO.getRating());
        hotel.setPricePerNight(hotelDTO.getPricePerNight());
        hotel.setAvailableRooms(hotelDTO.getAvailableRooms());
        hotel.setAmenities(hotelDTO.getAmenities());

        Hotel updatedHotel = hotelRepository.save(hotel);
        return convertToDTO(updatedHotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setLocation(hotel.getLocation());
        dto.setAddress(hotel.getAddress());
        dto.setRating(hotel.getRating());
        dto.setPricePerNight(hotel.getPricePerNight());
        dto.setAvailableRooms(hotel.getAvailableRooms());
        dto.setAmenities(hotel.getAmenities());
        dto.setAvailable(hotel.getAvailable());
        return dto;
    }
}