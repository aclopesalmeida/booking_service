package com.carolina.booking_service.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VenueArea venueArea;
    @Column(columnDefinition = "boolean default true")
    private Boolean enabled = true;
    @Column(columnDefinition = "boolean default false")
    private Boolean booked = false;
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "seat")
    private Booking booking;

    public Seat() {}

    public Seat(VenueArea venueArea) {
        this.venueArea = venueArea;
    }

    /**
     * Constructor handy for unit testing
     */
    public Seat(Integer id, VenueArea venueArea, Boolean enabled, Boolean booked) {
        this.id = id;
        this.venueArea = venueArea;
        this.enabled = enabled;
        this.booked = booked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VenueArea getVenueArea() {
        return venueArea;
    }

    public void setVenueArea(VenueArea venueArea) {
        this.venueArea = venueArea;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isBooked() {
        return booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        String result = "Booking{";
        if (id != null) {
            result += id + " ,";
        }
        if (venueArea != null) {
            result += venueArea + " ,";
        }
        if (enabled != null) {
            result += enabled + " ,";
        }
        if (booked != null) {
            result += booked + " ,";
        }
        return result;
    }
}
