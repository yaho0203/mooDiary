package com.moodiary.controller;

import com.moodiary.dto.DiaryDto;
import com.moodiary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 일기 관련 API 컨트롤러
 * 
 * 일기 작성, 조회, 수정, 삭제 및 감정 분석 결과 조회를 담당하는 REST API 엔드포인트들을 제공합니다.
 * 
 * 주요 기능:
 * - 일기 CRUD 작업 (생성, 조회, 수정, 삭제)
 * - 감정 분석 결과 조회
 * - 일기 분석 요약 조회
 * - 사용자별 일기 목록 조회 (페이징 지원)
 * - 특정 날짜 일기 조회
 * - 감정별 일기 필터링
 * 
 * API 특징:
 * - RESTful 설계 원칙 준수
 * - Swagger/OpenAPI 문서화 지원
 * - CORS 설정으로 프론트엔드 연동 지원
 * - Spring Security를 통한 인증/인가 (현재는 permitAll)
 * - 로깅을 통한 요청/응답 추적
 * 
 * 사용자 인증:
 * - 현재는 모든 API가 permitAll() 설정
 * - userId를 쿼리 파라미터로 전달하여 사용자 식별
 * - 향후 JWT 토큰 기반 인증으로 확장 예정
 * 
 * @author hyeonSuKim
 * @since 2025-09-21
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "일기 API", description = "일기 작성, 조회, 수정, 삭제 API")
public class DiaryController {
    
    /**
     * 일기 서비스 의존성 주입
     * 
     * 일기 관련 비즈니스 로직을 처리하는 서비스 계층과 연동합니다.
     * 
     * 주요 책임:
     * - 일기 CRUD 작업 처리
     * - OpenAI API를 통한 감정 분석 수행
     * - 감정 분석 결과 처리 및 저장
     * - 데이터 변환 및 응답 구성
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    private final DiaryService diaryService;
    
    /**
     * 일기 작성 API
     * 
     * 사용자가 새로운 일기를 작성하고 OpenAI API를 통한 감정 분석을 수행합니다.
     * 
     * 처리 과정:
     * 1. 사용자 ID 검증
     * 2. OpenAI API를 통한 텍스트 감정 분석
     * 3. 이미지가 있는 경우 이미지 감정 분석
     * 4. 텍스트와 이미지 통합 감정 분석
     * 5. 일기 데이터베이스 저장
     * 6. 감정 분석 결과와 함께 응답 반환
     * 
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param request 일기 작성 요청 데이터 (내용, 이미지 URL)
     * @return 생성된 일기 정보와 감정 분석 결과
     * 
     * HTTP 상태 코드:
     * - 201: 일기 생성 성공
     * - 400: 잘못된 요청 (사용자 없음, 필수 필드 누락)
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @PostMapping
    @Operation(summary = "일기 작성", description = "새로운 일기를 작성합니다.")
    public ResponseEntity<?> createDiary(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @RequestBody DiaryDto.CreateDiaryRequest request) {
        
        log.info("일기 작성 요청 - 사용자: {}, 내용: {}", userId, request.getContent());
        try {
            DiaryDto.DiaryResponse response = diaryService.createDiary(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("일기 작성 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * 이미지와 함께 일기 작성 API
     * 
     * 이미지 파일을 직접 업로드하면서 일기를 작성하고 감정 분석을 수행합니다.
     * 
     * 처리 과정:
     * 1. 사용자 ID 검증
     * 2. 이미지 파일 업로드 및 URL 생성
     * 3. OpenAI API를 통한 텍스트 감정 분석
     * 4. OpenAI API를 통한 이미지 감정 분석
     * 5. 텍스트와 이미지 통합 감정 분석
     * 6. 일기 데이터베이스 저장
     * 7. 감정 분석 결과와 함께 응답 반환
     * 
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param content 일기 내용
     * @param imageFile 업로드할 이미지 파일 (MultipartFile)
     * @return 생성된 일기 정보와 감정 분석 결과
     * 
     * HTTP 상태 코드:
     * - 201: 일기 생성 성공
     * - 400: 잘못된 요청 (사용자 없음, 파일 오류)
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지와 함께 일기 작성", description = "이미지 파일을 업로드하면서 일기를 작성합니다.")
    public ResponseEntity<?> createDiaryWithImage(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "일기 내용") @RequestParam String content,
            @Parameter(description = "이미지 파일") @RequestPart("image") MultipartFile imageFile) {
        
        log.info("이미지와 함께 일기 작성 요청 - 사용자: {}, 내용: {}, 파일명: {}", 
            userId, content, imageFile.getOriginalFilename());
        
        try {
            // 이미지 파일을 업로드하고 URL 생성
            String imageUrl = uploadImageFile(imageFile);
            
            // 일기 작성 요청 생성
            DiaryDto.CreateDiaryRequest request = new DiaryDto.CreateDiaryRequest();
            request.setContent(content);
            request.setImageUrl(imageUrl);
            
            // 일기 생성 및 감정 분석 수행
            DiaryDto.DiaryResponse response = diaryService.createDiary(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.error("이미지와 함께 일기 작성 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("이미지와 함께 일기 작성 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일기 작성 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 이미지 파일 업로드 헬퍼 메서드
     * 
     * @param imageFile 업로드할 이미지 파일
     * @return 업로드된 이미지의 접근 URL
     * @throws RuntimeException 파일 업로드 실패 시
     */
    private String uploadImageFile(MultipartFile imageFile) {
        // 파일 존재 여부 검증
        if (imageFile.isEmpty()) {
            throw new RuntimeException("이미지 파일이 비어있습니다.");
        }
        
        // 파일 크기 검증 (10MB)
        if (imageFile.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
        }
        
        // 파일 형식 검증
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
        }
        
        try {
            // 고유한 파일명 생성
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = java.util.UUID.randomUUID().toString() + fileExtension;
            
            // 업로드 디렉토리 생성
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("./uploads/");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            
            // 파일 저장
            java.nio.file.Path filePath = uploadPath.resolve(uniqueFilename);
            java.nio.file.Files.copy(imageFile.getInputStream(), filePath, 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // 접근 가능한 URL 반환
            return "/api/files/download/" + uniqueFilename;
            
        } catch (Exception e) {
            log.error("이미지 파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 일기 상세 조회 API
     * 
     * 특정 일기의 상세 내용과 감정 분석 결과를 조회합니다.
     * 
     * 조회 가능한 정보:
     * - 일기 기본 정보 (내용, 작성 시간, 수정 시간)
     * - 텍스트 감정 분석 결과 (감정, 점수, 신뢰도)
     * - 이미지 감정 분석 결과 (감정, 점수, 신뢰도)
     * - 통합 감정 분석 결과 (감정, 점수, 신뢰도)
     * - 추출된 키워드
     * 
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param diaryId 조회할 일기 ID (경로 변수)
     * @return 일기 상세 정보와 감정 분석 결과
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * - 404: 일기 없음
     * - 403: 권한 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/{diaryId}")
    @Operation(summary = "일기 상세 조회", description = "특정 일기의 상세 내용을 조회합니다.")
    public ResponseEntity<DiaryDto.DiaryResponse> getDiary(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "일기 ID") @PathVariable Long diaryId) {
        
        log.info("일기 상세 조회 요청 - 사용자: {}, 일기: {}", userId, diaryId);
        DiaryDto.DiaryResponse response = diaryService.getDiary(userId, diaryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 사용자별 일기 목록 조회 API (쿼리 파라미터 방식)
     * 
     * 특정 사용자의 일기 목록을 페이징하여 조회합니다.
     * 
     * @param userId 조회할 사용자 ID (쿼리 파라미터)
     * @param pageable 페이징 및 정렬 정보
     * @return 일기 목록 (페이징 정보 포함)
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping
    @Operation(summary = "사용자별 일기 목록 (쿼리 파라미터)", description = "특정 사용자의 일기 목록을 페이징하여 조회합니다.")
    public ResponseEntity<Page<DiaryDto.DiaryResponse>> getUserDiariesByQuery(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        
        log.info("사용자별 일기 목록 조회 요청 (쿼리 파라미터) - 사용자: {}, 페이지: {}", userId, pageable.getPageNumber());
        Page<DiaryDto.DiaryResponse> response = diaryService.getUserDiaries(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 사용자별 일기 목록 조회 API
     * 
     * 특정 사용자의 일기 목록을 페이징하여 조회합니다.
     * 
     * 페이징 특징:
     * - 기본 페이지 크기: 10개
     * - 정렬 기준: 생성 시간 내림차순 (최신순)
     * - 페이지 번호는 0부터 시작
     * 
     * @param userId 조회할 사용자 ID (경로 변수)
     * @param pageable 페이징 및 정렬 정보
     * @return 일기 목록 (페이징 정보 포함)
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 일기 목록", description = "특정 사용자의 일기 목록을 페이징하여 조회합니다.")
    public ResponseEntity<Page<DiaryDto.DiaryResponse>> getUserDiaries(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        
        log.info("사용자별 일기 목록 조회 요청 - 사용자: {}, 페이지: {}", userId, pageable.getPageNumber());
        Page<DiaryDto.DiaryResponse> response = diaryService.getUserDiaries(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 날짜 일기 조회 API
     * 
     * 사용자의 특정 날짜에 작성된 일기를 조회합니다.
     * 
     * 날짜 형식:
     * - yyyy-MM-dd (예: 2025-09-03)
     * - 시간대는 서버 기본 시간대 사용
     * 
     * @param userId 사용자 ID (경로 변수)
     * @param date 조회할 날짜 (쿼리 파라미터)
     * @return 해당 날짜의 일기 (없을 경우 404)
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * - 404: 해당 날짜 일기 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/user/{userId}/date")
    @Operation(summary = "특정 날짜 일기 조회", description = "사용자의 특정 날짜 일기를 조회합니다.")
    public ResponseEntity<DiaryDto.DiaryResponse> getDiaryByDate(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        log.info("특정 날짜 일기 조회 요청 - 사용자: {}, 날짜: {}", userId, date);
        return diaryService.getDiaryByDate(userId, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 감정별 일기 조회 API
     * 
     * 사용자의 특정 감정 타입별 일기를 조회합니다.
     * 
     * 지원하는 감정 타입:
     * - 행복, 슬픔, 분노, 평온, 우울, 기쁨, 불안, 화남, 만족, 실망
     * - 통합 감정 분석 결과를 기준으로 필터링
     * 
     * @param userId 사용자 ID (경로 변수)
     * @param emotion 조회할 감정 타입 (경로 변수)
     * @return 해당 감정의 일기 목록
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/user/{userId}/emotion/{emotion}")
    @Operation(summary = "감정별 일기 조회", description = "사용자의 특정 감정 타입별 일기를 조회합니다.")
    public ResponseEntity<List<DiaryDto.DiaryResponse>> getDiariesByEmotion(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "감정 타입") @PathVariable String emotion) {
        
        log.info("감정별 일기 조회 요청 - 사용자: {}, 감정: {}", userId, emotion);
        List<DiaryDto.DiaryResponse> response = diaryService.getDiariesByEmotion(userId, emotion);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 일기 수정 API
     * 
     * 기존 일기의 내용을 수정하고, 변경된 경우 감정 분석을 재수행합니다.
     * 
     * 감정 분석 재수행 조건:
     * - 일기 내용이 변경된 경우
     * - 이미지 URL이 변경된 경우
     * - 변경되지 않은 경우 기존 감정 분석 결과 유지
     * 
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param diaryId 수정할 일기 ID (경로 변수)
     * @param request 일기 수정 요청 데이터
     * @return 수정된 일기 정보와 업데이트된 감정 분석 결과
     * 
     * HTTP 상태 코드:
     * - 200: 수정 성공
     * - 404: 일기 없음
     * - 403: 권한 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @PutMapping("/{diaryId}")
    @Operation(summary = "일기 수정", description = "기존 일기를 수정합니다.")
    public ResponseEntity<DiaryDto.DiaryResponse> updateDiary(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "일기 ID") @PathVariable Long diaryId,
            @RequestBody DiaryDto.UpdateDiaryRequest request) {
        
        log.info("일기 수정 요청 - 사용자: {}, 일기: {}", userId, diaryId);
        DiaryDto.DiaryResponse response = diaryService.updateDiary(userId, diaryId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 일기 삭제 API
     * 
     * 사용자가 작성한 일기를 삭제합니다.
     * 
     * 삭제 조건:
     * - 일기 작성자만 삭제 가능
     * - 영구 삭제 (휴지통 없음)
     * 
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param diaryId 삭제할 일기 ID (경로 변수)
     * @return 삭제 성공 시 204 No Content
     * 
     * HTTP 상태 코드:
     * - 204: 삭제 성공
     * - 404: 일기 없음
     * - 403: 권한 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @DeleteMapping("/{diaryId}")
    @Operation(summary = "일기 삭제", description = "기존 일기를 삭제합니다.")
    public ResponseEntity<Void> deleteDiary(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "일기 ID") @PathVariable Long diaryId) {
        
        log.info("일기 삭제 요청 - 사용자: {}, 일기: {}", userId, diaryId);
        diaryService.deleteDiary(userId, diaryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 일기 감정 분석 결과 조회 API
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
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param diaryId 조회할 일기 ID (경로 변수)
     * @return 감정 분석 결과 상세 정보
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * - 404: 일기 없음
     * - 403: 권한 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/{diaryId}/analysis")
    @Operation(summary = "일기 감정 분석 결과", description = "특정 일기의 감정 분석 결과를 조회합니다.")
    public ResponseEntity<DiaryDto.EmotionAnalysisResponse> getDiaryAnalysis(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "일기 ID") @PathVariable Long diaryId) {
        
        log.info("일기 감정 분석 결과 조회 요청 - 사용자: {}, 일기: {}", userId, diaryId);
        DiaryDto.EmotionAnalysisResponse response = diaryService.getDiaryAnalysis(userId, diaryId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 일기 분석 요약 조회 API
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
     * @param userId 사용자 ID (쿼리 파라미터)
     * @param diaryId 조회할 일기 ID (경로 변수)
     * @return 감정 분석 요약 정보
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * - 404: 일기 없음
     * - 403: 권한 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/{diaryId}/summary")
    @Operation(summary = "일기 분석 요약", description = "특정 일기의 분석 결과 요약을 조회합니다.")
    public ResponseEntity<DiaryDto.AnalysisSummaryResponse> getDiaryAnalysisSummary(
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "일기 ID") @PathVariable Long diaryId) {
        
        log.info("일기 분석 요약 조회 요청 - 사용자: {}, 일기: {}", userId, diaryId);
        DiaryDto.AnalysisSummaryResponse response = diaryService.getDiaryAnalysisSummary(userId, diaryId);
        return ResponseEntity.ok(response);
    }
}
