package com.example.study.domain.user.dto.common;

import com.example.study.domain.user.User;

public record UserInfo(long id, String name) {
    public static UserInfo from(User user) {
        return new UserInfo(user.getId(), user.getName());
    }
}