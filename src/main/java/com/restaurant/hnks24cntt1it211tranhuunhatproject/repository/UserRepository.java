package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Tìm kiếm phân trang cho Admin quản lý người dùng
    Page<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(String username, String fullName, Pageable pageable);
}