package com.restaurant.hnks24cntt1it211tranhuunhatproject.controller.admin;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/blacklisted-tokens")
@RequiredArgsConstructor
public class TokenBlacklistAdminController {

    private final TokenBlacklistService tokenBlacklistService;

    // READ: Lấy danh sách các mã Token bị vô hiệu hóa đang nằm trên RAM Redis để quản trị viên theo dõi
    // GET /api/v1/admin/blacklisted-tokens?page=0&size=10
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBlacklistedTokens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = tokenBlacklistService.getBlacklistedTokensPaginated(page, size);
        return ResponseEntity.ok(response);
    }
}