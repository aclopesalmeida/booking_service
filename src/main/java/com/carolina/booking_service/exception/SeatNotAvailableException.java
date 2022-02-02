package com.carolina.booking_service.exception;

public class SeatNotAvailableException extends RuntimeException {

    public SeatNotAvailableException() {
        super("The chosen seat is currently not available!");
    }
}
