package com.carolina.booking_service.service;

import com.carolina.booking_service.model.User;

public interface UserService {
    User getUser(Long userId);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long userId);
}
