package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.admin;

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
@RequestMapping("/api/v1/admin/courts")
@RequiredArgsConstructor
public class CourtAdminController {

    private final CourtImageService courtImageService;

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCourtImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication) { // Thêm Authentication lấy danh tính người gõ
        try {
            String managerUsername = authentication.getName();
            List<CourtImageResponse> responses = courtImageService.uploadMultipleImages(id, files, managerUsername);
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}