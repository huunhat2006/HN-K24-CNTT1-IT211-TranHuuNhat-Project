package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.request.BookingRequest;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.BookingResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Booking;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.BadRequestException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.DataConflictException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.ResourceNotFoundException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.BookingRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản đặt sân!"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Sân cầu lông yêu cầu không tồn tại!"));

        boolean isConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
                request.getCourtId(), request.getBookingDate(), request.getTimeSlot(), Arrays.asList("PENDING", "CONFIRMED"));

        if (isConflict) {
            throw new DataConflictException("Xung đột lịch trình: Khung giờ này tại sân đã có người đặt trước!");
        }

        Booking booking = Booking.builder()
                .bookingDate(request.getBookingDate())
                .timeSlot(request.getTimeSlot())
                .totalPrice(120000.0)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .user(user)
                .court(court)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    @Override
    public Map<String, Object> getCustomerBookingHistory(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử: User không tồn tại!"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingRepository.findByUserId(user.getId(), pageable);

        List<BookingResponse> content = bookingPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return packagePaginatedResponse(bookingPage, content);
    }

    @Override
    public Map<String, Object> getAllBookingsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingRepository.findAll(pageable);

        List<BookingResponse> content = bookingPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return packagePaginatedResponse(bookingPage, content);
    }

    @Override
    public Map<String, Object> getBookingsForManager(String managerUsername, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingRepository.findByClusterManagerUsername(managerUsername, pageable);

        List<BookingResponse> content = bookingPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return packagePaginatedResponse(bookingPage, content);
    }

    @Override
    @Transactional
    // ĐÃ GIA CỐ BẢO MẬT CHỦ QUYỀN
    public BookingResponse updateBookingStatus(Long bookingId, String status, String managerUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt sân số: " + bookingId));

        // KIỂM TRA: Nếu người gọi API không phải là ADMIN, thì bắt buộc phải là MANAGER sở hữu cụm sân đó
        User currentUser = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản đang thao tác!"));

        if (!currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            String ownerUsername = booking.getCourt().getCluster().getManager().getUsername();
            if (!ownerUsername.equalsIgnoreCase(managerUsername)) {
                throw new BadRequestException("Vi phạm bảo mật: Bạn không có quyền phê duyệt đơn đặt thuộc sân của người khác!");
            }
        }

        String upperStatus = status.toUpperCase();
        if (!upperStatus.equals("CONFIRMED") && !upperStatus.equals("CANCELED")) {
            throw new BadRequestException("Yêu cầu phê duyệt thất bại: Trạng thái cập nhật không hợp lệ!");
        }

        booking.setStatus(upperStatus);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToResponse(updatedBooking);
    }

    private Map<String, Object> packagePaginatedResponse(Page<Booking> page, List<BookingResponse> content) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", content);
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        return response;
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .timeSlot(booking.getTimeSlot())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .courtName(booking.getCourt() != null ? booking.getCourt().getCourtName() : "SÂN ĐÃ BỊ XÓA")
                .build();
    }
}