package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BookingCustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void testCreateBookingEndpoint_Success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(2));
        request.setTimeSlot("19:00 - 20:00");

        Mockito.when(bookingService.createBooking(any(), any())).thenReturn(BookingResponse.builder().id(1L).build());

        mockMvc.perform(post("/api/v1/customer/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "nhat2006"))
                .andExpect(status().isCreated()); // Phải trả về mã 210 Created chuẩn RESTful
    }

    @Test
    void testGetMyBookingHistoryEndpoint_Success() throws Exception {
        Mockito.when(bookingService.getCustomerBookingHistory(any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/v1/customer/bookings")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "nhat2006"))
                .andExpect(status().isOk());
    }
}