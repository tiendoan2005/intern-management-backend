package com.example.backend.dto.request;

import com.example.backend.enums.ReviewDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest (
        @NotNull ReviewDecision decision,
        @Size(max = 2000) String comment
        ) {
}
