package com.carolina.booking_service.controller;

import com.carolina.booking_service.exception.UserAlreadyExistsException;
import com.carolina.booking_service.model.User;
import com.carolina.booking_service.model.UserBookingDTO;
import com.carolina.booking_service.service.BookingService;
import com.carolina.booking_service.service.UserService;
import com.carolina.booking_service.validation.Create;
import com.carolina.booking_service.validation.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;
    private BookingService bookingService;

    @Autowired
    public UserController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUser(id);
            return ResponseEntity.ok().body(user);
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<UserBookingDTO> getUserBookings(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(bookingService.getBookingsByUser(id));
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody @Validated(Create.class) User user) {
        try {
            User newUser = userService.createUser(user);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                    .path("/{id}")
                                                    .buildAndExpand(newUser.getId())
                                                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (UserAlreadyExistsException userAlreadyExistsException) {
            return ResponseEntity.ok().body(userAlreadyExistsException.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Validated(Update.class) User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }
}
