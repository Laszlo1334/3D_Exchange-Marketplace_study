package com.education.marketplace_3D.dto;

import java.util.UUID;

public record UserSummaryDto(
        UUID id,
        String displayName
) {
}
