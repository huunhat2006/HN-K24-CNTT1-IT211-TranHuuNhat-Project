package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import java.util.Map;

public interface TokenBlacklistService {
    // Thêm một mã Token vào danh sách đen trên RAM kèm thời gian sống tự hủy (TTL)
    void blacklistToken(String token, long expiryDurationMs);

    // Kiểm tra xem Token này có đang bị khóa trên RAM Redis hay không
    boolean isTokenBlacklisted(String token);

    // Xem danh sách mã token bị thu hồi (Phân trang dành cho Admin giám sát hệ thống)
    Map<String, Object> getBlacklistedTokensPaginated(int page, int size);
}