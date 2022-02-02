package com.carolina.booking_service.model;

import com.carolina.booking_service.validation.Create;
import com.carolina.booking_service.validation.Update;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingResponseDTO {

    @NotEmpty
    private Long bookingId;

    @NotEmpty
    private String showName;

    @NotEmpty
    private String userEmail;

    @NotNull(groups = {Create.class, Update.class})
    private Integer seatId;

    @NotEmpty
    private VenueArea venueArea;

    @CreationTimestamp
    @NotEmpty
    private LocalDateTime createdAt;

    public BookingResponseDTO() {}

    public BookingResponseDTO(Long bookingId, String showName, String userEmail, Integer seatId, VenueArea venueArea, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.showName = showName;
        this.userEmail = userEmail;
        this.seatId = seatId;
        this.venueArea = venueArea;
        this.createdAt = createdAt;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public VenueArea getVenueArea() {
        return venueArea;
    }

    public void setVenueArea(VenueArea venueArea) {
        this.venueArea = venueArea;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
