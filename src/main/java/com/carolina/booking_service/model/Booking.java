package com.carolina.booking_service.model;

import com.carolina.booking_service.validation.Create;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long showId;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotEmpty(groups = Create.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_booking_user_id"), nullable = false)
    private User user;

    @NotEmpty(groups = Create.class)
    @OneToOne
    @JoinColumn(name = "seatId", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_booking_seat_id"), nullable = false)
    private Seat seat;

    public Booking() {}

    public Booking(Long showId, Seat seat, User user) {
        this.showId = showId;
        this.seat = seat;
        this.user = user;
    }

    public Booking(Long id, Long showId, User user, Seat seat) {
        this.id = id;
        this.showId = showId;
        this.user = user;
        this.seat = seat;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public Seat getSeat() {
        Seat copy = new Seat(this.seat.getId(), this.seat.getVenueArea(), this.seat.isEnabled(), this.seat.isBooked());
        copy.setCreatedAt(this.seat.getCreatedAt());
        copy.setUpdatedAt(this.seat.getUpdatedAt());
        if (seat.getBooking() != null) {
            copy.setBooking(seat.getBooking());
        }
            return copy;
    }

    public void setSeat(Seat seat) {
        Seat copy = new Seat(seat.getId(), seat.getVenueArea(), seat.isEnabled(), seat.isBooked());
        copy.setCreatedAt(seat.getCreatedAt());
        copy.setUpdatedAt(seat.getUpdatedAt());
        if (seat.getBooking() != null) {
            copy.setBooking(seat.getBooking());
        }
        this.seat = copy;
    }

    public User getUser() {
        User copy = new User(this.user.getId(), this.user.getFirstName(), this.user.getLastName(), this.user.getEmail(), this.user.getPassword());
        copy.setCreatedAt(this.user.getCreatedAt());
        copy.setUpdatedAt(this.user.getUpdatedAt());
        if (user.getBookings() != null) {
            copy.setBookings(user.getBookings());
        }
        return copy;
    }

    public void setUser(User user) {
        User copy = new User(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        copy.setCreatedAt(user.getCreatedAt());
        copy.setUpdatedAt(user.getUpdatedAt());
        if (user.getBookings() != null) {
            copy.setBookings(user.getBookings());
        }
        this.user = copy;
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

    @Override
    public String toString() {
        String result = "Booking{";
        if (id != null) {
            result += id + " ,";
        }
        if (user != null) {
            result += user + " ,";
        }
        if (seat != null) {
            result += seat + " ,";
        }
        return result;
    }
}
