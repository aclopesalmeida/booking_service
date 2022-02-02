package com.carolina.booking_service.service;

import com.carolina.booking_service.model.Booking;
import com.carolina.booking_service.model.BookingRequestDTO;
import com.carolina.booking_service.model.BookingResponseDTO;

public interface MappingService {

    BookingResponseDTO mapToResponseDTO(Booking booking);
    Booking mapToBooking(BookingRequestDTO bookingDTO);
}
