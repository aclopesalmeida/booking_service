package com.carolina.booking_service.seeder;

import com.carolina.booking_service.model.*;
import com.carolina.booking_service.repository.BookingRepository;
import com.carolina.booking_service.repository.SeatRepository;
import com.carolina.booking_service.repository.UserRepository;
import com.carolina.booking_service.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(value="loadSeeder", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public DataSeeder(VenueRepository venueRepository, UserRepository userRepository, SeatRepository seatRepository, BookingRepository bookingRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
        loadVenueData();
        loadSeatData();
        loadBookingData();
    }

    private void loadBookingData() {
        if (bookingRepository.count() == 0) {
            Seat seat1 = seatRepository.findById(1).get();
            Seat seat2 = seatRepository.findById(2).get();
            Seat seat3 = seatRepository.findById(3).get();

            User user1 = userRepository.findById(1L).get();
            User user2 = userRepository.findById(2L).get();

            List<Booking> bookings = Arrays.asList(
                    new Booking(1L, seat1, user1),
                    new Booking(1L, seat2, user2),
                    new Booking(1L, seat3, user2)
            );
            bookingRepository.saveAll(bookings);
        }
    }

    private void loadSeatData() {
        if (seatRepository.count() == 0) {
            List<Seat> seats = Arrays.asList(
                    new Seat(VenueArea.FLOOR),
                    new Seat(VenueArea.FLOOR),
                    new Seat(VenueArea.LEVEL_1),
                    new Seat(VenueArea.LEVEL_2),
                    new Seat(VenueArea.LEVEL_1)
                    );
            seatRepository.saveAll(seats);
        }
    }

    private void loadUserData() {
        if (userRepository.count() == 0) {
            List<User> users = Arrays.asList(
                    new User("Ana", "Almeida", "ana.almeida@gmail.com", bCryptPasswordEncoder.encode("testePassword")),
                    new User("José", "Loureiro", "jose_loureiro@gmail.com", bCryptPasswordEncoder.encode("myPassword"))
            );
            userRepository.saveAll(users);
        }
    }

    private void loadVenueData() {
        if (venueRepository.count() == 0) {
            Venue venue = new Venue();
            venue.setName("XPTO Arena");
            venue.setAddress("Avenida da Boavista, nº3 Porto");
            venueRepository.save(venue);
        }
    }
}
