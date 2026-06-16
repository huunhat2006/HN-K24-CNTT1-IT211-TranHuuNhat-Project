package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.BadmintonCluster;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.BookingRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CourtRepository courtRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testCreateBooking_Success() {
        // 1. Arrange
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("17:00 - 18:00");

        User user = User.builder().id(1L).username("nhat2006").build();
        Court court = Court.builder().id(1L).courtName("Sân số 1").build();
        Booking savedBooking = Booking.builder().id(100L).bookingDate(request.getBookingDate()).timeSlot("17:00 - 18:00").court(court).status("PENDING").totalPrice(120000.0).build();

        Mockito.when(userRepository.findByUsername("nhat2006")).thenReturn(Optional.of(user));
        Mockito.when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        Mockito.when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(any(), any(), any(), any())).thenReturn(false);
        Mockito.when(bookingRepository.save(any())).thenReturn(savedBooking);

        // 2. Act
        BookingResponse response = bookingService.createBooking(request, "nhat2006");

        // 3. Assert
        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals("Sân số 1", response.getCourtName());
    }

    @Test
    void testCreateBooking_Conflict_ThrowsException() {
        // 1. Arrange
        BookingRequest request = new BookingRequest();
        request.setCourtId(1L);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("17:00 - 18:00");

        User user = User.builder().id(1L).username("nhat2006").build();
        Court court = Court.builder().id(1L).courtName("Sân số 1").build();

        Mockito.when(userRepository.findByUsername("nhat2006")).thenReturn(Optional.of(user));
        Mockito.when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        // Giả lập lịch bị trùng ca
        Mockito.when(bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(any(), any(), any(), any())).thenReturn(true);

        // 2. Act & Assert
        Exception ex = assertThrows(RuntimeException.class, () -> bookingService.createBooking(request, "nhat2006"));
        assertEquals("Khung giờ này đã có người đặt!", ex.getMessage());
    }

    // Bổ sung hàm này vào file BookingServiceImplTest.java sẵn có của ông
    @Test
    void testGetBookingsForManager_Success() {
        org.springframework.data.domain.Page<com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking> fakePage =
                org.springframework.data.domain.Page.empty();

        Mockito.when(bookingRepository.findByClusterManagerUsername(Mockito.anyString(), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(fakePage);

        Map<String, Object> result = bookingService.getBookingsForManager("manager1", 0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testUpdateBookingStatus_NotOwner_ThrowsException() {
        // 1. Arrange: Dựng cây đối tượng dữ liệu THẬT lồng nhau (booking -> court -> cluster -> manager)
        User managerVip = User.builder()
                .username("manager_bien_hoa")
                .role("MANAGER")
                .build();

        // Đã sửa đổi sang đúng tên thực thể BadmintonCluster theo cấu hình SQL của ông
        BadmintonCluster cluster = BadmintonCluster.builder()
                .manager(managerVip)
                .build();

        Court court = Court.builder()
                .cluster(cluster)
                .build();

        com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking fakeBooking =
                com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking.builder()
                        .id(1L)
                        .court(court)
                        .status("PENDING")
                        .build();

        // Giả lập ông Manager ở Hà Nội cố tình gửi request hack đè duyệt sân của cụm Biên Hòa ở trên
        User currentUser = User.builder()
                .username("manager_ha_noi")
                .role("MANAGER")
                .build();

        // Thực hiện nạp giả lập cho tầng Repository gác cổng
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(fakeBooking));
        Mockito.when(userRepository.findByUsername("manager_ha_noi")).thenReturn(Optional.of(currentUser));

        // 2. Act & Assert: Thực thi hàm nghiệp vụ gia cố bảo mật và bẫy lỗi kì vọng ném ra ngoại lệ
        assertThrows(RuntimeException.class, () ->
                bookingService.updateBookingStatus(1L, "CONFIRMED", "manager_ha_noi"));
    }
}