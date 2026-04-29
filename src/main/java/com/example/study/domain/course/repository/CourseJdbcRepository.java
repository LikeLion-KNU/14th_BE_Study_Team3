package com.example.study.domain.course.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.study.domain.course.entity.Course;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CourseJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Course> courseList, int batchSize) {
        String sql = "INSERT INTO course (name, enrolled_count, capacity) " + "VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, 
            courseList, 
            batchSize, 
            (PreparedStatement ps, Course course) -> {
                ps.setString(1, course.getName());
                ps.setInt(2, course.getEnrolledCount());
                ps.setInt(3, course.getCapacity());
            });
    }
}
