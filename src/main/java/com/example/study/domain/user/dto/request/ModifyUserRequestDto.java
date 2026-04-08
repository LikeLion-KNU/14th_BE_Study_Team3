package com.example.study.domain.user.dto.request;

import jakarta.validation.constraints.Size;

public record ModifyUserRequestDto(
   @Size(min=1, max=12)
   String changedName
) {}
