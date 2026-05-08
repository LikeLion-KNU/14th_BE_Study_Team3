package com.example.study.domain.user.dto.response;

import com.example.study.domain.user.dto.common.UserInfo;

public record GetUserResponseDto(
    UserInfo user
) {}
