package com.example.study.domain.user.dto.response;

import java.util.List;

import com.example.study.domain.user.dto.common.UserInfo;

public record GetAllUserResponseDto(
    List<UserInfo> users
) {}