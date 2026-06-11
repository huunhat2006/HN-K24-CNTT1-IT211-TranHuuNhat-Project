package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.CourtImageResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.CourtImage;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtImageRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.CourtImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtImageServiceImpl implements CourtImageService {

    private final CourtImageRepository courtImageRepository;
    private final CourtRepository courtRepository;

    @Override
    @Transactional
    public List<CourtImageResponse> uploadMultipleImages(Long courtId, MultipartFile[] files) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Sân cầu lông không tồn tại để gán ảnh!"));

        if (files == null || files.length == 0) {
            throw new RuntimeException("Danh sách tệp hình ảnh trống!");
        }

        // Dùng Stream API duyệt danh sách file upload, giả lập lưu trữ và map sang Entity
        List<CourtImage> imagesToSave = Arrays.stream(files)
                .map(file -> {
                    // Tạo tên ảnh ngẫu nhiên chống trùng tệp hệ thống: UUID_filename
                    String fakeStoredUrl = "/uploads/courts/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
                    
                    return CourtImage.builder()
                            .imageUrl(fakeStoredUrl)
                            .court(court)
                            .build();
                })
                .collect(Collectors.toList());

        List<CourtImage> savedImages = courtImageRepository.saveAll(imagesToSave);

        // Chuyển đổi dữ liệu sạch trả về bằng Stream API
        return savedImages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourtImageResponse> getImagesByCourt(Long courtId) {
        return courtImageRepository.findByCourtId(courtId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CourtImageResponse mapToResponse(CourtImage image) {
        return CourtImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .courtId(image.getCourt().getId())
                .build();
    }
}