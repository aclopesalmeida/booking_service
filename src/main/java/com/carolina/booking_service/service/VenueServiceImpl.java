package com.carolina.booking_service.service;

import com.carolina.booking_service.model.Venue;
import com.carolina.booking_service.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VenueServiceImpl implements VenueService {

    // There is only one venue
    private final Integer venueId = 1;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private SeatService seatService;

    /**
     * Returns the venue's capacity
     * @return Integer
     */
    @Override
    public Integer getVenueCapacity() {
        return venueRepository.findById(venueId).map(v -> v.getCapacity()).orElse(0);
    }

    /**
     * Informs if the venus is currently sold out or not
     * @return Boolean
     */
    @Override
    public Boolean isVenueSoldOut() {
        Venue venue = venueRepository.findById(venueId).get();
        return venue.getSoldOut();
    }

    /**
     * Checks if the venue is sold out basead on the total number of seats available and the venue's capacity
     */
    @Override
    public void changeVenueAvailability() {
        Integer bookedSeatsCount = seatService.getAvailableSeats().size();

        Venue venue = venueRepository.findById(venueId).get();
        venue.setSoldOut(bookedSeatsCount.equals(venue.getCapacity()));
    }
}
