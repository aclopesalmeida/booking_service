package com.carolina.booking_service.controller;

import com.carolina.booking_service.exception.SeatNotAvailableException;
import com.carolina.booking_service.exception.VenueSoldOutException;
import com.carolina.booking_service.model.BookingRequestDTO;
import com.carolina.booking_service.model.BookingResponseDTO;
import com.carolina.booking_service.service.BookingService;
import com.carolina.booking_service.validation.Create;
import com.carolina.booking_service.validation.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;

@RequestMapping("/api/v1/bookings")
@RestController
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BookingResponseDTO>> getBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
       BookingResponseDTO bookingDTO = bookingService.getBookingById(id);
       return ResponseEntity.ok().body(bookingDTO);
    }

    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody @Validated(Create.class) BookingRequestDTO bookingDTO) {
        try {
            BookingResponseDTO newBooking = bookingService.createBooking(bookingDTO);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                        .path("/{id}")
                                                        .buildAndExpand(newBooking.getBookingId())
                                                        .toUri();
            return ResponseEntity.created(location).build();
        } catch (VenueSoldOutException | SeatNotAvailableException exception) {
            return ResponseEntity.ok().body(exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> updateBooking(@PathVariable Long id, @RequestBody @Validated(Update.class) BookingRequestDTO bookingDTO) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
            return ResponseEntity.ok().body(updatedBooking);
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

}
