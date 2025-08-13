# mooDiary

AI 기반 감정 일기 플랫폼

## Quick Start

### Prerequisites
- Node.js 18+
- Java 17+
- MySQL 8.0+

### 1. 데이터베이스 설정
```bash
mysql -u root -p < scripts/database-schema.sql
```

### 2. 백엔드 실행
```bash
cd backend
gradlew.bat bootRun
```

### 3. 프론트엔드 실행
```bash
cd frontend
npm install
npm start
```

### 4. 개발 환경 전체 시작 (Windows)
```bash
scripts/start-dev.bat
```

### 5. 개발 환경 전체 시작 (Linux/Mac)
```bash
chmod +x scripts/start-dev.sh
./scripts/start-dev.sh
```

## 프로젝트 구조

```
mooDiary/
├── frontend/          # React + TypeScript
├── backend/           # Spring Boot
├── scripts/           # 개발 스크립트
├── tests/             # 테스트 파일
└── PROJECT_STRUCTURE.md  # 상세 구조 가이드
```

## 환경 설정

### 백엔드 설정
- `backend/src/main/resources/application.yml`에서 데이터베이스 연결 정보 수정
- OpenAI API 키 설정 필요

### 프론트엔드 설정
- `frontend/package.json`에서 의존성 확인
- `frontend/src/types/`에서 타입 정의 확인

## 상세 문서

프로젝트의 상세한 구조와 개발 가이드라인은 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)를 참조하세요.

## 주요 기능

- 일기 작성 및 감정 분석
- AI 기반 감정 인식 (텍스트 + 이미지)
- 감정 트렌드 시각화
- 감정 기반 콘텐츠 추천
- 커뮤니티 기능

## 기술 스택

- **Frontend**: React, TypeScript, Tailwind CSS
- **Backend**: Spring Boot, JPA, MySQL
- **AI**: OpenAI API
- **Security**: JWT, Spring Security
