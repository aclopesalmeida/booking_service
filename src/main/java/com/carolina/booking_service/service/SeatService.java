package com.carolina.booking_service.service;

import com.carolina.booking_service.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatService {

    List<Seat> getAvailableSeats();
    Boolean isSeatAvailable(Integer id);
    Seat getSeat(Integer id);
    void changeSeatBookingStatus(Integer id, Boolean isBooked);
}
