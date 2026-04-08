package com.example.study.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(
    @NotNull(message = "이름은 필수입니다.")
    @Size(min=1, max=12)
    String name
) {}
