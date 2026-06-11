package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenBlacklistResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.TokenBlacklist;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.repository.TokenBlacklistRepository;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Mặc định tối ưu hóa cho các thao tác đọc dữ liệu
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public Map<String, Object> getBlacklistedTokensPaginated(int page, int size) {
        // Sắp xếp các token mới bị thu hồi lên trên cùng (Dựa vào ID giảm dần)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<TokenBlacklist> tokenPage = tokenBlacklistRepository.findAll(pageable);

        // BẮT BUỘC: Sử dụng Java Stream API chuyển đổi cấu trúc sạch trả về Client
        List<TokenBlacklistResponse> content = tokenPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", content);
        response.put("currentPage", tokenPage.getNumber());
        response.put("totalItems", tokenPage.getTotalElements());
        response.put("totalPages", tokenPage.getTotalPages());

        return response;
    }

    @Override
    @Transactional // Ghi đè cấu hình để thực hiện hành động XÓA (Ghi xuống DB)
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenBlacklistRepository.purgeExpiredTokens(now);
    }

    // Helper đóng gói ánh xạ thực thể
    private TokenBlacklistResponse mapToResponse(TokenBlacklist entity) {
        return TokenBlacklistResponse.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .expiryTime(entity.getExpiryTime())
                .username(entity.getUser() != null ? entity.getUser().getUsername() : "UNKNOWN")
                .build();
    }
}