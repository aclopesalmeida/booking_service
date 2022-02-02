package com.carolina.booking_service.service;

import com.carolina.booking_service.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class MappingServiceImplTest {

    @Autowired
    private MappingService mappingService;

    @MockBean
    private UserService userService;
    @MockBean
    private SeatService seatService;
    @MockBean
    private RestTemplate mockRestTemplate;

    private User mockUser;
    private Seat mockSeat;
    private Booking mockBooking;
    private BookingResponseDTO mockResponseBookingDTO;

    @BeforeEach
    void setUp() {
        this.mockSeat = new Seat(1, VenueArea.FLOOR, true, true);
        this.mockUser = new User(1L, "Ana", "Almeida", "ana@test.com", "testPassword");

        this.mockBooking = new Booking(1L, 1L, this.mockUser, this.mockSeat);
        mockResponseBookingDTO =  new BookingResponseDTO(
                1L, "Dancing Queen", this.mockBooking.getUser().getEmail(), 1, this.mockSeat.getVenueArea(), LocalDateTime.now()
        );
    }

    @Test
    void sendBooking_returnsBookingRespondeDTO() {
        // Given
        Show mockShow = new Show("Aladdin");

        when(mockRestTemplate.getForObject(any(String.class), any())).thenReturn(mockShow);

        // When
        BookingResponseDTO actualBookingResponseDTO = this.mappingService.mapToResponseDTO(this.mockBooking);

        // Then
        Assertions.assertInstanceOf(BookingResponseDTO.class, actualBookingResponseDTO);
        Assertions.assertEquals(this.mockResponseBookingDTO.getUserEmail(), actualBookingResponseDTO.getUserEmail());
        Assertions.assertEquals(mockShow.getName(), actualBookingResponseDTO.getShowName());
    }

    @Test
    void sendBookingRequestDTO_returnsBooking() {
        // Given
        BookingRequestDTO mockRequestBookingDTO =  new BookingRequestDTO(1L, this.mockUser.getId(), this.mockSeat.getId());
        Show mockShow = new Show("Aladdin");

        when(seatService.getSeat(any(Integer.class))).thenReturn(this.mockSeat);
        when(userService.getUser(any(Long.class))).thenReturn(this.mockUser);

        // When
        Booking actualBooking = this.mappingService.mapToBooking(mockRequestBookingDTO);

        // Then
        Assertions.assertInstanceOf(Booking.class, actualBooking);
        Assertions.assertEquals(mockRequestBookingDTO.getUserId(), actualBooking.getUser().getId());
        Assertions.assertEquals(mockRequestBookingDTO.getShowId(), actualBooking.getShowId());
    }
}