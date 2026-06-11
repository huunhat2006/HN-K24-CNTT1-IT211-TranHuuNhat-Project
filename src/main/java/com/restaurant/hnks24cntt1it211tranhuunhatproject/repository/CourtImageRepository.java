package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.CourtImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourtImageRepository extends JpaRepository<CourtImage, Long> {
    // Tìm toàn bộ danh sách ảnh của 1 sân cầu phục vụ hiển thị bằng Stream API
    List<CourtImage> findByCourtId(Long courtId);
}