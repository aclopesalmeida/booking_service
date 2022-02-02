package com.carolina.booking_service.controller;

import com.carolina.booking_service.model.*;
import com.carolina.booking_service.service.BookingService;
import com.carolina.booking_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;

    private User mockUser;

    private final String port = String.valueOf(8080);
    private final String baseUrl = "http://localhost:";
    private String apiUrl;

    @BeforeEach
    void setUp() {
        apiUrl = baseUrl + port + "/api/v1/users";
        mockUser = new User(1L, "Ana", "Almeida", "test@email.com", "testpassword");
    }

    @Test
    void get_userById_returns200WithUser() throws Exception {
        // Given
        Long userId = this.mockUser.getId();
        String url = this.apiUrl + '/' + userId;

        when(userService.getUser(userId)).thenReturn(this.mockUser);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        User actualUser = this.mapper.readValue(response.getContentAsString(), User.class);

        // Then
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(this.mockUser.getId(), actualUser.getId());
        assertEquals("Ana", actualUser.getFirstName());
    }

    @Test
    void get_userById_whenUserDoesntExist_catchesExceptionAndReturns404() throws Exception {
        // GiveN
        Long userId = this.mockUser.getId();
        String url = this.apiUrl + '/' + this.mockUser.getId();
        when(userService.getUser(userId)).thenThrow(EntityNotFoundException.class);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void get_userBookingsById_returns200WithUserBookingDTO() throws Exception {
        // Given
        Long userId = this.mockUser.getId();
        String url = String.format("%s/%o/bookings", this.apiUrl, userId);

        Seat mockSeat1 = new Seat();
        mockSeat1.setId(1);
        mockSeat1.setVenueArea(VenueArea.FLOOR);
        Seat mockSeat2 = new Seat();
        mockSeat2.setId(2);
        mockSeat2.setVenueArea(VenueArea.LEVEL_1);
        UserBookingDTO mockUserBookingDTO = new UserBookingDTO(1L, Arrays.asList(
                new BookingResponseDTO(1L, "Dancing Queen", this.mockUser.getEmail(), mockSeat1.getId(), mockSeat1.getVenueArea(), LocalDateTime.now()),
                new BookingResponseDTO(1L, "Beauty and the Beast", this.mockUser.getEmail(), mockSeat2.getId(), mockSeat2.getVenueArea(), LocalDateTime.now())
        ));
        Integer expectedBookingsCount = 2;

        when(bookingService.getBookingsByUser(userId)).thenReturn(mockUserBookingDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        UserBookingDTO actualUserBookings = this.mapper.readValue(response.getContentAsString(), UserBookingDTO.class);
        Assertions.assertEquals(expectedBookingsCount, actualUserBookings.getBookings().size());
    }


    @Test
    void post_createUser_returns201() throws Exception {
        // Given
        String url = apiUrl;

        when(userService.createUser(any(User.class))).thenReturn(this.mockUser);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.mockUser));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void post_createUser_whenFirstLastEmailPasswordFieldsAreMissing_returns400() throws Exception {
        // Given
        String url = apiUrl;
        User mockUserWithoutFields = new User();
        mockUserWithoutFields.setId(1L);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(mockUserWithoutFields));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    void put_updateUser_returns200() throws Exception {
        // Given
        Long userId = this.mockUser.getId();
        String url = this.apiUrl + '/' + userId;

        User updatedMockUser = new User();
        updatedMockUser.setId(1L);
        updatedMockUser.setFirstName("Carolina");

        when(userService.updateUser(any(User.class))).thenReturn(updatedMockUser);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(this.mapper.writeValueAsString(updatedMockUser));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void put_updateNonExistingUser_catchesExceptionAndReturns404() throws Exception {
        // Given
        Long userId = this.mockUser.getId();
        String url = this.apiUrl + '/' + userId;

        User updatedMockUser = new User();
        updatedMockUser.setId(1L);
        updatedMockUser.setFirstName("Carolina");

        when(userService.updateUser(any(User.class))).thenThrow(EntityNotFoundException.class);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON)
                                                .content(this.mapper.writeValueAsString(updatedMockUser));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void delete_deleteUser_returns200() throws Exception {
        // Given
        Long userId = this.mockUser.getId();
        String url = this.apiUrl + '/' + userId;

        doNothing().when(userService).deleteUser(userId);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url)
                                                    .accept(MediaType.APPLICATION_JSON)
                                                    .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void delete_deleteNonExistingUser_catchesExceptionAndReturns404() throws Exception {

        // Given
        Long userId = this.mockUser.getId();
        String url = this.apiUrl + '/' + userId;

        doThrow(EntityNotFoundException.class).when(userService).deleteUser(userId);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}