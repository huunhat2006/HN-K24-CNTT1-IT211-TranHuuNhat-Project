package com.restaurant.hnks24cntt1it211tranhuunhatproject.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourtImageResponse {
    private Long id;
    private String imageUrl;
    private Long courtId;
}