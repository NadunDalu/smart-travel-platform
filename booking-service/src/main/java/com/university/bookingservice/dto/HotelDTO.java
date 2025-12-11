package com.university.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String location;
    private String address;
    private Integer rating;
    private BigDecimal pricePerNight;
    private Integer availableRooms;
    private String amenities;
    private Boolean available;
}