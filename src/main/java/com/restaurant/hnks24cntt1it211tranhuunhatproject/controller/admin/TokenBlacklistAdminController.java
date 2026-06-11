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

    // 1. READ: Lấy danh sách Token bị khóa có phân trang để quản trị viên giám sát
    // GET /api/v1/admin/blacklisted-tokens?page=0&size=10
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBlacklistedTokens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = tokenBlacklistService.getBlacklistedTokensPaginated(page, size);
        return ResponseEntity.ok(response);
    }

    // 2. DELETE: Kích hoạt dọn dẹp các token đã quá hạn tự nhiên ra khỏi bộ nhớ
    // DELETE /api/v1/admin/blacklisted-tokens/purge
    @DeleteMapping("/purge")
    public ResponseEntity<String> purgeExpiredTokens() {
        tokenBlacklistService.purgeExpiredTokens();
        return ResponseEntity.ok("Đã dọn dẹp toàn bộ các mã Token hết hạn thành công!");
    }
}