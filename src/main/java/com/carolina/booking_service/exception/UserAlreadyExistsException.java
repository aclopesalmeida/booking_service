package com.carolina.booking_service.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("The specified email address has already been taken!");
    }
}
