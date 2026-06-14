package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.CourtImageResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.CourtImage;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.BadRequestException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.exception.ResourceNotFoundException;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtImageRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.CourtImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtImageServiceImpl implements CourtImageService {

    private final CourtImageRepository courtImageRepository;
    private final CourtRepository courtRepository;
    private final Cloudinary cloudinary;

    @Override
    @Transactional
    // ĐÃ GIA CỐ KIỂM TRA SỞ HỮU SÂN CẦU LÔNG
    public List<CourtImageResponse> uploadMultipleImages(Long courtId, MultipartFile[] files, String managerUsername) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new ResourceNotFoundException("Tải ảnh thất bại: Sân cầu lông mục tiêu không tồn tại!"));

        // KIỂM TRA: Sân này có phải do Manager đang gọi API làm chủ cụm không?
        String ownerUsername = court.getCluster().getManager().getUsername();
        if (!ownerUsername.equalsIgnoreCase(managerUsername)) {
            throw new BadRequestException("Vi phạm bảo mật: Bạn không được quyền upload hình ảnh lên sân của chủ khác!");
        }

        if (files == null || files.length == 0) {
            throw new BadRequestException("Yêu cầu không thể thực hiện do danh sách tệp hình ảnh trống!");
        }

        List<CourtImage> imagesToSave = Arrays.stream(files)
                .map(file -> {
                    try {
                        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                ObjectUtils.asMap("folder", "badminton_courts")
                        );

                        String secureUrl = uploadResult.get("secure_url").toString();

                        return CourtImage.builder()
                                .imageUrl(secureUrl)
                                .court(court)
                                .build();
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi kết nối hệ thống khi đẩy tệp lên đám mây Cloudinary: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());

        List<CourtImage> savedImages = courtImageRepository.saveAll(imagesToSave);

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