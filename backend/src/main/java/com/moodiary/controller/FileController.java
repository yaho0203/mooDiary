package com.moodiary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 파일 업로드 및 다운로드 API 컨트롤러
 * 
 * 이미지 파일 업로드, 다운로드, 삭제를 담당하는 REST API 엔드포인트들을 제공합니다.
 * 
 * 주요 기능:
 * - 이미지 파일 업로드 (MultipartFile)
 * - 업로드된 파일 다운로드
 * - 파일 형식 검증 (jpg, jpeg, png, gif)
 * - 파일 크기 제한 (10MB)
 * - 고유한 파일명 생성 (UUID)
 * 
 * API 특징:
 * - RESTful 설계 원칙 준수
 * - Swagger/OpenAPI 문서화 지원
 * - CORS 설정으로 프론트엔드 연동 지원
 * - 파일 보안 검증 (확장자, MIME 타입)
 * - 로깅을 통한 요청/응답 추적
 * 
 * @author hyeonSuKim
 * @since 2025-09-21
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "파일 API", description = "이미지 파일 업로드, 다운로드 API")
public class FileController {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.allowed-types}")
    private String allowedTypes;
    
    @Value("${file.max-size}")
    private long maxSize;
    
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    
    /**
     * 이미지 파일 업로드 API
     * 
     * 사용자가 선택한 이미지 파일을 서버에 업로드하고 접근 가능한 URL을 반환합니다.
     * 
     * 처리 과정:
     * 1. 파일 존재 여부 검증
     * 2. 파일 크기 검증 (최대 10MB)
     * 3. 파일 형식 검증 (jpg, jpeg, png, gif)
     * 4. 고유한 파일명 생성 (UUID)
     * 5. 파일을 업로드 디렉토리에 저장
     * 6. 접근 가능한 URL 반환
     * 
     * @param file 업로드할 이미지 파일 (MultipartFile)
     * @return 업로드된 파일의 접근 URL
     * 
     * HTTP 상태 코드:
     * - 200: 업로드 성공
     * - 400: 잘못된 요청 (파일 없음, 크기 초과, 형식 오류)
     * - 500: 서버 오류 (파일 저장 실패)
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 파일 업로드", description = "이미지 파일을 업로드하고 접근 URL을 반환합니다.")
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "업로드할 이미지 파일") @RequestPart("file") MultipartFile file) {
        
        log.info("파일 업로드 요청 - 파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());
        
        try {
            // 1. 파일 존재 여부 검증
            if (file.isEmpty()) {
                log.warn("업로드 실패: 파일이 비어있습니다.");
                return ResponseEntity.badRequest().body("파일이 비어있습니다.");
            }
            
            // 2. 파일 크기 검증
            if (file.getSize() > maxSize) {
                log.warn("업로드 실패: 파일 크기가 너무 큽니다. 크기: {} bytes, 최대: {} bytes", 
                    file.getSize(), maxSize);
                return ResponseEntity.badRequest().body("파일 크기가 너무 큽니다. 최대 " + (maxSize / 1024 / 1024) + "MB까지 업로드 가능합니다.");
            }
            
            // 3. 파일 형식 검증
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                log.warn("업로드 실패: 지원하지 않는 파일 형식입니다. MIME 타입: {}", contentType);
                return ResponseEntity.badRequest().body("지원하지 않는 파일 형식입니다. jpg, jpeg, png, gif 파일만 업로드 가능합니다.");
            }
            
            // 4. 원본 파일명에서 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 5. 고유한 파일명 생성 (UUID + 확장자)
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 6. 업로드 디렉토리 생성 (존재하지 않는 경우)
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("업로드 디렉토리 생성: {}", uploadPath.toAbsolutePath());
            }
            
            // 7. 파일 저장
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 8. 접근 가능한 URL 생성
            String fileUrl = "/api/files/download/" + uniqueFilename;
            
            log.info("파일 업로드 성공 - 원본명: {}, 저장명: {}, URL: {}", 
                originalFilename, uniqueFilename, fileUrl);
            
            return ResponseEntity.ok().body(new FileUploadResponse(uniqueFilename, fileUrl, file.getSize()));
            
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("파일 업로드 중 예상치 못한 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("파일 업로드 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 업로드된 파일 다운로드 API
     * 
     * 업로드된 이미지 파일을 다운로드합니다.
     * 
     * @param filename 다운로드할 파일명
     * @return 파일 리소스
     * 
     * HTTP 상태 코드:
     * - 200: 다운로드 성공
     * - 404: 파일 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-21
     */
    @GetMapping("/download/{filename}")
    @Operation(summary = "파일 다운로드", description = "업로드된 파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "다운로드할 파일명") @PathVariable String filename) {
        
        log.info("파일 다운로드 요청 - 파일명: {}", filename);
        
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // MIME 타입 설정
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                log.info("파일 다운로드 성공 - 파일명: {}, MIME 타입: {}", filename, contentType);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                log.warn("파일 다운로드 실패: 파일을 찾을 수 없습니다 - {}", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("파일 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 파일 업로드 응답 DTO
     */
    public static class FileUploadResponse {
        private String filename;
        private String url;
        private long size;
        
        public FileUploadResponse(String filename, String url, long size) {
            this.filename = filename;
            this.url = url;
            this.size = size;
        }
        
        // Getters
        public String getFilename() { return filename; }
        public String getUrl() { return url; }
        public long getSize() { return size; }
    }
}
