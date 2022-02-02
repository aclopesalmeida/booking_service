package com.carolina.booking_service.service;

import com.carolina.booking_service.exception.SeatNotAvailableException;
import com.carolina.booking_service.exception.VenueSoldOutException;
import com.carolina.booking_service.model.*;
import com.carolina.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VenueService venueService;
    private final SeatService seatService;
    private final UserService userService;
    private final MappingService mappingService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, VenueService venueService, SeatService seatService, UserService userService, MappingService mappingService) {
        this.bookingRepository = bookingRepository;
        this.venueService = venueService;
        this.seatService = seatService;
        this.userService = userService;
        this.mappingService = mappingService;
    }

    /**
     * Returns all of the bookings
     * @return List<BookingResponseDTO>
     */
    @Override
    public List<BookingResponseDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingResponseDTO> bookingsDTO = new ArrayList<>();
        bookings.stream()
                .map(b -> this.mappingService.mapToResponseDTO(b))
                .forEach(bookingsDTO::add);

        return bookingsDTO;
    }

    /**
     * Gets a booking based on the id
     * @param id: booking id
     * @return Optional<BookingResponseDTO>
     */
    @Override
    public BookingResponseDTO getBookingById(Long id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (!bookingOptional.isPresent()) {
            throw new EntityNotFoundException();
        }
        return this.mappingService.mapToResponseDTO(bookingOptional.get());
    }

    /**
     * Returns all of the user's bookings
            * @param userId: the user's id
            * @return List<BookingDTO>
     */
    @Override
    public UserBookingDTO getBookingsByUser(Long userId) {
        List<Booking> bookingsPerUser = bookingRepository.findByUserId(userId);
        List<BookingResponseDTO> bookingsDTO = new ArrayList<>();
        for (Booking b : bookingsPerUser) {
            bookingsDTO.add(this.mappingService.mapToResponseDTO(b));
        }
        return new UserBookingDTO(userId, bookingsDTO);
    }

    /**
     *
     * @param bookingDTO: BookingRequestDTO record
     * @return bookingResponseDTO record
     */
    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO bookingDTO) {
        // Check if the venus is still not sold out
        Boolean isVenueSoldOut = venueService.isVenueSoldOut();
        if (isVenueSoldOut) {
            throw new VenueSoldOutException();
        }

        // Check if the chosen seat is available (currently in use and not booked yet)
        boolean isSeatAvailable = seatService.isSeatAvailable(bookingDTO.getSeatId());
        if (!isSeatAvailable) {
            throw new SeatNotAvailableException();
        }

        // Create booking
        Booking booking = this.mappingService.mapToBooking(bookingDTO);
        bookingRepository.save(booking);

        // Set related seat 'booked' status
        seatService.changeSeatBookingStatus(bookingDTO.getSeatId(), true);

        // Check and update, if needed, the venue's sold out status
        venueService.changeVenueAvailability();

        return this.mappingService.mapToResponseDTO(booking);
    }

    /**
     * Updates the specified booking and related seat object
     * @param bookingDTO: BookingRequestDTO object
     * @param id: Booking record id
     * @return BookingResponseDTO
     */
    @Override
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO bookingDTO) {
        // Check if the booking exists
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (!bookingOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        // Check if the chosen seat is available (currently in use and not booked yet)
        Integer chosenSeatId = bookingDTO.getSeatId();
        boolean isSeatAvailable = seatService.isSeatAvailable(chosenSeatId);
        if (!isSeatAvailable) {
            throw new SeatNotAvailableException();
        }

        // Update booking (only the seat)
        Booking existingBooking = bookingOptional.get();
        if (bookingDTO.getSeatId() != null) {
            Seat chosenSeat = seatService.getSeat(chosenSeatId);
            existingBooking.setSeat(chosenSeat);
        }
        bookingRepository.save(existingBooking);

         // Set the previously-chosen seat as not booked if the seat id has changed
        Integer previousSeatId = existingBooking.getSeat().getId();
        if (!Objects.equals(previousSeatId, chosenSeatId)) {
            seatService.changeSeatBookingStatus(previousSeatId, false);
        }
        // Set the chosen seat as booked
        seatService.changeSeatBookingStatus(chosenSeatId, true);

        return this.mappingService.mapToResponseDTO(existingBooking);
    }

    /**
     * Deletes the specified booking and updated the related seat object
     * @param id: booking id
     */
    @Override
    public void deleteBooking(Long id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (!bookingOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        // Delete booking
        bookingRepository.deleteById(id);
        // Set related seat as not booked
        Booking booking = bookingOptional.get();
        seatService.changeSeatBookingStatus(booking.getSeat().getId(), false);
    }
}
