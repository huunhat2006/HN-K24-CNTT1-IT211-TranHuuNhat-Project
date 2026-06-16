package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.CourtImageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CourtManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourtImageService courtImageService;

    @Test
    void testUploadCourtImages_Success() throws Exception {
        // Giả lập một file ảnh thô gửi lên
        MockMultipartFile fakeFile = new MockMultipartFile(
                "files", "san_cau_long.jpg", "image/jpeg", "data_hinh_anh_binary".getBytes());

        Mockito.when(courtImageService.uploadMultipleImages(anyLong(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/manager/courts/1/images")
                        .file(fakeFile)
                        .principal(() -> "manager1"))
                .andExpect(status().isCreated()); // Trả về 201 Created
    }
}