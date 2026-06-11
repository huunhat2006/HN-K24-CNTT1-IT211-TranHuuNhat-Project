package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token);
}