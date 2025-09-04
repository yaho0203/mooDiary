package com.moodiary.repository;

import com.moodiary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

/**
 * 사용자 데이터 접근을 위한 Repository 인터페이스
 *
 * 사용자 엔티티와 관련된 데이터베이스 작업을 담당합니다.
 * JpaRepository를 상속받아 기본적인 CRUD 작업과 함께
 * 사용자 정의 쿼리 메서드를 제공합니다.
 *
 * 주요 기능:
 * - 이메일로 사용자 조회
 * - 이메일 중복 확인
 * - 닉네임 중복 확인
 *
 * @author hyeonSuKim
 * @since 2025-09-03
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     *
     * @param email 조회할 사용자의 이메일
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 존재 여부 확인
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임이 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByNickname(String nickname);
}
