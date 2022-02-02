package com.carolina.booking_service.service;

import java.util.ArrayList;

import com.carolina.booking_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
        User user = userService.getUserByEmail(email);
        return new org.springframework.security.core.userdetails.User(email, user.getPassword(),
                    new ArrayList<>());
        } catch (EntityNotFoundException exception) {
            throw new EntityNotFoundException("No such user with the specified email address");
        }
    }
}