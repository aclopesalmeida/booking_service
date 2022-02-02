package com.carolina.booking_service.service;

import com.carolina.booking_service.model.Seat;
import com.carolina.booking_service.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    /**
     * Get seat by id
     * @param id: seat id
     * @return Optional
     */
    @Override
    public Seat getSeat(Integer id) {
        Optional<Seat> seatOptional = seatRepository.findById(id);
        if (!seatOptional.isPresent()) {
            throw new EntityNotFoundException();
        }
        return seatOptional.get();
    }

    /**
     * Returns all seats that are active (available to be used) and not booked yet
     * @return List<Seat></Seat>
     */
    @Override
    public List<Seat> getAvailableSeats() {
        return seatRepository.findAll()
                .stream()
                .filter(s -> s.isEnabled() && !s.isBooked())
                .collect(Collectors.toList());
    }

    /**
     * Checks if the specified seat is being used (enabled) and hasn't been booked yet
     * @param id: seat id
     * @return Boolean
     */
    @Override
    public Boolean isSeatAvailable(Integer id) {
        Optional<Seat> seatOptional = seatRepository.findById(id);
        return seatOptional.isPresent() && seatOptional.get().isEnabled() && !seatOptional.get().isBooked();
    }

    /**
     * Changes a seat's booking status
     * @param id: seat id
     * @param isBooked: boolean
     */
    @Override
    public void changeSeatBookingStatus(Integer id, Boolean isBooked) {
        Seat seat = seatRepository.findById(id).get();
        seat.setBooked(isBooked);
        seatRepository.save(seat);
    }
}
