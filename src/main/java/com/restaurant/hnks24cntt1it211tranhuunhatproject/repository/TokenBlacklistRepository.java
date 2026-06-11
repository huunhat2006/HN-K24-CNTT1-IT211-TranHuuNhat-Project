package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.TokenBlacklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    // Kiểm tra token có nằm trong blacklist không (Dùng cho JwtRequestFilter)
    boolean existsByToken(String token);

    // Xóa định kỳ toàn bộ các Token đã hết hạn tự nhiên để giải phóng dung lượng DB
    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiryTime <= :now")
    void purgeExpiredTokens(@Param("now") LocalDateTime now);
}