package com.carolina.booking_service.service;

import com.carolina.booking_service.model.Seat;
import com.carolina.booking_service.model.VenueArea;
import com.carolina.booking_service.repository.SeatRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SeatServiceImplTest {
    
    @Autowired
    private SeatService seatService;
    
    @MockBean
    private SeatRepository seatRepository;

    private Seat mockSeat;

    @BeforeEach
    void setUp() {
        this.mockSeat = new Seat(1, VenueArea.FLOOR, true, false);
    }

    @Test
    void getSeatById_returnsSeat() {
        // Given
        Integer mockSeatId = this.mockSeat.getId();
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(this.mockSeat));

        // When
        Seat actualSeat = seatService.getSeat(mockSeatId);

        // Then
        Assertions.assertNotNull(actualSeat);
        Assertions.assertEquals(this.mockSeat.getVenueArea(), actualSeat.getVenueArea());
    }

    @Test
    void getSeatById_whenDoesntExist_throwsEntityNotFoundException() {
        // Given
        Integer mockSeatId = this.mockSeat.getId();
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            seatService.getSeat(mockSeatId);
        });
    }

    @Test
    void getAvailableSeats_returnsListOfSeatsEnabledAndNotBookedYet() {
        // Given
        List<Seat> mockSeats = new ArrayList<>() {
            {
                add(new Seat(1, VenueArea.LEVEL_1, true, false));
                add(new Seat(2, VenueArea.FLOOR, true, false));
                add(new Seat(3, VenueArea.FLOOR, true, true));
                add(new Seat(4, VenueArea.LEVEL_1, true, false));
                add(new Seat(5, VenueArea.LEVEL_2, false, false));
            }
        };
        List<Seat> availableMockSeats = mockSeats.stream()
                                                .filter(s -> s.isEnabled() && !s.isBooked())
                                                .collect(Collectors.toList());
        Integer expectedAvailableSeatsCount = availableMockSeats.size();

        when(seatRepository.findAll()).thenReturn(mockSeats);

        // When
        List<Seat> actualSeats = seatService.getAvailableSeats();

        // Then
        Assertions.assertEquals(expectedAvailableSeatsCount, actualSeats.size());
        Assertions.assertIterableEquals(availableMockSeats, actualSeats);
    }

    @Test
    void isSeatAvailable_returnsTrueIfSeatIsNotNullAndIsEnabledAndNotBookedYet() {
        // Given
        Integer mockSeatId = this.mockSeat.getId();

        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(this.mockSeat));

        // When
        Boolean actualIsSeatAvailable = seatService.isSeatAvailable(mockSeatId);

        // Then
        Assertions.assertTrue(actualIsSeatAvailable);
    }

    @Test
    void isSeatAvailable_returnsFalseIfSeatIsNotNullAndIsEnabledAndIsBooked() {
        // Given
        Seat mockBookedSeat = new Seat(1, VenueArea.FLOOR, true, true);
        Integer mockBookedSeatId = mockBookedSeat.getId();

        when(seatRepository.findById(mockBookedSeatId)).thenReturn(Optional.of(mockBookedSeat));

        // When
        Boolean actualIsSeatAvailable = seatService.isSeatAvailable(mockBookedSeatId);

        // Then
        Assertions.assertFalse(actualIsSeatAvailable);
    }

    @Test
    void isSeatAvailable_returnsFalseIfSeatIsNotNullAndIsNotEnabledAndNotBookedYet() {
        // Given
        Seat mockBookedSeat = new Seat(1, VenueArea.FLOOR, false, false);
        Integer mockBookedSeatId = mockBookedSeat.getId();

        when(seatRepository.findById(mockBookedSeatId)).thenReturn(Optional.of(mockBookedSeat));

        // When
        Boolean actualIsSeatAvailable = seatService.isSeatAvailable(mockBookedSeatId);

        // Then
        Assertions.assertFalse(actualIsSeatAvailable);
    }

    @Test
    void isSeatAvailable_returnsFalseIfSeatIsNull() {
        // Given
        Integer mockSeatId = this.mockSeat.getId();

        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.empty());

        // When
        Boolean actualIsSeatAvailable = seatService.isSeatAvailable(mockSeatId);

        // Then
        Assertions.assertFalse(actualIsSeatAvailable);
    }

    @Test
    void changeSeatBookingStatus_fromNotBookedToBooked_verifySeatRepositorySaveIsCalled() {
        Seat mockUpdatedSeat = new Seat(1, VenueArea.FLOOR, true, true);

        when(seatRepository.findById(this.mockSeat.getId())).thenReturn(Optional.of(mockUpdatedSeat));

        // When
        seatService.changeSeatBookingStatus(this.mockSeat.getId(), true);

        // Then
        verify(seatRepository, times(1)).save(mockUpdatedSeat);
    }
}