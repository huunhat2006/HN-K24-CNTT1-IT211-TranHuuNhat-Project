package com.restaurant.hnks24cntt1it211tranhuunhatproject.service;

import java.util.Map;

public interface TokenBlacklistService {
    // Xem danh sách mã token bị thu hồi (Phân trang dành cho Admin giám sát hệ thống)
    Map<String, Object> getBlacklistedTokensPaginated(int page, int size);
    
    // Kích hoạt dọn dẹp thủ công các token đã hết hạn
    void purgeExpiredTokens();
}