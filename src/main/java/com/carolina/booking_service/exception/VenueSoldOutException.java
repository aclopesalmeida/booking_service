package com.carolina.booking_service.exception;

public class VenueSoldOutException extends RuntimeException {
    public VenueSoldOutException() {
        super("The venue is sold out!");
    }
}
