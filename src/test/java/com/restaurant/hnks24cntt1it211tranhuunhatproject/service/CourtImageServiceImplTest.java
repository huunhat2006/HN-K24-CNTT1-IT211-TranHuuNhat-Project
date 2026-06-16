package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.CourtImageResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.BadmintonCluster; // IMPORT ĐÚNG CLASS CỤM SÂN
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtImageRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.CourtRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl.CourtImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CourtImageServiceImplTest {

    @Mock
    private CourtImageRepository courtImageRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private Cloudinary cloudinary;

    @InjectMocks
    private CourtImageServiceImpl courtImageService;

    @Test
    void testUploadMultipleImages_Success() throws IOException {
        // 1. Arrange: Sử dụng @Builder để dựng cây dữ liệu Entity THẬT lồng nhau (manager -> cluster -> court)
        User fakeManager = User.builder()
                .username("manager1")
                .role("MANAGER")
                .build();

        BadmintonCluster fakeCluster = BadmintonCluster.builder()
                .manager(fakeManager)
                .build();

        Court fakeCourt = Court.builder()
                .id(1L)
                .courtName("Sân số 1")
                .cluster(fakeCluster) // Gắn cụm sân vào
                .build();

        // Chỉ mock tầng gác cổng Repository trả về đối tượng Court thật ở trên
        Mockito.when(courtRepository.findById(1L)).thenReturn(Optional.of(fakeCourt));

        // Mock bộ bộ xử lý upload dữ liệu của bên thứ 3 Cloudinary
        Uploader mockUploader = Mockito.mock(Uploader.class);
        Map<String, Object> fakeCloudinaryResult = new HashMap<>();
        fakeCloudinaryResult.put("secure_url", "https://res.cloudinary.com/test_image.jpg");

        Mockito.when(cloudinary.uploader()).thenReturn(mockUploader);
        Mockito.when(mockUploader.upload(any(byte[].class), any(Map.class))).thenReturn(fakeCloudinaryResult);

        // Giả lập mảng chứa 1 file ảnh nhị phân đẩy lên từ Postman/Client
        MultipartFile fakeMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(fakeMultipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        MultipartFile[] files = new MultipartFile[]{fakeMultipartFile};

        Mockito.when(courtImageRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act: Thực thi hàm nghiệp vụ upload ảnh lên Cloudinary đám mây
        List<CourtImageResponse> result = courtImageService.uploadMultipleImages(1L, files, "manager1");

        // 3. Assert: Đối chiếu dữ liệu đầu ra xem có đúng kỳ vọng không
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://res.cloudinary.com/test_image.jpg", result.get(0).getImageUrl());
    }
}