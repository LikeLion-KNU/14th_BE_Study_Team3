package com.example.study.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.study.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {}
