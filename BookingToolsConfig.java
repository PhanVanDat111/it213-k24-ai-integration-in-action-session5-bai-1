package com.example.booking.config;

import com.example.booking.dto.RoomCheckRequest;
import com.example.booking.dto.RoomCheckResponse;
import com.example.booking.service.BookingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.ai.tool.annotation.Tool;
import java.util.function.Function;

@Configuration
public class BookingToolsConfig {

    private final BookingService bookingService;

    public BookingToolsConfig(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Bean
    @Description("Kiểm tra tình trạng còn phòng trống của khách sạn dựa trên ngày nhận phòng (checkInDate, định dạng yyyy-MM-dd), ngày trả phòng (checkOutDate, định dạng yyyy-MM-dd) và loại phòng (roomType, ví dụ: Deluxe, Standard). Trả về trạng thái và đơn giá mỗi đêm.")
    public Function<RoomCheckRequest, RoomCheckResponse> getRoomAvailability() {
        return bookingService.getRoomAvailability();
    }

    @Bean
    public BookingTools bookingTools() {
        return new BookingTools(bookingService);
    }

    public static class BookingTools {
        private final BookingService bookingService;

        public BookingTools(BookingService bookingService) {
            this.bookingService = bookingService;
        }

        @Tool(description = "Tính toán tổng chi phí lưu trú của khách hàng tại khách sạn. Công cụ này chỉ được gọi sau khi đã xác định được loại phòng (roomType) và tổng số ngày lưu trú thực tế của khách hàng (numberOfDays, phải lớn hơn 0).")
        public double calculateTotalPrice(String roomType, int numberOfDays) {
            return bookingService.calculateTotalPrice(roomType, numberOfDays);
        }
    }
}