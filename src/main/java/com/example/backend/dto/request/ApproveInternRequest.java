package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public class ApproveInternRequest {
    @NotNull
    private Boolean approved; //true = duyệt, false = từ chối

    private String reason; // lý do từ chối hoặc ghi chú duyệt
}
