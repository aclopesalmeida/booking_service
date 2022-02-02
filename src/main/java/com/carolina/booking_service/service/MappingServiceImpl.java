package com.carolina.booking_service.service;

import com.carolina.booking_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MappingServiceImpl implements MappingService {

    private final UserService userService;
    private final SeatService seatService;
    private final RestTemplate restTemplate;
    @Value("${showApiUrl}")
    private String showApiUrl;

    @Autowired
    public MappingServiceImpl(UserService userService, SeatService seatService, RestTemplate restTemplate) {
        this.userService = userService;
        this.seatService = seatService;
        this.restTemplate = restTemplate;
    }

    @Override
    public BookingResponseDTO mapToResponseDTO(Booking booking) {
        // Fetch show data
        String url = this.showApiUrl + '/' + booking.getShowId();
        String showName;
        // Fetch seat data
        Seat seat = booking.getSeat();

        try {
            Show show = restTemplate.getForObject(url, Show.class);
            return new BookingResponseDTO(
                    booking.getId(),
                    show.getName(),
                    booking.getUser().getEmail(),
                    seat.getId(),
                    seat.getVenueArea(),
                    booking.getCreatedAt()
            );
        } catch (Exception e) {
            showName = "";
            return new BookingResponseDTO(
                    booking.getId(),
                    showName,
                    booking.getUser().getEmail(),
                    seat.getId(),
                    seat.getVenueArea(),
                    booking.getCreatedAt()
            );
        }
    }

    @Override
    public Booking mapToBooking(BookingRequestDTO bookingDTO) {
        Seat seat = seatService.getSeat(bookingDTO.getSeatId());
        User user = userService.getUser(bookingDTO.getUserId());

        return new Booking(bookingDTO.getShowId(), seat, user);
    }
}
