package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TokenBlacklistResponse {
    private Long id;
    private String token; // Chuỗi Token mã hóa
    private LocalDateTime expiryTime; // Thời điểm token tự hết hiệu lực
    private String username; // Người dùng sở hữu token bị thu hồi này
}