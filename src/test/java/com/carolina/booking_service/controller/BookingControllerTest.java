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

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;

    private BookingRequestDTO mockRequestBooking;
    private BookingResponseDTO mockResponseBooking;

    private final String port = String.valueOf(8080);
    private final String baseUrl = "http://localhost:";
    private String apiUrl;

    @BeforeEach
    void setUp() {
        apiUrl = baseUrl + port + "/api/v1/bookings";

        mockRequestBooking =  new BookingRequestDTO(1L, 1L, 1);
        mockResponseBooking =  new BookingResponseDTO(1L, "Dancing Queen", "user@test.com", 1, VenueArea.LEVEL_1, LocalDateTime.now());
    }

    @Test
    void get_bookingById_returns200WithBookingResponseDTO() throws Exception {
        // Given
        Long bookingId = this.mockResponseBooking.getBookingId();
        String url = this.apiUrl + '/' + bookingId;

        when(bookingService.getBookingById(bookingId)).thenReturn(this.mockResponseBooking);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        BookingResponseDTO actualBooking = this.mapper.readValue(response.getContentAsString(), BookingResponseDTO.class);

        // Then
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertNotNull(actualBooking);
        Assertions.assertEquals(this.mockResponseBooking.getShowName(), actualBooking.getShowName());
    }

    @Test
    void post_createBooking_returns201() throws Exception {
        // Given
        String url = apiUrl;

        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(this.mockResponseBooking);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(this.mapper.writeValueAsString(this.mockRequestBooking));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }
//
    @Test
    void put_updateBooking_returns200WithUpdatedBookingResponseDTO() throws Exception {
        // Given
        Long bookingId = this.mockResponseBooking.getBookingId();
        String url = this.apiUrl + '/' + bookingId;
        Integer updatedSeatId = 2;
        VenueArea updatedVenueArea = VenueArea.LEVEL_1;
        BookingRequestDTO updatedMockRequestBookingDTO = new BookingRequestDTO(1L, 1L, updatedSeatId);
        BookingResponseDTO updatedMockResponseBookingDTO = new BookingResponseDTO(
                bookingId, "Dancing Queen", this.mockResponseBooking.getUserEmail(), updatedSeatId, updatedVenueArea, LocalDateTime.now()
        );

        when(bookingService.updateBooking(any(Long.class), any(BookingRequestDTO.class))).thenReturn(updatedMockResponseBookingDTO);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .content(this.mapper.writeValueAsString(updatedMockRequestBookingDTO));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        BookingResponseDTO actualBookingDTO = this.mapper.readValue(response.getContentAsString(), BookingResponseDTO.class);
        Assertions.assertEquals(updatedMockResponseBookingDTO.getSeatId(), actualBookingDTO.getSeatId());
        Assertions.assertEquals(updatedMockResponseBookingDTO.getVenueArea(), actualBookingDTO.getVenueArea());
    }
//
    @Test
    void put_updateNonExistingBooking_catchesExceptionAndReturns404() throws Exception {
        // Given
        Long bookingId = this.mockResponseBooking.getBookingId();
        String url = this.apiUrl + '/' + bookingId;
        Integer updatedSeatId = 2;
        VenueArea updatedVenueArea = VenueArea.LEVEL_1;
        BookingRequestDTO updatedMockRequestBookingDTO = new BookingRequestDTO(1L, 1L, updatedSeatId);
        BookingResponseDTO updatedMockResponseBookingDTO = new BookingResponseDTO(
                bookingId, "Dancing Queen", this.mockResponseBooking.getUserEmail(), updatedSeatId, updatedVenueArea, LocalDateTime.now()
        );

        when(bookingService.updateBooking(any(Long.class), any(BookingRequestDTO.class))).thenThrow(EntityNotFoundException.class);

        // When
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .content(this.mapper.writeValueAsString(updatedMockRequestBookingDTO));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        // Then
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void delete_deleteBooking_returns200() throws Exception {
        // Given
        Long bookingId = this.mockResponseBooking.getBookingId();
        String url = this.apiUrl + '/' + bookingId;

        doNothing().when(bookingService).deleteBooking(bookingId);

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
    void delete_deleteNonExistingBooking_catchesExceptionAndReturns404() throws Exception {

        // Given
        Long bookingId = this.mockResponseBooking.getBookingId();
        String url = this.apiUrl + '/' + bookingId;

        doThrow(EntityNotFoundException.class).when(bookingService).deleteBooking(bookingId);

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