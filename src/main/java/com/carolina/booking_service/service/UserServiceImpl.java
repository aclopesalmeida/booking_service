package com.carolina.booking_service.service;

import com.carolina.booking_service.exception.UserAlreadyExistsException;
import com.carolina.booking_service.model.*;
import com.carolina.booking_service.repository.BookingRepository;
import com.carolina.booking_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Gets a user by id
     * @param userId: the user's id
     * @return User
     */
    @Override
    public User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException();
        }

        return optionalUser.get();
    }


    /**
     * Gets a user based on the email address
     * @param email: user's email address
     * @return User
     */
    @Override
    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException();
        }
        return userOptional.get();
    }

    /**
     * Creates a new user
     * @param user: user record
     */
    @Override
    public User createUser(User user) {
        // Check if a user already exists with the same email address
        Optional<User> userWithSameEmailOptional = userRepository.findByEmail(user.getEmail());
        if (userWithSameEmailOptional.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        String password = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(password);
        return userRepository.save(user);
    }

    /**
     * Updates the specified user
     * @param user: User record
     */
    @Override
    public User updateUser(User user) {
        // Check if the user exists
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException();
        }

        // Prevent email changes
        User existingUser = userOptional.get();
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        // Update user
        return userRepository.save(existingUser);
    }

    /**
     * Deletes the specified user
     * @param userId: the user's id
     */
    @Override
    public void deleteUser(Long userId) {
        // Check if the user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException();
        }
        // Delete user
        userRepository.deleteById(userId);
    }
}
