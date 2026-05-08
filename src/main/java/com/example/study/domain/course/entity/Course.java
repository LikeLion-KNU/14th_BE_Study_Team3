package com.example.study.domain.course.entity;

import com.example.study.domain.course.exception.CourseErrorCode;
import com.example.study.domain.user.User;
import com.example.study.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(name = "enrolled_count", nullable = false)
    private Integer enrolledCount;

    @Column(nullable = false)
    private Integer capacity;

    @Builder
    public Course(String name, Integer enrolledCount, Integer capacity) {
        if (capacity == null || capacity <= 0)
            throw new BusinessException(CourseErrorCode.COURSE_INVALID_CAPACITY);

        if (enrolledCount == null || enrolledCount < 0)
            throw new BusinessException(CourseErrorCode.COURSE_ENROLLMENT_NEGATIVE);

        if (enrolledCount > capacity)
            throw new BusinessException(CourseErrorCode.COURSE_CAPACITY_EXCEEDED);

        this.name = name;
        this.enrolledCount = enrolledCount;
        this.capacity = capacity;
    }

    public void increaseEnrolledCount() {
        if (this.enrolledCount >= this.capacity)
            throw new BusinessException(CourseErrorCode.COURSE_CAPACITY_EXCEEDED);

        this.enrolledCount++;
    }

    public void decreaseEnrolledCount() {
        if (this.enrolledCount <= 0)
            throw new BusinessException(CourseErrorCode.COURSE_ENROLLMENT_NEGATIVE);

        this.enrolledCount--;
    }

    public void updateCourse(String name, Integer capacity) {
        if (name != null)
            this.name = name;

        int nextCapacity = (capacity != null) ? capacity : this.capacity;

        validateCapacity(this.enrolledCount, nextCapacity);

        this.capacity = nextCapacity;
    }

    private void validateCapacity(int enrolledCount, int capacity) {
        if (capacity <= 0)
            throw new BusinessException(CourseErrorCode.COURSE_INVALID_CAPACITY);

        if (enrolledCount < 0)
            throw new BusinessException(CourseErrorCode.COURSE_ENROLLMENT_NEGATIVE);

        if (enrolledCount > capacity)
            throw new BusinessException(CourseErrorCode.COURSE_CAPACITY_EXCEEDED);
    }
}
