package com.example.booking.dto;

public record RoomCheckResponse(
    boolean available,
    double pricePerNight,
    String message
) {}