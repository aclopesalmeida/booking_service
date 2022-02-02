package com.carolina.booking_service.service;

public interface VenueService {

    Integer getVenueCapacity();
    Boolean isVenueSoldOut();
    void changeVenueAvailability();
}
