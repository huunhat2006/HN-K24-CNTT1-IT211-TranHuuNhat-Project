package com.restaurant.hnks24cntt1it211tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it211tranhuunhatproject.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
}