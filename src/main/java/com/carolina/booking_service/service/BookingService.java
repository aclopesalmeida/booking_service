package com.carolina.booking_service.service;

import com.carolina.booking_service.model.BookingRequestDTO;
import com.carolina.booking_service.model.BookingResponseDTO;
import com.carolina.booking_service.model.UserBookingDTO;

import java.util.List;

public interface BookingService {

    List<BookingResponseDTO> getAllBookings();
    BookingResponseDTO getBookingById(Long id);
    UserBookingDTO getBookingsByUser(Long userId);
    BookingResponseDTO createBooking(BookingRequestDTO bookingDTO);
    BookingResponseDTO updateBooking(Long id, BookingRequestDTO bookingDTO);
    void deleteBooking(Long id);
}
