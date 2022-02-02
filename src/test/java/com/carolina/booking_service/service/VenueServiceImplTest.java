package com.carolina.booking_service.service;

import com.carolina.booking_service.model.Seat;
import com.carolina.booking_service.model.Venue;
import com.carolina.booking_service.model.VenueArea;
import com.carolina.booking_service.repository.VenueRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class VenueServiceImplTest {
    
    @Autowired
    private VenueService venueService;
    
    @MockBean
    private VenueRepository venueRepository;
    @MockBean
    private SeatService seatService;
    
    private Venue mockVenue;

    @BeforeEach
    void setUp() {
        this.mockVenue = new Venue();
        mockVenue.setId(1);
        mockVenue.setName("Test venue");
        mockVenue.setAddress("Test address");
        mockVenue.setSoldOut(false);
    }

    @Test
    void getVenueCapacity_returnsVenueCapacity() {
        // Given
        when(venueRepository.findById(this.mockVenue.getId())).thenReturn(Optional.of(this.mockVenue));

        // When
        Integer actualVenueCapacity = venueService.getVenueCapacity();

        // Then
        Assertions.assertEquals(this.mockVenue.getCapacity(), actualVenueCapacity);
    }

    @Test
    void getVenueCapacity_returnsZeroIfVenueIsNull() {
        // Given
        Integer expectedCapacity = 0;
        when(venueRepository.findById(this.mockVenue.getId())).thenReturn(Optional.empty());

        // When
        Integer actualVenueCapacity = venueService.getVenueCapacity();

        // Then
        Assertions.assertEquals(expectedCapacity, actualVenueCapacity);
    }

    @Test
    void isVenueSoldOut_whenVenueIsntSoldOut_returnsFalse() {
        // Given
         when(venueRepository.findById(this.mockVenue.getId())).thenReturn(Optional.of(this.mockVenue));

         // When
        Boolean actualIsVenueSoldOut = venueService.isVenueSoldOut();

         // Then
        Assertions.assertFalse(actualIsVenueSoldOut);
    }

    @Test
    void isVenueSoldOut_whenVenueIsSoldOut_returnsTrue() {
        // Given
        this.mockVenue.setSoldOut(true);
        when(venueRepository.findById(this.mockVenue.getId())).thenReturn(Optional.of(this.mockVenue));

        // When
        Boolean actualIsVenueSoldOut = venueService.isVenueSoldOut();

        // Then
        Assertions.assertTrue(actualIsVenueSoldOut);
    }
}