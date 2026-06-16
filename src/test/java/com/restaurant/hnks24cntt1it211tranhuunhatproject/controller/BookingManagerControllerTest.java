package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.manager.BookingManagerController;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BookingManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void testGetManagedBookings_Success() throws Exception {
        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("currentPage", 0);
        fakeResponse.put("totalItems", 0);

        Mockito.when(bookingService.getBookingsForManager(anyString(), anyInt(), anyInt()))
                .thenReturn(fakeResponse);

        mockMvc.perform(get("/api/v1/manager/bookings")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "manager1")) // Giả lập Manager1 đăng nhập
                .andExpect(status().isOk());
    }

    // Thêm hàm test này vào file BookingManagerControllerTest ở trên
    @Test
    void testManagerProcessBooking_Success() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/manager/bookings/1/status")
                        .param("status", "CONFIRMED")
                        .principal(() -> "manager1"))
                .andExpect(status().isOk());
    }
}