package com.restaurant.hnks24cntt1it211tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response.TokenBlacklistResponse;
import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    // Tiêm bộ công cụ thao tác chuỗi của Redis (Nhanh và tối ưu hơn RedisTemplate thông thường)
    private final StringRedisTemplate redisTemplate;

    // Tiền tố (Prefix) để quản lý nhóm key trong Redis, tránh xung đột dữ liệu chéo
    private static final String REDIS_KEY_PREFIX = "JWT_BLACKLIST:";

    @Override
    public void blacklistToken(String token, long expiryDurationMs) {
        String key = REDIS_KEY_PREFIX + token;

        if (expiryDurationMs > 0) {
            // Lưu vào RAM Redis, đặt giá trị là "REVOKED", đặt thời gian sống tự hủy theo đúng mili-giây còn lại của Token
            redisTemplate.opsForValue().set(key, "REVOKED", expiryDurationMs, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = REDIS_KEY_PREFIX + token;
        // Kiểm tra sự tồn tại của Key trên RAM với độ phức tạp lý tưởng O(1)
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    @Override
    public Map<String, Object> getBlacklistedTokensPaginated(int page, int size) {
        // Quét toàn bộ các Key có tiền tố cấu hình trên mảng RAM Redis
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("data", List.of());
            emptyResponse.put("currentPage", page);
            emptyResponse.put("totalItems", 0);
            emptyResponse.put("totalPages", 0);
            return emptyResponse;
        }

        // Xử lý phân trang thủ công trên RAM bằng Stream API (Vì Redis không hỗ trợ Pageable native như SQL)
        List<TokenBlacklistResponse> allTokens = keys.stream()
                .map(key -> {
                    String cleanToken = key.replace(REDIS_KEY_PREFIX, "");
                    Long ttlSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                    LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(ttlSeconds != null && ttlSeconds > 0 ? ttlSeconds : 0);

                    return TokenBlacklistResponse.builder()
                            .token(cleanToken)
                            .expiryTime(expiryTime)
                            .username("LOGOUT_USER") // Redis lưu stateless, không cần ép Join bảng nặng nề như SQL
                            .build();
                })
                .collect(Collectors.toList());

        int totalItems = allTokens.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<TokenBlacklistResponse> paginatedContent = List.of();
        if (fromIndex < totalItems) {
            paginatedContent = allTokens.subList(fromIndex, toIndex);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", paginatedContent);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);

        return response;
    }
}