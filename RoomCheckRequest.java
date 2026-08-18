package com.example.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record RoomCheckRequest(
    @JsonPropertyDescription("Ngày nhận phòng, định dạng yyyy-MM-dd")
    @JsonProperty(required = true)
    String checkInDate,

    @JsonPropertyDescription("Ngày trả phòng, định dạng yyyy-MM-dd")
    @JsonProperty(required = true)
    String checkOutDate,

    @JsonPropertyDescription("Loại phòng khách đặt, ví dụ: Deluxe, Standard")
    @JsonProperty(required = true)
    String roomType
) {}