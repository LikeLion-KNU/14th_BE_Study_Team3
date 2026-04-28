/**
 * 대용량 더미 데이터 생성을 위한 스토어드 프로시저 스크립트
 * 사용법:
 * 1. 본 스크립트 실행하여 프로시저 등록
 * 2. CALL InsertDummyUsers(); 실행
 * 3. CALL InsertDummyCourses(); 실행
 */

-- [1] 유저 더미 데이터 생성 프로시저 (30,000건)
DELIMITER $$

CREATE PROCEDURE InsertDummyUsers()
BEGIN
    DECLARE i INT DEFAULT 1;
    SET autocommit = 0;

    WHILE i <= 30000 DO
        INSERT INTO user (name)
        VALUES (CONCAT('User_', i));
        SET i = i + 1;
END WHILE;

COMMIT;
SET autocommit = 1;
END$$

DELIMITER ;


-- [2] 강의 더미 데이터 생성 프로시저 (5,000건)
DELIMITER $$

CREATE PROCEDURE InsertDummyCourses()
BEGIN
    DECLARE i INT DEFAULT 1;
    SET autocommit = 0;

    WHILE i <= 5000 DO
        INSERT INTO course (capacity, enrolled_count, name)
        VALUES (
            (FLOOR(RAND() * 5) + 2) * 10, -- {20, 30, 40, 50, 60} 중 랜덤
            0,                             -- 수강신청 인원 초기값
            CONCAT('강의_', i)             -- 강의명
        );
        SET i = i + 1;
END WHILE;

COMMIT;
SET autocommit = 1;
END$$

DELIMITER ;