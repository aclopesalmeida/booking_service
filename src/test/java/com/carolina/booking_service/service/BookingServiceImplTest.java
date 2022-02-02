package com.carolina.booking_service.service;

import com.carolina.booking_service.exception.SeatNotAvailableException;
import com.carolina.booking_service.exception.VenueSoldOutException;
import com.carolina.booking_service.model.*;
import com.carolina.booking_service.repository.BookingRepository;
import com.carolina.booking_service.repository.SeatRepository;
import com.carolina.booking_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private VenueService venueService;
    @MockBean
    private SeatService seatService;
    @MockBean
    private UserService userService;
    @MockBean
    private MappingService mappingService;

    private Booking mockBooking;
    private BookingRequestDTO mockRequestBookingDTO;
    private BookingResponseDTO mockResponseBookingDTO;
    private User mockUser;
    private Seat mockSeatFloor;
    private Seat mockSeatLevel1;

    @BeforeEach
    void setUp() {
        this.mockSeatFloor = new Seat(1, VenueArea.FLOOR, true, true);
        this.mockSeatLevel1 = new Seat(2, VenueArea.LEVEL_1, true, true);
        this.mockUser = new User(1L, "Ana", "Almeida", "ana@test.com", "testPassword");

        this.mockBooking = new Booking(1L, 1L, this.mockUser, this.mockSeatFloor);
        mockRequestBookingDTO =  new BookingRequestDTO(1L, this.mockUser.getId(), this.mockSeatFloor.getId());
        mockResponseBookingDTO =  new BookingResponseDTO(
                1L, "Dancing Queen", this.mockBooking.getUser().getEmail(), 1, this.mockSeatFloor.getVenueArea(), LocalDateTime.now()
        );
    }

    @Test
    void getAllBookings_returnsListOfBookingResponseDTO() {
        // Given
        Booking mockBooking2 = new Booking(2L, 1L, this.mockUser, this.mockSeatLevel1);
        BookingResponseDTO mockResponseBookingDTO2 = new BookingResponseDTO(
                2L, "Beauty and the Beast", this.mockUser.getEmail(), this.mockSeatLevel1.getId(), this.mockSeatLevel1.getVenueArea(), LocalDateTime.now()
        );
        List<Booking> mockBookings = Arrays.asList(this.mockBooking, mockBooking2);
        List<BookingResponseDTO> mockBookingsDTO = Arrays.asList(this.mockResponseBookingDTO, mockResponseBookingDTO2);

        when(bookingRepository.findAll()).thenReturn(mockBookings);
        for (BookingResponseDTO dto : mockBookingsDTO) {
            when(mappingService.mapToResponseDTO(any(Booking.class))).thenReturn(dto);
        }

        // When
        List<BookingResponseDTO> actualBookingsDTO = this.bookingService.getAllBookings();

        // Then
        Assertions.assertEquals(mockBookingsDTO.size(), actualBookingsDTO.size());
        for (Object obj : actualBookingsDTO) {
            Assertions.assertInstanceOf(BookingResponseDTO.class, obj);
        }
    }

    @Test
    void getBookingById_returnsBookingResponseDTO() {
        // Given
        Long mockBookingId = this.mockResponseBookingDTO.getBookingId();
        VenueArea expectedVenueArea = this.mockResponseBookingDTO.getVenueArea();

        when(bookingRepository.findById(mockBookingId)).thenReturn(Optional.of(this.mockBooking));
        when(mappingService.mapToResponseDTO(this.mockBooking)).thenReturn(this.mockResponseBookingDTO);

        // When
        BookingResponseDTO actualBookingDTO = bookingService.getBookingById(mockBookingId);

        // Then
        Assertions.assertInstanceOf(BookingResponseDTO.class, actualBookingDTO);
        Assertions.assertEquals(expectedVenueArea, actualBookingDTO.getVenueArea());
    }

    @Test
    void getBookingsByUser_returnsUserBookingDTO() {
        // Given
        Long mockBookingUserId = this.mockBooking.getUser().getId();
        Booking mockBooking2 = new Booking(2L, this.mockSeatLevel1, this.mockUser);
        List<Booking> mockBookings = Arrays.asList(this.mockBooking, mockBooking2);
        List<BookingResponseDTO> mockBookingsResponseDTO = Arrays.asList(
                this.mockResponseBookingDTO,
                new BookingResponseDTO(mockBooking2.getId(), "Beauty and the Beast", mockBooking2.getUser().getEmail(), mockBooking2.getSeat().getId(), mockBooking2.getSeat().getVenueArea(), LocalDateTime.now())
        );
        UserBookingDTO mockUserBookingDTO = new UserBookingDTO(mockBookingUserId, mockBookingsResponseDTO);

        when(bookingRepository.findByUserId(any(Long.class))).thenReturn(mockBookings);
        for (BookingResponseDTO bookingResponseDTO : mockBookingsResponseDTO) {
            when(this.mappingService.mapToResponseDTO(any(Booking.class))).thenReturn(bookingResponseDTO);
        }

        // When
        UserBookingDTO actualUserBookingDTO = bookingService.getBookingsByUser(mockBookingUserId);

        // Then
        Assertions.assertInstanceOf(UserBookingDTO.class, actualUserBookingDTO);
        Assertions.assertEquals(mockUserBookingDTO.getBookings().size(), actualUserBookingDTO.getBookings().size());
        for (Object b : actualUserBookingDTO.getBookings()) {
            Assertions.assertNotNull(b);
        }
//        Assertions.assertEquals(mockUserBookingDTO.getBookings().get(1).getUserEmail(), actualUserBookingDTO.getBookings().get(1).getUserEmail());
    }

    @Test
    void createBooking_returnsCreatedBookingResponseDTO() {
        // Given
        String expectedUserEmail = this.mockBooking.getUser().getEmail();
        when(venueService.isVenueSoldOut()).thenReturn(false);
        when(seatService.isSeatAvailable(this.mockRequestBookingDTO.getSeatId())).thenReturn(true);
        when(mappingService.mapToBooking(any(BookingRequestDTO.class))).thenReturn(this.mockBooking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(this.mockBooking);
        when(mappingService.mapToResponseDTO(any(Booking.class))).thenReturn(this.mockResponseBookingDTO);

        // When
        BookingResponseDTO actualBookingDTO = bookingService.createBooking(this.mockRequestBookingDTO);

        // Then
        Assertions.assertInstanceOf(BookingResponseDTO.class, actualBookingDTO);
        Assertions.assertEquals(expectedUserEmail, actualBookingDTO.getUserEmail());
        Assertions.assertEquals(this.mockResponseBookingDTO.getVenueArea(), actualBookingDTO.getVenueArea());
    }

    @Test
    void createBooking_whenVenueIsSoldOut_throwsVenueSoldOutException() {
        // Given
        when(venueService.isVenueSoldOut()).thenReturn(true);

        // Then
        Assertions.assertThrows(VenueSoldOutException.class, ()
                -> bookingService.createBooking(this.mockRequestBookingDTO)
        );
    }

    @Test
    void createBooking_whenSeatIsntAvailable_throwsSeatNotAvailableException() {
        // Given
        Integer bookingDTOSeatId = this.mockRequestBookingDTO.getSeatId();

        when(venueService.isVenueSoldOut()).thenReturn(false);
        when(seatService.isSeatAvailable(bookingDTOSeatId)).thenReturn(false);

        // Then
        Assertions.assertThrows(SeatNotAvailableException.class, ()
                -> bookingService.createBooking(this.mockRequestBookingDTO)
        );
    }

    @Test
    void updateBooking_returnsUpdatedBookingResponseDTO() {
        // Given
        Long bookingIdToUpdate = this.mockBooking.getId();
        Booking updatedMockBooking = new Booking(1L, 1L, this.mockUser, this.mockSeatLevel1);
        BookingRequestDTO updatedMockRequestBookingDTO = new BookingRequestDTO(1L, updatedMockBooking.getUser().getId(), updatedMockBooking.getSeat().getId());
        BookingResponseDTO updatedMockResponseBookingDTO = new BookingResponseDTO(
                1L, "Dancing Queen", updatedMockBooking.getUser().getEmail(), updatedMockBooking.getSeat().getId(), updatedMockBooking.getSeat().getVenueArea(), LocalDateTime.now()
        );

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(this.mockBooking));
        when(seatService.isSeatAvailable(any(Integer.class))).thenReturn(true);
        when(mappingService.mapToBooking(any(BookingRequestDTO.class))).thenReturn(updatedMockBooking);
        when(seatService.getSeat(any(Integer.class))).thenReturn(updatedMockBooking.getSeat());
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedMockBooking);
        when(mappingService.mapToResponseDTO(any(Booking.class))).thenReturn(updatedMockResponseBookingDTO);

        // When
        BookingResponseDTO actualBookingDTO = bookingService.updateBooking(bookingIdToUpdate, updatedMockRequestBookingDTO);

        // Then
        Assertions.assertInstanceOf(BookingResponseDTO.class, actualBookingDTO);
        Assertions.assertEquals(updatedMockResponseBookingDTO.getSeatId(), actualBookingDTO.getSeatId());
        Assertions.assertEquals(updatedMockResponseBookingDTO.getVenueArea(), actualBookingDTO.getVenueArea());
    }

    @Test
    void updateBooking_whenDoesNotExist_throwsEntityNotFoundException() {
        // Given
        Long bookingIdToUpdate = this.mockBooking.getId();
        Booking updatedMockBooking = new Booking(1L, 1L, this.mockUser, this.mockSeatLevel1);
        BookingRequestDTO updatedMockRequestBookingDTO = new BookingRequestDTO(1L, updatedMockBooking.getUser().getId(), updatedMockBooking.getSeat().getId());

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class, ()
                -> bookingService.updateBooking(bookingIdToUpdate, updatedMockRequestBookingDTO)
        );
    }

    @Test
    void updateBooking_whenSeatIsntAvailable_throwsSeatNotAvailableException() {
        // Given
        Integer bookingDTOSeatId = this.mockRequestBookingDTO.getSeatId();
        Long bookingIdToUpdate = this.mockBooking.getId();
        Booking updatedMockBooking = new Booking(1L, 1L, this.mockUser, this.mockSeatLevel1);
        BookingRequestDTO updatedMockRequestBookingDTO = new BookingRequestDTO(1L, updatedMockBooking.getUser().getId(), updatedMockBooking.getSeat().getId());

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(this.mockBooking));
        when(seatService.isSeatAvailable(any(Integer.class))).thenReturn(false);

        // Then
        Assertions.assertThrows(SeatNotAvailableException.class, ()
                -> bookingService.updateBooking(bookingIdToUpdate, updatedMockRequestBookingDTO)
        );
    }

    @Test
    void deleteBooking_verifyDeleteRepositoryIsCalled() {
        // Given
        Long mockBookingId = this.mockBooking.getId();

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(this.mockBooking));

        // When
        bookingService.deleteBooking(mockBookingId);

        // Then
        verify(bookingRepository, times(1)).deleteById(eq(mockBookingId));
    }

    @Test
    void deleteBooking_whenBookingDoesntExist_throwsEntityNotFoundException() {
        // Given
        Long mockBookingId = this.mockBooking.getId();

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class, ()
                -> bookingService.deleteBooking(mockBookingId)
        );
    }




}