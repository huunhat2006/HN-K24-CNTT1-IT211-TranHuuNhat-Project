package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.CourtImageResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CourtImageService {
    // Lưu danh sách nhiều file ảnh tải lên gán cho 1 sân cầu
    List<CourtImageResponse> uploadMultipleImages(Long courtId, MultipartFile[] files);
    
    // Lấy danh sách ảnh của sân cầu
    List<CourtImageResponse> getImagesByCourt(Long courtId);
}