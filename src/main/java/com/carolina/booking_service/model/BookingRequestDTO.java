package com.carolina.booking_service.model;

import com.carolina.booking_service.validation.Create;
import com.carolina.booking_service.validation.Update;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingRequestDTO {

    @NotNull
    private Long showId;

    @NotNull(groups = Create.class)
    private Long userId;

    @NotNull(groups = {Create.class, Update.class})
    private Integer seatId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public BookingRequestDTO() {}

    public BookingRequestDTO(Long showId, Long userId, Integer seatId) {
        this.showId = showId;
        this.userId = userId;
        this.seatId = seatId;
    }


    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
