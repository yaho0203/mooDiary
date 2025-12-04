# MooDiary API 명세서

## 기본 정보
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json` (일반), `multipart/form-data` (파일 업로드)
- **인증 방식**: JWT Token (현재는 permitAll, 향후 적용 예정)

---

## 1. 사용자 API (User API)

### 1.1 회원가입
**POST** `/api/users/create`

**Request Body:**
```json
{
  "email": "string",
  "password": "string",
  "nickname": "string",
  "phone": "string (optional)",
  "username": "string (optional)",
  "profileImage": "string (optional)"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "nickname": "사용자닉네임",
  "profileImage": "string (optional)",
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

---

### 1.2 로그인
**POST** `/api/users/login`

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "사용자닉네임",
    "profileImage": "string (optional)",
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-01-01T00:00:00"
  }
}
```

---

### 1.3 토큰 갱신
**POST** `/api/users/refresh`

**Request Header:**
```
refresh-token: Bearer {refreshToken}
```

**Response (200 OK):**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

---

### 1.4 테스트
**GET** `/api/users/test`

**Response (200 OK):**
```
"OK"
```

---

## 2. 일기 API (Diary API)

### 2.1 일기 작성
**POST** `/api/diaries?userId={userId}`

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Request Body:**
```json
{
  "content": "string",
  "imageUrl": "string (optional)"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "content": "일기 내용",
  "imageUrl": "string (optional)",
  "emotionAnalysis": {
    "textEmotion": {
      "emotion": "HAPPY",
      "score": 0.85,
      "confidence": 0.92
    },
    "facialEmotion": {
      "emotion": "HAPPY",
      "score": 0.78,
      "confidence": 0.88
    },
    "integratedEmotion": {
      "emotion": "HAPPY",
      "score": 0.82,
      "confidence": 0.90
    },
    "keywords": ["키워드1", "키워드2"],
    "timestamp": "2025-01-01T00:00:00"
  },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Error Response (400 Bad Request):**
```
"에러 메시지"
```

---

### 2.2 이미지와 함께 일기 작성
**POST** `/api/diaries/with-image?userId={userId}`

**Content-Type:** `multipart/form-data`

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Form Data:**
- `content` (String, required): 일기 내용
- `image` (MultipartFile, required): 이미지 파일

**제한사항:**
- 파일 크기: 최대 10MB
- 파일 형식: jpg, jpeg, png, gif

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "content": "일기 내용",
  "imageUrl": "/api/files/download/{filename}",
  "emotionAnalysis": {
    "textEmotion": {
      "emotion": "HAPPY",
      "score": 0.85,
      "confidence": 0.92
    },
    "facialEmotion": {
      "emotion": "HAPPY",
      "score": 0.78,
      "confidence": 0.88
    },
    "integratedEmotion": {
      "emotion": "HAPPY",
      "score": 0.82,
      "confidence": 0.90
    },
    "keywords": ["키워드1", "키워드2"],
    "timestamp": "2025-01-01T00:00:00"
  },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Error Response (400 Bad Request):**
```
"에러 메시지"
```

---

### 2.3 일기 세부내역 조회 (일기 상세 조회)
**GET** `/api/diaries/{diaryId}?userId={userId}`

**설명:** 특정 일기의 상세 내용과 감정 분석 결과를 조회합니다.

**Path Parameters:**
- `diaryId` (Long, required): 일기 ID

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "content": "일기 내용",
  "imageUrl": "string (optional)",
  "emotionAnalysis": {
    "textEmotion": {
      "emotion": "HAPPY",
      "score": 0.85,
      "confidence": 0.92
    },
    "facialEmotion": {
      "emotion": "HAPPY",
      "score": 0.78,
      "confidence": 0.88
    },
    "integratedEmotion": {
      "emotion": "HAPPY",
      "score": 0.82,
      "confidence": 0.90
    },
    "keywords": ["키워드1", "키워드2"],
    "timestamp": "2025-01-01T00:00:00"
  },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

---

### 2.4 전체 일기 목록 조회
**GET** `/api/diaries/all?page={page}&size={size}&sort={sort}`

**설명:** 모든 사용자의 일기를 조회합니다. (현재 미구현 - TODO)

**Query Parameters:**
- `page` (Integer, optional): 페이지 번호 (기본값: 0)
- `size` (Integer, optional): 페이지 크기 (기본값: 10)
- `sort` (String, optional): 정렬 기준 (기본값: createdAt)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "content": "일기 내용",
      "imageUrl": "string (optional)",
      "emotionAnalysis": { ... },
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

**Note:** 현재는 구현되지 않았습니다. 필요시 백엔드에 추가 요청이 필요합니다.

---

### 2.5 사용자 자신이 작성한 일기 전체 조회 (쿼리 파라미터)
**GET** `/api/diaries?userId={userId}&page={page}&size={size}&sort={sort}`

**설명:** 사용자 자신이 작성한 모든 일기를 페이징하여 조회합니다.

**Query Parameters:**
- `userId` (Long, required): 사용자 ID
- `page` (Integer, optional): 페이지 번호 (기본값: 0)
- `size` (Integer, optional): 페이지 크기 (기본값: 10)
- `sort` (String, optional): 정렬 기준 (기본값: createdAt)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "content": "일기 내용",
      "imageUrl": "string (optional)",
      "emotionAnalysis": { ... },
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

---

### 2.6 사용자 자신이 작성한 일기 전체 조회 (경로 변수)
**GET** `/api/diaries/user/{userId}?page={page}&size={size}&sort={sort}`

**설명:** 사용자 자신이 작성한 모든 일기를 페이징하여 조회합니다. (2.5와 동일한 기능, 경로 변수 방식)

**Path Parameters:**
- `userId` (Long, required): 사용자 ID

**Query Parameters:**
- `page` (Integer, optional): 페이지 번호 (기본값: 0)
- `size` (Integer, optional): 페이지 크기 (기본값: 10)
- `sort` (String, optional): 정렬 기준 (기본값: createdAt)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "content": "일기 내용",
      "imageUrl": "string (optional)",
      "emotionAnalysis": { ... },
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

---

### 2.7 특정 날짜 일기 조회
**GET** `/api/diaries/user/{userId}/date?date={yyyy-MM-dd}`

**Path Parameters:**
- `userId` (Long, required): 사용자 ID

**Query Parameters:**
- `date` (String, required): 날짜 (형식: yyyy-MM-dd, 예: 2025-01-01)

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "content": "일기 내용",
  "imageUrl": "string (optional)",
  "emotionAnalysis": { ... },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Response (404 Not Found):**
해당 날짜의 일기가 없을 경우

---

### 2.8 감정별 일기 조회
**GET** `/api/diaries/user/{userId}/emotion/{emotion}`

**Path Parameters:**
- `userId` (Long, required): 사용자 ID
- `emotion` (String, required): 감정 타입 (예: HAPPY, SAD, ANGRY 등)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "content": "일기 내용",
    "imageUrl": "string (optional)",
    "emotionAnalysis": { ... },
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-01-01T00:00:00"
  }
]
```

---

### 2.9 일기 수정
**PUT** `/api/diaries/{diaryId}?userId={userId}`

**Path Parameters:**
- `diaryId` (Long, required): 일기 ID

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Request Body:**
```json
{
  "content": "수정된 일기 내용",
  "imageUrl": "string (optional)"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "content": "수정된 일기 내용",
  "imageUrl": "string (optional)",
  "emotionAnalysis": { ... },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Note:** 내용이나 이미지가 변경된 경우 감정 분석이 재수행됩니다.

---

### 2.10 일기 삭제
**DELETE** `/api/diaries/{diaryId}?userId={userId}`

**Path Parameters:**
- `diaryId` (Long, required): 일기 ID

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Response (204 No Content):**
삭제 성공 시 응답 본문 없음

---

### 2.11 일기 감정 분석 결과 조회
**GET** `/api/diaries/{diaryId}/analysis?userId={userId}`

**Path Parameters:**
- `diaryId` (Long, required): 일기 ID

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Response (200 OK):**
```json
{
  "textEmotion": {
    "emotion": "HAPPY",
    "score": 0.85,
    "confidence": 0.92
  },
  "facialEmotion": {
    "emotion": "HAPPY",
    "score": 0.78,
    "confidence": 0.88
  },
  "integratedEmotion": {
    "emotion": "HAPPY",
    "score": 0.82,
    "confidence": 0.90
  },
  "keywords": ["키워드1", "키워드2"],
  "timestamp": "2025-01-01T00:00:00"
}
```

---

### 2.12 일기 분석 요약 조회
**GET** `/api/diaries/{diaryId}/summary?userId={userId}`

**Path Parameters:**
- `diaryId` (Long, required): 일기 ID

**Query Parameters:**
- `userId` (Long, required): 사용자 ID

**Response (200 OK):**
```json
{
  "diaryId": 1,
  "content": "일기 내용",
  "overallEmotion": "HAPPY",
  "overallEmotionScore": 0.82,
  "dominantEmotion": "HAPPY",
  "topKeywords": ["키워드1", "키워드2", "키워드3", "키워드4", "키워드5"],
  "analysisInsight": "사용자 친화적 분석 인사이트",
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

---

## 3. 파일 API (File API)

### 3.1 이미지 파일 업로드
**POST** `/api/files/upload`

**Content-Type:** `multipart/form-data`

**Form Data:**
- `file` (MultipartFile, required): 이미지 파일

**제한사항:**
- 파일 크기: 최대 5MB (설정값에 따라 다를 수 있음)
- 파일 형식: jpg, jpeg, png, gif
- MIME 타입: image/jpeg, image/jpg, image/png, image/gif

**Response (200 OK):**
```json
{
  "filename": "uuid-generated-filename.jpg",
  "url": "/api/files/download/uuid-generated-filename.jpg",
  "size": 1024000
}
```

**Error Response (400 Bad Request):**
```
"파일이 비어있습니다."
"파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다."
"지원하지 않는 파일 형식입니다. jpg, jpeg, png, gif 파일만 업로드 가능합니다."
```

**Error Response (500 Internal Server Error):**
```
"파일 업로드 중 오류가 발생했습니다."
```

---

### 3.2 파일 다운로드
**GET** `/api/files/download/{filename}`

**Path Parameters:**
- `filename` (String, required): 다운로드할 파일명

**Response (200 OK):**
- Content-Type: 이미지 MIME 타입 (image/jpeg, image/png 등)
- 파일 바이너리 데이터

**Response (404 Not Found):**
파일이 존재하지 않을 경우

---

## 4. 감정 API (Emotion API)

**현재 구현되지 않음 (TODO)**

예정된 기능:
- 텍스트 감정 분석
- 이미지 감정 분석
- 통합 감정 분석
- 감정 트렌드 조회
- 콘텐츠 추천

---

## 5. 커뮤니티 API (Community API)

**현재 구현되지 않음 (TODO)**

예정된 기능:
- 게시글 작성
- 게시글 목록 조회
- 게시글 상세 조회
- 댓글 작성
- 댓글 목록 조회

---

## 감정 타입 (EmotionType)

지원하는 감정 타입:
- `HAPPY` (행복)
- `SAD` (슬픔)
- `ANGRY` (분노)
- `CALM` (평온)
- `DEPRESSED` (우울)
- `JOYFUL` (기쁨)
- `ANXIOUS` (불안)
- `FRUSTRATED` (화남)
- `SATISFIED` (만족)
- `DISAPPOINTED` (실망)

---

## 에러 응답 형식

일반적인 에러 응답:
- **400 Bad Request**: 잘못된 요청 (필수 필드 누락, 형식 오류 등)
- **401 Unauthorized**: 인증 실패 (향후 JWT 적용 시)
- **403 Forbidden**: 권한 없음
- **404 Not Found**: 리소스를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

에러 메시지는 문자열로 반환됩니다:
```
"에러 메시지 내용"
```

---

## 페이징 정보

페이징 응답 구조:
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

- `content`: 현재 페이지의 데이터 배열
- `totalElements`: 전체 데이터 개수
- `totalPages`: 전체 페이지 수
- `size`: 페이지 크기
- `number`: 현재 페이지 번호 (0부터 시작)
- `first`: 첫 페이지 여부
- `last`: 마지막 페이지 여부

---

## 날짜/시간 형식

모든 날짜/시간 필드는 ISO 8601 형식을 사용합니다:
- 형식: `yyyy-MM-ddTHH:mm:ss`
- 예시: `2025-01-01T12:30:45`

날짜만 필요한 경우:
- 형식: `yyyy-MM-dd`
- 예시: `2025-01-01`

---

## Swagger UI

API 문서는 Swagger UI를 통해 확인할 수 있습니다:
- **URL**: `http://localhost:8080/api/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api/api-docs`

---

## 참고사항

1. **인증**: 현재는 모든 API가 `permitAll()` 설정으로 되어 있어 인증 없이 사용 가능합니다. 향후 JWT 토큰 기반 인증으로 변경 예정입니다.

2. **CORS**: 모든 API는 `@CrossOrigin(origins = "*")` 설정으로 CORS가 허용되어 있습니다.

3. **파일 업로드**: 
   - 최대 파일 크기: 5MB (설정 파일에서 변경 가능)
   - 허용 파일 형식: jpg, jpeg, png, gif
   - 업로드 디렉토리: `./uploads/`

4. **감정 분석**: 
   - OpenAI API를 사용하여 텍스트와 이미지의 감정을 분석합니다.
   - 텍스트 감정, 이미지 감정, 통합 감정 분석 결과를 제공합니다.

5. **페이징**: 
   - 기본 페이지 크기: 10개
   - 기본 정렬: 생성 시간 내림차순 (최신순)
   - 페이지 번호는 0부터 시작합니다.

