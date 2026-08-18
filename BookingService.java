package com.example.booking.service;

import com.example.booking.dto.RoomCheckRequest;
import com.example.booking.dto.RoomCheckResponse;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class BookingService {

    public Function<RoomCheckRequest, RoomCheckResponse> getRoomAvailability() {
        return request -> {
            boolean isAvailable = "Deluxe".equalsIgnoreCase(request.roomType()) || "Standard".equalsIgnoreCase(request.roomType());
            double price = "Deluxe".equalsIgnoreCase(request.roomType()) ? 150.0 : 80.0;
            String message = isAvailable ? "Phòng " + request.roomType() + " còn trống." : "Rất tiếc, loại phòng này đã hết.";
            return new RoomCheckResponse(isAvailable, price, message);
        };
    }

    public double calculateTotalPrice(String roomType, int numberOfDays) {
        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("Số ngày lưu trú phải lớn hơn 0");
        }
        double pricePerNight = "Deluxe".equalsIgnoreCase(roomType) ? 150.0 : 80.0;
        return pricePerNight * numberOfDays;
    }
}