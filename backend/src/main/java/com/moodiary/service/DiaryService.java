package com.moodiary.service;

import com.moodiary.dto.DiaryDto;
import com.moodiary.entity.DiaryEntry;
import com.moodiary.entity.EmotionType;
import com.moodiary.entity.User;
import com.moodiary.repository.DiaryRepository;
import com.moodiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * 일기 관련 비즈니스 로직 서비스
 * 
 * 일기 CRUD 작업과 OpenAI API를 통한 감정 분석을 담당하는 핵심 서비스 계층입니다.
 * 
 * 주요 기능:
 * - 일기 생성, 조회, 수정, 삭제
 * - OpenAI API를 통한 텍스트 감정 분석
 * - OpenAI API를 통한 이미지 감정 분석
 * - 텍스트와 이미지 통합 감정 분석
 * - 감정 분석 결과 데이터베이스 저장
 * - 감정 분석 결과 조회 및 요약
 * - 사용자별 일기 목록 조회 (페이징 지원)
 * - 특정 날짜 및 감정별 일기 필터링
 * 
      * 감정 분석 특징:
     * - 텍스트 기반 감정 분석 (KoBERT 대신 OpenAI GPT-4 사용)
     * - 이미지 기반 감정 분석 (DeepFace 대신 OpenAI Vision API 사용)
     * - 텍스트와 이미지 통합 감정 분석 (가중 평균)
     * - 감정 점수: 0 (매우 부정적) ~ 100 (매우 긍정적)
     * - 신뢰도 점수: 0 (낮음) ~ 100 (높음)
 * 
 * 트랜잭션 관리:
 * - 읽기 전용 메서드: @Transactional(readOnly = true)
 * - 쓰기 메서드: @Transactional (기본 설정)
 * 
 * @author hyeonSuKim
 * @since 2025-09-03
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    /**
     * 일기 데이터 접근 계층 의존성 주입
     * 
     * 일기 엔티티의 데이터베이스 CRUD 작업을 담당합니다.
     * 
     * 주요 기능:
     * - 일기 저장, 조회, 수정, 삭제
     * - 사용자별 일기 목록 조회
     * - 특정 날짜 및 감정별 일기 필터링
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private final DiaryRepository diaryRepository;

    /**
     * 사용자 데이터 접근 계층 의존성 주입
     * 
     * 사용자 엔티티의 데이터베이스 조회 작업을 담당합니다.
     * 
     * 주요 기능:
     * - 사용자 ID로 사용자 정보 조회
     * - 사용자 존재 여부 검증
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private final UserRepository userRepository;

    /**
     * OpenAI API 서비스 의존성 주입
     * 
     * OpenAI GPT-4 API를 통한 감정 분석을 담당합니다.
     * 
     * 주요 기능:
     * - 텍스트 기반 감정 분석
     * - 이미지 기반 감정 분석
     * - 텍스트와 이미지 통합 감정 분석
     * - 감정 점수 및 신뢰도 계산
     * - 키워드 추출
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private final OpenAiService openAiService;

    /**
     * 일기 작성 및 감정 분석 수행
     * 
     * 사용자가 새로운 일기를 작성할 때 호출되며, OpenAI API를 통한 감정 분석을 수행합니다.
     * 
     * 처리 과정:
     * 1. 사용자 ID 검증 및 사용자 정보 조회
     * 2. OpenAI API를 통한 텍스트 감정 분석 (내용이 있는 경우)
     * 3. OpenAI API를 통한 이미지 감정 분석 (이미지가 있는 경우)
     * 4. 텍스트와 이미지 통합 감정 분석 (둘 다 있는 경우)
     * 5. 감정 분석 결과를 포함한 일기 엔티티 생성
     * 6. 데이터베이스에 일기 저장
     * 7. 응답 DTO로 변환하여 반환
     * 
     * 감정 분석 실패 처리:
     * - OpenAI API 호출 실패 시에도 일기는 정상 저장
     * - 감정 분석 결과는 null로 설정
     * - 로그에 오류 정보 기록
     * 
     * @param userId 일기 작성자 사용자 ID
     * @param request 일기 작성 요청 데이터 (내용, 이미지 URL)
     * @return 생성된 일기 정보와 감정 분석 결과
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @Transactional
    public DiaryDto.DiaryResponse createDiary(Long userId, DiaryDto.CreateDiaryRequest request) {
        log.info("일기 작성 시작 - 사용자 ID: {}, 내용: {}", userId, request.getContent());

        // 사용자 존재 여부 검증 및 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // OpenAI를 통한 감정 분석 수행
        OpenAiService.EmotionAnalysisResult textAnalysis = null;
        OpenAiService.EmotionAnalysisResult imageAnalysis = null;
        OpenAiService.EmotionAnalysisResult integratedAnalysis = null;

        try {
            // 텍스트 감정 분석 (내용이 있는 경우에만 수행)
            if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
                textAnalysis = openAiService.analyzeTextEmotion(request.getContent());
                log.info("텍스트 감정 분석 완료 - 감정: {}, 점수: {}", 
                    textAnalysis.getEmotion(), textAnalysis.getScore());
            }

            // 이미지 감정 분석 (이미지가 있는 경우에만 수행)
            if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
                imageAnalysis = openAiService.analyzeImageEmotion(request.getImageUrl());
                log.info("이미지 감정 분석 완료 - 감정: {}, 점수: {}", 
                    imageAnalysis.getEmotion(), imageAnalysis.getScore());
            }

            // 통합 감정 분석 (텍스트 또는 이미지가 있는 경우)
            if (textAnalysis != null || imageAnalysis != null) {
                if (textAnalysis != null && imageAnalysis != null) {
                    // 텍스트와 이미지 모두 있는 경우에만 통합 분석 수행
                    integratedAnalysis = openAiService.analyzeIntegratedEmotion(
                        request.getContent() != null ? request.getContent() : "",
                        request.getImageUrl() != null ? request.getImageUrl() : ""
                    );
                    log.info("통합 감정 분석 완료 - 감정: {}, 점수: {}", 
                        integratedAnalysis.getEmotion(), integratedAnalysis.getScore());
                } else if (textAnalysis != null) {
                    // 텍스트만 있는 경우 텍스트 분석 결과를 통합 결과로 사용
                    integratedAnalysis = OpenAiService.EmotionAnalysisResult.builder()
                        .emotion(textAnalysis.getEmotion())
                        .score(textAnalysis.getScore())
                        .confidence(textAnalysis.getConfidence())
                        .keywords(textAnalysis.getKeywords())
                        .build();
                    log.info("텍스트 분석 결과를 통합 결과로 사용 - 감정: {}, 점수: {}", 
                        integratedAnalysis.getEmotion(), integratedAnalysis.getScore());
                } else if (imageAnalysis != null) {
                    // 이미지만 있는 경우 이미지 분석 결과를 통합 결과로 사용
                    integratedAnalysis = OpenAiService.EmotionAnalysisResult.builder()
                        .emotion(imageAnalysis.getEmotion())
                        .score(imageAnalysis.getScore())
                        .confidence(imageAnalysis.getConfidence())
                        .keywords(imageAnalysis.getKeywords())
                        .build();
                    log.info("이미지 분석 결과를 통합 결과로 사용 - 감정: {}, 점수: {}", 
                        integratedAnalysis.getEmotion(), integratedAnalysis.getScore());
                }
            }

        } catch (Exception e) {
            log.error("감정 분석 중 오류 발생: {}", e.getMessage(), e);
            // 감정 분석 실패 시에도 일기는 저장
        }

        // 감정 분석 결과를 포함한 일기 엔티티 생성 및 저장
        DiaryEntry diaryEntry = DiaryEntry.builder()
                .user(user)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .textEmotion(textAnalysis != null ? EmotionType.fromString(textAnalysis.getEmotion()) : null)
                .textEmotionScore(textAnalysis != null ? textAnalysis.getScore() : null)
                .textEmotionConfidence(textAnalysis != null ? textAnalysis.getConfidence() : null)
                .facialEmotion(imageAnalysis != null ? EmotionType.fromString(imageAnalysis.getEmotion()) : null)
                .facialEmotionScore(imageAnalysis != null ? imageAnalysis.getScore() : null)
                .facialEmotionConfidence(imageAnalysis != null ? imageAnalysis.getConfidence() : null)
                .integratedEmotion(integratedAnalysis != null ? EmotionType.fromString(integratedAnalysis.getEmotion()) : null)
                .integratedEmotionScore(integratedAnalysis != null ? integratedAnalysis.getScore() : null)
                .integratedEmotionConfidence(integratedAnalysis != null ? integratedAnalysis.getConfidence() : null)
                .keywords(integratedAnalysis != null ? integratedAnalysis.getKeywords() : null)
                .build();

        DiaryEntry savedEntry = diaryRepository.save(diaryEntry);
        log.info("일기 작성 완료 - 일기 ID: {}, 사용자 ID: {}", savedEntry.getId(), userId);

        return convertToResponse(savedEntry);
    }

    /**
     * 일기 수정 및 감정 분석 재수행
     * 
     * 기존 일기의 내용을 수정하고, 변경된 경우에만 감정 분석을 재수행합니다.
     * 
     * 감정 분석 재수행 조건:
     * - 일기 내용이 변경된 경우
     * - 이미지 URL이 변경된 경우
     * - 변경되지 않은 경우 기존 감정 분석 결과 유지
     * 
     * 처리 과정:
     * 1. 일기 존재 여부 및 권한 검증
     * 2. 내용 변경 여부 확인
     * 3. 변경된 경우 OpenAI API를 통한 감정 분석 재수행
     * 4. 감정 분석 결과 업데이트
     * 5. 데이터베이스에 변경사항 저장
     * 6. 응답 DTO로 변환하여 반환
     * 
     * @param userId 일기 수정자 사용자 ID
     * @param diaryId 수정할 일기 ID
     * @param request 일기 수정 요청 데이터
     * @return 수정된 일기 정보와 업데이트된 감정 분석 결과
     * @throws RuntimeException 일기를 찾을 수 없거나 권한이 없는 경우
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @Transactional
    public DiaryDto.DiaryResponse updateDiary(Long userId, Long diaryId, DiaryDto.UpdateDiaryRequest request) {
        log.info("일기 수정 시작 - 사용자 ID: {}, 일기 ID: {}", userId, diaryId);

        // 일기 존재 여부 및 권한 검증
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));

        if (!diaryEntry.getUser().getId().equals(userId)) {
            throw new RuntimeException("일기를 수정할 권한이 없습니다.");
        }

        // 내용이 변경된 경우에만 감정 분석 재수행
        boolean contentChanged = !request.getContent().equals(diaryEntry.getContent());
        boolean imageChanged = (request.getImageUrl() != null && !request.getImageUrl().equals(diaryEntry.getImageUrl())) ||
                              (request.getImageUrl() == null && diaryEntry.getImageUrl() != null);

        if (contentChanged || imageChanged) {
            log.info("내용 또는 이미지가 변경되어 감정 분석을 재수행합니다.");
            
            // OpenAI를 통한 감정 분석 수행
            OpenAiService.EmotionAnalysisResult textAnalysis = null;
            OpenAiService.EmotionAnalysisResult imageAnalysis = null;
            OpenAiService.EmotionAnalysisResult integratedAnalysis = null;

            try {
                // 텍스트 감정 분석 (내용이 있는 경우에만 수행)
                if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
                    textAnalysis = openAiService.analyzeTextEmotion(request.getContent());
                    log.info("텍스트 감정 분석 완료 - 감정: {}, 점수: {}", 
                        textAnalysis.getEmotion(), textAnalysis.getScore());
                }

                // 이미지 감정 분석 (이미지가 있는 경우에만 수행)
                if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
                    imageAnalysis = openAiService.analyzeImageEmotion(request.getImageUrl());
                    log.info("이미지 감정 분석 완료 - 감정: {}, 점수: {}", 
                        imageAnalysis.getEmotion(), imageAnalysis.getScore());
                }

                // 통합 감정 분석 (텍스트 또는 이미지가 있는 경우)
                if (textAnalysis != null || imageAnalysis != null) {
                    if (textAnalysis != null && imageAnalysis != null) {
                        // 텍스트와 이미지 모두 있는 경우에만 통합 분석 수행
                        integratedAnalysis = openAiService.analyzeIntegratedEmotion(
                            request.getContent() != null ? request.getContent() : "",
                            request.getImageUrl() != null ? request.getImageUrl() : ""
                        );
                        log.info("통합 감정 분석 완료 - 감정: {}, 점수: {}", 
                            integratedAnalysis.getEmotion(), integratedAnalysis.getScore());
                    } else if (textAnalysis != null) {
                        // 텍스트만 있는 경우 텍스트 분석 결과를 통합 결과로 사용
                        integratedAnalysis = OpenAiService.EmotionAnalysisResult.builder()
                            .emotion(textAnalysis.getEmotion())
                            .score(textAnalysis.getScore())
                            .confidence(textAnalysis.getConfidence())
                            .keywords(textAnalysis.getKeywords())
                            .build();
                        log.info("텍스트 분석 결과를 통합 결과로 사용 - 감정: {}, 점수: {}", 
                            integratedAnalysis.getEmotion(), integratedAnalysis.getScore());
                    } else if (imageAnalysis != null) {
                        // 이미지만 있는 경우 이미지 분석 결과를 통합 결과로 사용
                        integratedAnalysis = OpenAiService.EmotionAnalysisResult.builder()
                            .emotion(imageAnalysis.getEmotion())
                            .score(imageAnalysis.getScore())
                            .confidence(imageAnalysis.getConfidence())
                            .keywords(imageAnalysis.getKeywords())
                            .build();
                        log.info("이미지 분석 결과를 통합 결과로 사용 - 감정: {}, 점수: {}", 
                            integratedAnalysis.getEmotion(), integratedAnalysis.getScore());
                    }
                }

            } catch (Exception e) {
                log.error("감정 분석 중 오류 발생: {}", e.getMessage(), e);
                // 감정 분석 실패 시에도 일기는 수정
            }

            // 감정 분석 결과 업데이트
            diaryEntry.updateContent(request.getContent());
            diaryEntry.updateImageUrl(request.getImageUrl());
            diaryEntry.updateTextEmotion(textAnalysis != null ? EmotionType.fromString(textAnalysis.getEmotion()) : null);
            diaryEntry.updateTextEmotionScore(textAnalysis != null ? textAnalysis.getScore() : null);
            diaryEntry.updateTextEmotionConfidence(textAnalysis != null ? textAnalysis.getConfidence() : null);
            diaryEntry.updateFacialEmotion(imageAnalysis != null ? EmotionType.fromString(imageAnalysis.getEmotion()) : null);
            diaryEntry.updateFacialEmotionScore(imageAnalysis != null ? imageAnalysis.getScore() : null);
            diaryEntry.updateFacialEmotionConfidence(imageAnalysis != null ? imageAnalysis.getConfidence() : null);
            diaryEntry.updateIntegratedEmotion(integratedAnalysis != null ? EmotionType.fromString(integratedAnalysis.getEmotion()) : null);
            diaryEntry.updateIntegratedEmotionScore(integratedAnalysis != null ? integratedAnalysis.getScore() : null);
            diaryEntry.updateIntegratedEmotionConfidence(integratedAnalysis != null ? integratedAnalysis.getConfidence() : null);
            diaryEntry.updateKeywords(integratedAnalysis != null ? integratedAnalysis.getKeywords() : null);
        } else {
            // 내용이 변경되지 않은 경우 기본 업데이트만 수행
            diaryEntry.updateContent(request.getContent());
            diaryEntry.updateImageUrl(request.getImageUrl());
        }

        DiaryEntry updatedEntry = diaryRepository.save(diaryEntry);
        log.info("일기 수정 완료 - 일기 ID: {}, 사용자 ID: {}", updatedEntry.getId(), userId);

        return convertToResponse(updatedEntry);
    }

    /**
     * 일기 삭제
     * 
     * 사용자가 작성한 일기를 영구적으로 삭제합니다.
     * 
     * 삭제 조건:
     * - 일기 작성자만 삭제 가능
     * - 영구 삭제 (휴지통 없음)
     * 
     * @param userId 일기 삭제자 사용자 ID
     * @param diaryId 삭제할 일기 ID
     * @throws RuntimeException 일기를 찾을 수 없거나 권한이 없는 경우
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        log.info("일기 삭제 시작 - 사용자 ID: {}, 일기 ID: {}", userId, diaryId);

        // 일기 존재 여부 및 권한 검증
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));

        if (!diaryEntry.getUser().getId().equals(userId)) {
            throw new RuntimeException("일기를 삭제할 권한이 없습니다.");
        }

        diaryRepository.delete(diaryEntry);
        log.info("일기 삭제 완료 - 일기 ID: {}, 사용자 ID: {}", diaryId, userId);
    }

    /**
     * 일기 상세 조회
     * 
     * 특정 일기의 상세 정보를 조회합니다.
     * 
     * @param userId 조회 요청자 사용자 ID
     * @param diaryId 조회할 일기 ID
     * @return 일기 상세 정보
     * @throws RuntimeException 일기를 찾을 수 없거나 권한이 없는 경우
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public DiaryDto.DiaryResponse getDiary(Long userId, Long diaryId) {
        log.info("일기 상세 조회 - 사용자 ID: {}, 일기 ID: {}", userId, diaryId);

        // 일기 존재 여부 및 권한 검증
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));

        if (!diaryEntry.getUser().getId().equals(userId)) {
            throw new RuntimeException("일기를 조회할 권한이 없습니다.");
        }

        return convertToResponse(diaryEntry);
    }

    /**
     * 사용자별 일기 목록 조회 (페이징 지원)
     * 
     * 특정 사용자의 일기 목록을 페이징하여 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 일기 목록 (페이징 정보 포함)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public Page<DiaryDto.DiaryResponse> getUserDiaries(Long userId, Pageable pageable) {
        log.info("사용자별 일기 목록 조회 - 사용자 ID: {}, 페이지: {}", userId, pageable.getPageNumber());

        Page<DiaryEntry> diaryEntries = diaryRepository.findByUserId(userId, pageable);
        return diaryEntries.map(this::convertToResponse);
    }

    /**
     * 특정 날짜 일기 조회
     * 
     * 사용자의 특정 날짜에 작성된 일기를 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @param date 조회할 날짜
     * @return 해당 날짜의 일기 (없을 경우 Optional.empty())
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public Optional<DiaryDto.DiaryResponse> getDiaryByDate(Long userId, LocalDate date) {
        log.info("특정 날짜 일기 조회 - 사용자 ID: {}, 날짜: {}", userId, date);

        return diaryRepository.findByUserIdAndCreatedAtBetween(
                userId, 
                date.atStartOfDay(), 
                date.atTime(23, 59, 59)
        ).map(this::convertToResponse);
    }

    /**
     * 감정별 일기 조회
     * 
     * 사용자의 특정 감정 타입별 일기를 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @param emotion 조회할 감정 타입
     * @return 해당 감정의 일기 목록
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public List<DiaryDto.DiaryResponse> getDiariesByEmotion(Long userId, String emotion) {
        log.info("감정별 일기 조회 - 사용자 ID: {}, 감정: {}", userId, emotion);

        List<DiaryEntry> diaryEntries = diaryRepository.findByUserIdAndIntegratedEmotion(userId, emotion);
        return diaryEntries.stream().map(this::convertToResponse).toList();
    }

    /**
     * 일기 감정 분석 결과 조회
     * 
     * 특정 일기의 상세한 감정 분석 결과를 조회합니다.
     * 
     * 제공하는 정보:
     * - 텍스트 감정 분석 결과 (감정, 점수, 신뢰도)
     * - 이미지 감정 분석 결과 (감정, 점수, 신뢰도)
     * - 통합 감정 분석 결과 (감정, 점수, 신뢰도)
     * - 추출된 키워드 목록
     * - OpenAI의 상세 분석 결과
     * 
     * @param userId 조회 요청자 사용자 ID
     * @param diaryId 조회할 일기 ID
     * @return 감정 분석 결과 상세 정보
     * @throws RuntimeException 일기를 찾을 수 없거나 권한이 없는 경우
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public DiaryDto.EmotionAnalysisResponse getDiaryAnalysis(Long userId, Long diaryId) {
        log.info("일기 감정 분석 결과 조회 - 사용자 ID: {}, 일기 ID: {}", userId, diaryId);

        // 일기 존재 여부 및 권한 검증
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));

        if (!diaryEntry.getUser().getId().equals(userId)) {
            throw new RuntimeException("일기를 조회할 권한이 없습니다.");
        }

        // 감정 분석 결과를 DTO로 변환
        DiaryDto.EmotionScoreResponse textEmotion = DiaryDto.EmotionScoreResponse.builder()
                .emotion(diaryEntry.getTextEmotion())
                .score(diaryEntry.getTextEmotionScore())
                .confidence(diaryEntry.getTextEmotionConfidence())
                .build();

        DiaryDto.EmotionScoreResponse facialEmotion = DiaryDto.EmotionScoreResponse.builder()
                .emotion(diaryEntry.getFacialEmotion())
                .score(diaryEntry.getFacialEmotionScore())
                .confidence(diaryEntry.getFacialEmotionConfidence())
                .build();

        DiaryDto.EmotionScoreResponse integratedEmotion = DiaryDto.EmotionScoreResponse.builder()
                .emotion(diaryEntry.getIntegratedEmotion())
                .score(diaryEntry.getIntegratedEmotionScore())
                .confidence(diaryEntry.getIntegratedEmotionConfidence())
                .build();

        // 키워드를 쉼표로 분리하여 리스트로 변환
        List<String> keywords = diaryEntry.getKeywords() != null ? 
                List.of(diaryEntry.getKeywords().split(",")) : new ArrayList<>();

        // 분석 인사이트 생성
        String analysis = generateAnalysisInsight(diaryEntry);

        return DiaryDto.EmotionAnalysisResponse.builder()
                .textEmotion(textEmotion)
                .facialEmotion(facialEmotion)
                .integratedEmotion(integratedEmotion)
                .keywords(keywords)
                .build();
    }

    /**
     * 일기 분석 요약 조회
     * 
     * 특정 일기의 감정 분석 결과를 요약하여 제공합니다.
     * 
     * 요약 정보:
     * - 전체 감정 상태 (매우 긍정적 ~ 매우 부정적)
     * - 전체 감정 점수 (가중 평균)
     * - 주요 감정 (가장 높은 점수)
     * - 상위 키워드 (최대 5개)
     * - 분석 인사이트 (사용자 친화적 설명)
     * 
     * @param userId 조회 요청자 사용자 ID
     * @param diaryId 조회할 일기 ID
     * @return 감정 분석 요약 정보
     * @throws RuntimeException 일기를 찾을 수 없거나 권한이 없는 경우
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public DiaryDto.AnalysisSummaryResponse getDiaryAnalysisSummary(Long userId, Long diaryId) {
        log.info("일기 분석 요약 조회 - 사용자 ID: {}, 일기 ID: {}", userId, diaryId);

        // 일기 존재 여부 및 권한 검증
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));

        if (!diaryEntry.getUser().getId().equals(userId)) {
            throw new RuntimeException("일기를 조회할 권한이 없습니다.");
        }

        // 전체 감정 점수 계산 및 요약 정보 생성
        Double overallEmotionScore = calculateOverallEmotionScore(diaryEntry);
        String overallEmotion = determineOverallEmotion(overallEmotionScore);
        String dominantEmotion = determineDominantEmotion(diaryEntry);
        List<String> topKeywords = extractTopKeywords(diaryEntry);
        String analysisInsight = generateAnalysisInsight(diaryEntry);

        return DiaryDto.AnalysisSummaryResponse.builder()
                .diaryId(diaryEntry.getId())
                .content(diaryEntry.getContent())
                .overallEmotion(EmotionType.fromString(overallEmotion))
                .overallEmotionScore(overallEmotionScore)
                .dominantEmotion(dominantEmotion)
                .topKeywords(topKeywords)
                .analysisInsight(analysisInsight)
                .createdAt(diaryEntry.getCreatedAt())
                .updatedAt(diaryEntry.getUpdatedAt())
                .build();
    }

    /**
     * 전체 감정 점수 계산
     * 
     * 텍스트 감정과 표정 감정의 가중 평균을 계산합니다.
     * 
     * 가중치:
     * - 텍스트 감정: 70% (더 정확한 감정 표현)
     * - 표정 감정: 30% (보조적 감정 정보)
     * 
     * @param diaryEntry 계산할 일기 엔티티
     * @return 전체 감정 점수 (0.0 ~ 1.0)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private Double calculateOverallEmotionScore(DiaryEntry diaryEntry) {
        // 텍스트 감정과 표정 감정의 가중 평균 계산
        Double textScore = diaryEntry.getTextEmotionScore() != null ? diaryEntry.getTextEmotionScore() : 0.0;
        Double facialScore = diaryEntry.getFacialEmotionScore() != null ? diaryEntry.getFacialEmotionScore() : 0.0;
        
        // 텍스트에 더 높은 가중치 부여 (0.7 : 0.3)
        return (textScore * 0.7) + (facialScore * 0.3);
    }

    /**
     * 전체 감정 상태 결정
     * 
     * 감정 점수를 기반으로 전체 감정 상태를 결정합니다.
     * 
     * 점수 기준:
     * - 0~20: 매우 부정적
     * - 20~40: 부정적
     * - 40~60: 중립
     * - 60~80: 긍정적
     * - 80~100: 매우 긍정적
     * 
     * @param score 감정 점수 (0 ~ 100)
     * @return 전체 감정 상태 문자열
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private String determineOverallEmotion(Double score) {
        if (score == null || score == 0.0) return "분석 중";
        if (score >= 80) return "매우 긍정적";
        if (score >= 60) return "긍정적";
        if (score >= 40) return "중립";
        if (score >= 20) return "부정적";
        return "매우 부정적";
    }

    /**
     * 주요 감정 결정
     * 
     * 가장 높은 점수를 가진 감정을 우선순위로 결정합니다.
     * 
     * 우선순위:
     * 1. 텍스트 감정 (가장 정확)
     * 2. 표정 감정 (보조적)
     * 3. 통합 감정 (종합적)
     * 
     * @param diaryEntry 분석할 일기 엔티티
     * @return 주요 감정 문자열
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private String determineDominantEmotion(DiaryEntry diaryEntry) {
        // 가장 높은 점수를 가진 감정을 우선순위로 결정
        if (diaryEntry.getTextEmotion() != null) return diaryEntry.getTextEmotion().name();
        if (diaryEntry.getFacialEmotion() != null) return diaryEntry.getFacialEmotion().name();
        if (diaryEntry.getIntegratedEmotion() != null) return diaryEntry.getIntegratedEmotion().name();
        return "분석 중";
    }

    /**
     * 상위 키워드 추출
     * 
     * 일기에서 추출된 키워드를 쉼표로 분리하고 상위 5개만 반환합니다.
     * 
     * @param diaryEntry 분석할 일기 엔티티
     * @return 상위 키워드 목록 (최대 5개)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private List<String> extractTopKeywords(DiaryEntry diaryEntry) {
        if (diaryEntry.getKeywords() == null || diaryEntry.getKeywords().isEmpty()) {
            return new ArrayList<>();
        }
        // 키워드를 쉼표로 분리하고 상위 5개만 반환
        return List.of(diaryEntry.getKeywords().split(","))
                .stream()
                .limit(5)
                .toList();
    }

    /**
     * 분석 인사이트 생성
     * 
     * 감정 분석 결과를 사용자 친화적인 형태로 변환합니다.
     * 
     * 포함 정보:
     * - 텍스트 분석 결과 (감정, 신뢰도)
     * - 표정 분석 결과 (감정, 신뢰도)
     * - 통합 분석 결과 (감정, 신뢰도)
     * 
     * @param diaryEntry 분석할 일기 엔티티
     * @return 사용자 친화적인 분석 인사이트 문자열
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private String generateAnalysisInsight(DiaryEntry diaryEntry) {
        StringBuilder insight = new StringBuilder();
        
        // 텍스트 분석 결과 추가
        if (diaryEntry.getTextEmotion() != null) {
            insight.append("텍스트 분석: ").append(diaryEntry.getTextEmotion());
            if (diaryEntry.getTextEmotionConfidence() != null) {
                insight.append(" (신뢰도: ").append(String.format("%.1f%%", diaryEntry.getTextEmotionConfidence() * 100)).append(")");
            }
            insight.append("\n");
        }
        
        // 표정 분석 결과 추가
        if (diaryEntry.getFacialEmotion() != null) {
            insight.append("표정 분석: ").append(diaryEntry.getFacialEmotion());
            if (diaryEntry.getFacialEmotionConfidence() != null) {
                insight.append(" (신뢰도: ").append(String.format("%.1f%%", diaryEntry.getFacialEmotionConfidence() * 100)).append(")");
            }
            insight.append("\n");
        }
        
        // 통합 분석 결과 추가
        if (diaryEntry.getIntegratedEmotion() != null) {
            insight.append("통합 분석: ").append(diaryEntry.getIntegratedEmotion());
            if (diaryEntry.getIntegratedEmotionConfidence() != null) {
                insight.append(" (신뢰도: ").append(String.format("%.1f%%", diaryEntry.getIntegratedEmotionConfidence() * 100)).append(")");
            }
        }
        
        return insight.toString().trim();
    }

    /**
     * 일기 엔티티를 응답 DTO로 변환
     * 
     * JPA 엔티티를 API 응답용 DTO로 변환합니다.
     * 
     * 변환 내용:
     * - 기본 일기 정보 (ID, 사용자 ID, 내용, 이미지 URL)
     * - 감정 분석 결과 (텍스트, 표정, 통합)
     * - 메타데이터 (생성 시간, 수정 시간)
     * - LocalDateTime을 String으로 변환 (JSON 직렬화 호환성)
     * 
     * @param diaryEntry 변환할 일기 엔티티
     * @return 일기 응답 DTO
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private DiaryDto.DiaryResponse convertToResponse(DiaryEntry diaryEntry) {
        return DiaryDto.DiaryResponse.builder()
                .id(diaryEntry.getId())
                .userId(diaryEntry.getUser().getId())
                .content(diaryEntry.getContent())
                .imageUrl(diaryEntry.getImageUrl())
                .emotionAnalysis(createEmotionAnalysisResponse(diaryEntry))
                .createdAt(diaryEntry.getCreatedAt())
                .updatedAt(diaryEntry.getUpdatedAt())
                .build();
    }
    
    /**
     * DiaryEntry에서 EmotionAnalysisResponse 생성
     */
    private DiaryDto.EmotionAnalysisResponse createEmotionAnalysisResponse(DiaryEntry diaryEntry) {
        DiaryDto.EmotionScoreResponse textEmotion = null;
        DiaryDto.EmotionScoreResponse facialEmotion = null;
        DiaryDto.EmotionScoreResponse integratedEmotion = null;
        
        if (diaryEntry.getTextEmotion() != null) {
            textEmotion = DiaryDto.EmotionScoreResponse.builder()
                    .emotion(diaryEntry.getTextEmotion())
                    .score(diaryEntry.getTextEmotionScore())
                    .confidence(diaryEntry.getTextEmotionConfidence())
                    .build();
        }
        
        if (diaryEntry.getFacialEmotion() != null) {
            facialEmotion = DiaryDto.EmotionScoreResponse.builder()
                    .emotion(diaryEntry.getFacialEmotion())
                    .score(diaryEntry.getFacialEmotionScore())
                    .confidence(diaryEntry.getFacialEmotionConfidence())
                    .build();
        }
        
        if (diaryEntry.getIntegratedEmotion() != null) {
            integratedEmotion = DiaryDto.EmotionScoreResponse.builder()
                    .emotion(diaryEntry.getIntegratedEmotion())
                    .score(diaryEntry.getIntegratedEmotionScore())
                    .confidence(diaryEntry.getIntegratedEmotionConfidence())
                    .build();
        }
        
        List<String> keywords = new ArrayList<>();
        if (diaryEntry.getKeywords() != null && !diaryEntry.getKeywords().trim().isEmpty()) {
            // JSON 파싱 로직 (간단한 구현)
            String[] keywordArray = diaryEntry.getKeywords().replace("[", "").replace("]", "").replace("\"", "").split(",");
            for (String keyword : keywordArray) {
                if (!keyword.trim().isEmpty()) {
                    keywords.add(keyword.trim());
                }
            }
        }
        
        return DiaryDto.EmotionAnalysisResponse.builder()
                .textEmotion(textEmotion)
                .facialEmotion(facialEmotion)
                .integratedEmotion(integratedEmotion)
                .keywords(keywords)
                .timestamp(diaryEntry.getUpdatedAt())
                .build();
    }
}
