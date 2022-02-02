package com.carolina.booking_service.service;

import com.carolina.booking_service.exception.UserAlreadyExistsException;
import com.carolina.booking_service.model.User;
import com.carolina.booking_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        this.mockUser = new User(1L, "Ana", "Almeida", "ana@test.com", "testpassword");
    }

    @Test
    void getUserById_returnsUser() {
        // Given
        Long mockUserId = this.mockUser.getId();
        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(this.mockUser));

        // When
        User actualUser = userService.getUser(mockUserId);

        // Then
        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(this.mockUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void getUserById_whenDoesntExist_throwsEntityNotFoundException() {
        // Given
        Long mockUserId = this.mockUser.getId();
        when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.getUser(mockUserId);
        });
    }

    @Test
    void createUser_returnsCreatedUser() {
        // Given
        User mockUserWithDifferentEmail = new User(2L, "Carolina", "Almeida", "carplina@test.com", "password");
        mockUserWithDifferentEmail.setPassword("abcdef");
        when(userRepository.findByEmail(this.mockUser.getEmail())).thenReturn(Optional.of(mockUserWithDifferentEmail));
        when(userRepository.save(any(User.class))).thenReturn(mockUserWithDifferentEmail);

        // When
        User actualUser = userService.createUser(mockUserWithDifferentEmail);

        // Then
        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(mockUserWithDifferentEmail.getFirstName(), actualUser.getFirstName());
    }

    @Test
    void createUser_whenUserWithSameEmailAlreadyExists_throwsUserAlreadyExistsException() {
        // Given
        this.mockUser.setPassword("abcdef");
        when(userRepository.findByEmail(this.mockUser.getEmail())).thenReturn(Optional.of(this.mockUser));

        // Then
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(this.mockUser);
        });
    }

    @Test
    void updateUser_returnsUpdatedUser() {
        // Given
        Long mockUserId = this.mockUser.getId();
        User mockUpdatedUser = new User();
        mockUpdatedUser.setId(1L);
        mockUpdatedUser.setFirstName("Ana Carolina");
        mockUpdatedUser.setEmail("ana@test.com");

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(this.mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUpdatedUser);

        // When
        User actualUser = userService.updateUser(mockUpdatedUser);

        // Then
        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(mockUpdatedUser.getFirstName(), actualUser.getFirstName());
    }

    @Test
    void updateUser_whenUserDoesntExist_throwsEntityNotFoundException() {
        // Given
        when(userRepository.findById(this.mockUser.getId())).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(this.mockUser);
        });
    }

    @Test
    void delete_verifyDeleteRepositoryIsCalled() {
        // Given
        Long mockUserId = this.mockUser.getId();

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(this.mockUser));

        // When
        userService.deleteUser(mockUserId);

        // Then
        verify(userRepository, times(1)).deleteById(eq(mockUserId));
    }

    @Test
    void deleteUser_whenUserDoesntExist_throwsEntityNotFoundException() {
        // Given
        Long mockUserId = this.mockUser.getId();

        when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class, ()
                -> userService.deleteUser(mockUserId)
        );
    }


}