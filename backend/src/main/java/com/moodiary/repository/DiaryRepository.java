package com.moodiary.repository;

import com.moodiary.entity.DiaryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 일기 데이터 접근 계층 (Repository)
 * 
 * 일기 엔티티의 데이터베이스 CRUD 작업을 담당하는 인터페이스입니다.
 * Spring Data JPA를 사용하여 기본적인 CRUD 작업을 자동으로 제공하며,
 * 추가적인 쿼리 메서드들을 정의하여 비즈니스 요구사항을 충족합니다.
 * 
 * 주요 기능:
 * - 기본 CRUD 작업 (생성, 조회, 수정, 삭제)
 * - 사용자별 일기 목록 조회 (페이징 지원)
 * - 특정 날짜 범위 일기 조회
 * - 감정별 일기 필터링
 * - 커스텀 JPQL 쿼리 지원
 * 
 * 상속받는 기능:
 * - JpaRepository<DiaryEntry, Long>: 기본 CRUD 메서드들
 * - save(), findById(), delete() 등
 * 
 * 메서드 명명 규칙:
 * - findBy{필드명}: 단일 필드로 조회
 * - findBy{필드명}And{필드명}: 복합 조건 조회
 * - findBy{필드명}Between: 범위 조건 조회
 * 
 * @author hyeonSuKim
 * @since 2025-09-03
 * @version 1.0
 */
@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {

    /**
     * 사용자 ID로 일기 목록을 페이징하여 조회
     * 
     * 특정 사용자가 작성한 일기 목록을 페이징하여 조회합니다.
     * Spring Data JPA의 메서드 명명 규칙을 사용하여 자동으로 쿼리를 생성합니다.
     * 
     * 생성되는 SQL:
     * ```sql
     * SELECT * FROM diary_entries 
     * WHERE user_id = ? 
     * ORDER BY created_at DESC
     * LIMIT ? OFFSET ?
     * ```
     * 
     * @param userId 조회할 사용자 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 일기 목록 (페이징 정보 포함)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    Page<DiaryEntry> findByUserId(Long userId, Pageable pageable);

    /**
     * 사용자 ID와 특정 날짜 범위로 일기 조회
     * 
     * 특정 사용자의 특정 날짜 범위에 작성된 일기를 조회합니다.
     * JPQL 쿼리를 사용하여 복잡한 날짜 범위 조건을 처리합니다.
     * 
     * 생성되는 SQL:
     * ```sql
     * SELECT * FROM diary_entries 
     * WHERE user_id = ? 
     * AND created_at BETWEEN ? AND ?
     * ```
     * 
     * 날짜 범위:
     * - startDate: 해당 날짜의 00:00:00
     * - endDate: 해당 날짜의 23:59:59
     * 
     * @param userId 조회할 사용자 ID
     * @param startDate 시작 날짜 및 시간
     * @param endDate 종료 날짜 및 시간
     * @return 해당 날짜 범위의 일기 (Optional, 최대 1개)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @Query("SELECT d FROM DiaryEntry d WHERE d.user.id = :userId AND d.createdAt BETWEEN :startDate AND :endDate")
    Optional<DiaryEntry> findByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 사용자 ID와 통합 감정으로 일기 목록 조회
     * 
     * 특정 사용자의 특정 통합 감정을 가진 일기들을 조회합니다.
     * Spring Data JPA의 메서드 명명 규칙을 사용합니다.
     * 
     * 생성되는 SQL:
     * ```sql
     * SELECT * FROM diary_entries 
     * WHERE user_id = ? AND integrated_emotion = ?
     * ```
     * 
     * @param userId 조회할 사용자 ID
     * @param emotion 조회할 감정 타입
     * @return 해당 감정의 일기 목록
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    List<DiaryEntry> findByUserIdAndIntegratedEmotion(Long userId, String emotion);

    /**
     * 사용자 ID와 텍스트 감정으로 일기 목록 조회
     * 
     * 특정 사용자의 특정 텍스트 감정을 가진 일기들을 조회합니다.
     * Spring Data JPA의 메서드 명명 규칙을 사용합니다.
     * 
     * 생성되는 SQL:
     * ```sql
     * SELECT * FROM diary_entries 
     * WHERE user_id = ? AND text_emotion = ?
     * ```
     * 
     * @param userId 조회할 사용자 ID
     * @param emotion 조회할 텍스트 감정 타입
     * @return 해당 텍스트 감정의 일기 목록
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    List<DiaryEntry> findByUserIdAndTextEmotion(Long userId, String emotion);

    /**
     * 사용자 ID와 표정 감정으로 일기 목록 조회
     * 
     * 특정 사용자의 특정 표정 감정을 가진 일기들을 조회합니다.
     * Spring Data JPA의 메서드 명명 규칙을 사용합니다.
     * 
     * 생성되는 SQL:
     * ```sql
     * SELECT * FROM diary_entries 
     * WHERE user_id = ? AND facial_emotion = ?
     * ```
     * 
     * @param userId 조회할 사용자 ID
     * @param emotion 조회할 표정 감정 타입
     * @return 해당 표정 감정의 일기 목록
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    List<DiaryEntry> findByUserIdAndFacialEmotion(Long userId, String emotion);
}
