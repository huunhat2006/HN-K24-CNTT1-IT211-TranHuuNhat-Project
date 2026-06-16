package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.customer;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.CourtImageResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.CourtImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
// ĐÃ SỬA: Chuyển về đúng ranh giới của Manager để tránh bị dính lỗi 403 Forbidden
@RequestMapping("/api/v1/manager/courts")
@RequiredArgsConstructor
public class CourtManagerController {

    private final CourtImageService courtImageService;

    // Manager tiến hành tải lên hàng loạt ảnh chi tiết cho sân cầu lông thuộc quyền sở hữu của mình
    // POST /api/v1/manager/courts/1/images
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCourtImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication) {
        try {
            // Trích xuất username của Manager đang đăng nhập từ Token bảo mật
            String managerUsername = authentication.getName();
            
            // Gọi service xử lý upload Cloudinary và kiểm toán chủ quyền sân
            List<CourtImageResponse> responses = courtImageService.uploadMultipleImages(id, files, managerUsername);
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}