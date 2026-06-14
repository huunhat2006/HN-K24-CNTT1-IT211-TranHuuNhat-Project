package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.CourtImageResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CourtImageService {
    // ĐÃ SỬA: Thêm biến managerUsername để check quyền gán ảnh
    List<CourtImageResponse> uploadMultipleImages(Long courtId, MultipartFile[] files, String managerUsername);

    List<CourtImageResponse> getImagesByCourt(Long courtId);
}