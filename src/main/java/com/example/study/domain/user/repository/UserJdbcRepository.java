package com.example.study.domain.user.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.study.domain.user.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<User> userList, int batchSize) {
        String sql = "INSERT INTO user (name) " + "VALUES (?)";

        jdbcTemplate.batchUpdate(sql, 
            userList, 
            batchSize,
            (PreparedStatement ps, User user) -> {
                ps.setString(1, user.getName());
            });
    }
}