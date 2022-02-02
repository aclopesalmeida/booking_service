package com.carolina.booking_service.model;

import java.util.List;

public class UserBookingDTO {

    private Long userId;
    private List<BookingResponseDTO> bookingsDTO;

    public UserBookingDTO() {}

    public UserBookingDTO(Long userId, List<BookingResponseDTO> bookingsDTO) {
        this.userId = userId;
        this.bookingsDTO = bookingsDTO;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<BookingResponseDTO> getBookings() {
        return bookingsDTO;
    }

    public void setBookings(List<BookingResponseDTO> bookingsDTO) {
        this.bookingsDTO = bookingsDTO;
    }
}
