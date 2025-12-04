# mooDiary 프로젝트 구조 가이드

##  프로젝트 개요

**mooDiary** - AI 기반 감정 일기 플랫폼
- **프론트엔드**: React + TypeScript
- **백엔드**: Java Spring Boot
- **AI 분석**: OpenAI API
- **데이터베이스**: MySQL
- **협업 인원**: 6명

##  전체 프로젝트 구조

```
mooDiary/
├── frontend/                    # React 프론트엔드
│   ├── src/
│   │   ├── types/              # TypeScript 타입 정의
│   │   │   ├── common.ts       # 공통 타입 (EmotionType, User 등)
│   │   │   └── api.ts          # API 요청/응답 타입
│   │   ├── components/         # 재사용 가능한 UI 컴포넌트
│   │   ├── pages/             # 페이지 컴포넌트
│   │   ├── hooks/             # 커스텀 React 훅
│   │   ├── utils/             # 유틸리티 함수
│   │   └── styles/            # CSS/스타일링 파일
│   └── public/                # 정적 파일 (이미지, 아이콘 등)
├── backend/                    # Java Spring Boot 백엔드
│   └── src/main/java/com/moodiary/
│       ├── entity/            # JPA 엔티티 (데이터베이스 테이블)
│       │   ├── User.java              # 사용자 정보
│       │   ├── DiaryEntry.java        # 일기 엔트리
│       │   ├── EmotionType.java       # 감정 타입 enum
│       │   ├── RecommendedContent.java # 추천 콘텐츠
│       │   ├── CommunityPost.java     # 커뮤니티 게시글
│       │   └── CommunityComment.java  # 커뮤니티 댓글
│       ├── dto/               # Data Transfer Object
│       │   ├── UserDto.java           # 사용자 관련 DTO
│       │   ├── DiaryDto.java          # 일기 관련 DTO
│       │   ├── OpenAiDto.java         # OpenAI API 관련 DTO
│       │   └── CommunityDto.java      # 커뮤니티 관련 DTO
│       ├── controller/        # REST API 컨트롤러
│       ├── service/           # 비즈니스 로직
│       ├── repository/        # 데이터 접근 계층
│       ├── config/            # 설정 파일
│       ├── exception/         # 예외 처리
│       └── util/              # 유틸리티 클래스
├── tests/                     # 테스트 파일
├── scripts/                   # 빌드/배포 스크립트
└── README.md                  # 프로젝트 설명
```

##  핵심 기능별 구조

### 1. 사용자 관리 (User Management)
```
Entity: User.java
DTO: UserDto.java
- SignUpRequest: 회원가입 요청
- LoginRequest: 로그인 요청
- UserResponse: 사용자 정보 응답
- TokenResponse: JWT 토큰 응답
```

### 2. 일기 작성 (Diary Writing)
```
Entity: DiaryEntry.java
DTO: DiaryDto.java
- CreateDiaryRequest: 일기 작성 요청
- DiaryResponse: 일기 정보 응답
- EmotionAnalysisResponse: 감정 분석 결과
```

### 3. 감정 분석 (Emotion Analysis)
```
DTO: OpenAiDto.java
- EmotionAnalysisRequest: 감정 분석 요청
- EmotionAnalysisResponse: 감정 분석 응답
- ContentRecommendationRequest: 콘텐츠 추천 요청
```

### 4. 커뮤니티 (Community)
```
Entity: CommunityPost.java, CommunityComment.java
DTO: CommunityDto.java
- CreatePostRequest: 게시글 작성 요청
- PostResponse: 게시글 정보 응답
- CommentResponse: 댓글 정보 응답
```

##  팀원별 담당 영역 

### Frontend Developer 1 - // UI/UX Components
- **담당**:
- **주요 작업**: 
  -
  -
  -

### Frontend Developer 2 - // Pages & Styling
- **담당**:
- **주요 작업**:
  -
  -
  -

### Backend Developer 1 -
- **담당**:
- **주요 작업**:
  -
  -
  -

### Backend Developer 2
- **담당**:
- **주요 작업**:
  -
  -
  -

### Backend Developer 3
- **담당**:
- **주요 작업**:
  -
  -
  -

### Backend Developer 4
- **담당**:
- **주요 작업**:
  -
  -
  -

##  개발 워크플로우

### 1. 브랜치 전략
```
main (메인 브랜치)
├── develop (개발 브랜치)
├── feature/user-auth (사용자 인증 기능)
├── feature/diary-writing (일기 작성 기능)
├── feature/emotion-analysis (감정 분석 기능)
└── feature/community (커뮤니티 기능)
```

### 2. 커밋 메시지 규칙
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 추가/수정
chore: 빌드 프로세스 또는 보조 도구 변경
```

### 3. API 설계 규칙
```
GET    /api/users/{id}          # 사용자 정보 조회
POST   /api/users               # 사용자 등록
PUT    /api/users/{id}          # 사용자 정보 수정
DELETE /api/users/{id}          # 사용자 삭제

GET    /api/diaries             # 일기 목록 조회
POST   /api/diaries             # 일기 작성
GET    /api/diaries/{id}        # 일기 상세 조회
PUT    /api/diaries/{id}        # 일기 수정
DELETE /api/diaries/{id}        # 일기 삭제

POST   /api/emotion/analyze     # 감정 분석
GET    /api/emotion/trend       # 감정 트렌드 조회

GET    /api/community/posts     # 커뮤니티 게시글 목록
POST   /api/community/posts     # 게시글 작성
```

##  데이터 모델 관계

```
User (1) ←→ (N) DiaryEntry
User (1) ←→ (N) CommunityPost
User (1) ←→ (N) CommunityComment
CommunityPost (1) ←→ (N) CommunityComment
```

##  기술 스택 상세

### Frontend
- **React 18**: UI 라이브러리
- **TypeScript**: 타입 안정성
- **Tailwind CSS**: 스타일링
- **Recharts**: 차트 라이브러리
- **Axios**: HTTP 클라이언트

### Backend
- **Spring Boot 3**: 백엔드 프레임워크
- **Spring Security**: 인증/인가
- **Spring Data JPA**: 데이터 접근
- **MySQL**: 데이터베이스
- **JWT**: 토큰 기반 인증
- **OpenAI API**: 감정 분석

##  개발 환경 설정

### Prerequisites
- Node.js 18+
- Java 17+
- MySQL 8.0+
- Git

### Frontend 설정
```bash
cd frontend
npm install
npm start
```

### Backend 설정
```bash
cd backend
gradlew.bat build
gradlew.bat bootRun
```

##  다음 단계

1. **개발 환경 설정**: 각자 로컬 환경 구성
2. **기본 기능 구현**: 사용자 인증부터 시작
3. **API 연동**: 프론트엔드-백엔드 통신
4. **AI 기능 통합**: OpenAI API 연동
5. **테스트 및 배포**: 품질 검증 및 배포

