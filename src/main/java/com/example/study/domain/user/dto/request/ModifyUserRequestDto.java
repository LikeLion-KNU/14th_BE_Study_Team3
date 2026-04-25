package com.example.study.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifyUserRequestDto(
   @NotBlank(message = "이름은 최소 1글자입니다.")
   @Size(min=1, max=12, message = "이름은 최소 1글자, 최대 12글자입니다.")
   String changedName
) {}
