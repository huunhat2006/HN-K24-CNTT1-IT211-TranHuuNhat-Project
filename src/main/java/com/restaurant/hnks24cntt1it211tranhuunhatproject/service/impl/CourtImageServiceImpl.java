package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
    private final Cloudinary cloudinary; // Tiêm Bean Cloudinary đã cấu hình vào đây

    @Override
    @Transactional
    public List<CourtImageResponse> uploadMultipleImages(Long courtId, MultipartFile[] files) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Sân cầu lông không tồn tại để gán ảnh!"));

        if (files == null || files.length == 0) {
            throw new RuntimeException("Danh sách tệp hình ảnh trống!");
        }

        // BẮT BUỘC: Sử dụng Stream API duyệt mảng file, đẩy trực tiếp lên Cloudinary
        List<CourtImage> imagesToSave = Arrays.stream(files)
                .map(file -> {
                    try {
                        // Thực hiện đẩy file ảnh dạng bytes lên thư mục tự động đặt tên "badminton_courts" trên mây
                        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                                file.getBytes(),
                                ObjectUtils.asMap("folder", "badminton_courts")
                        );

                        // Trích xuất đường dẫn URL bảo mật dạng HTTPS trả về từ Cloudinary
                        String secureUrl = uploadResult.get("secure_url").toString();

                        return CourtImage.builder()
                                .imageUrl(secureUrl) // Lưu URL mây thực tế xuống MySQL
                                .court(court)
                                .build();
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi hệ thống khi tải ảnh lên Cloudinary: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());

        // Lưu toàn bộ danh sách thực thể xuống Database
        List<CourtImage> savedImages = courtImageRepository.saveAll(imagesToSave);

        // Map cấu trúc dữ liệu sạch trả về phía Client
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