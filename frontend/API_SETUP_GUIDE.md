# API 설정 가이드

## 📋 현재 설정 상태

백엔드 배포 주소: `https://www.jinwook.shop`

## 🔧 환경 변수 설정

프로젝트 루트에 다음 파일들을 생성하세요:

### 1. `.env` (로컬 개발용)

```bash
# 개발 환경 설정
# 옵션 1: 프록시 사용 (권장) - vite.config.ts의 프록시 설정 활용
VITE_API_URL=

# 옵션 2: 직접 백엔드 URL 사용
# VITE_API_URL=https://www.jinwook.shop
```

### 2. `.env.production` (프로덕션 빌드용)

```bash
# 프로덕션 환경 설정
VITE_API_URL=https://www.jinwook.shop
```

### 3. `.env.local` (로컬 개발 개인 설정, Git에 커밋되지 않음)

```bash
# 개인 개발 환경 설정 (선택사항)
VITE_API_URL=https://www.jinwook.shop
```

## 🏗️ API 호출 로직 설명

### 개발 환경 (Development)

**방법 1: 프록시 사용 (권장)**
```typescript
// .env에 VITE_API_URL= (빈 문자열)
// vite.config.ts의 프록시가 자동으로 작동
// /api/emotion → https://www.jinwook.shop/api/emotion
```

**방법 2: 직접 URL 사용**
```typescript
// .env에 VITE_API_URL=https://www.jinwook.shop
// axios가 직접 백엔드 서버로 요청
```

### 프로덕션 환경 (Production)

```typescript
// .env.production에 VITE_API_URL=https://www.jinwook.shop
// 빌드 시 환경 변수가 번들에 포함됨
// axios가 직접 백엔드 서버로 요청
```

## 📡 API 클라이언트 동작 방식

### 1. Base URL 결정 로직

```typescript
// src/lib/apiClient.ts의 getApiBaseUrl() 함수

1. VITE_API_URL 환경 변수 확인
   ↓ 있으면 → 그대로 사용
   ↓ 없으면
2. 프로덕션 환경인가?
   ↓ 예 → https://www.jinwook.shop (기본값)
   ↓ 아니오
3. 개발 환경 → "" (빈 문자열, 프록시 사용)
```

### 2. 인증 토큰 자동 추가

```typescript
// Request Interceptor가 모든 요청에 자동으로 토큰 추가
api.interceptors.request.use((config) => {
  const token = getAccessToken(); // localStorage에서 토큰 가져오기
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 3. 에러 처리

```typescript
// Response Interceptor가 에러 처리
- 401 Unauthorized: 인증 실패 감지
- 상세한 에러 로그 출력
- 에러 메시지 추출 및 전달
```

## 🔍 실제 API 호출 예시

### 예시 1: 감정 데이터 조회

```typescript
// src/pages/RecBook.tsx
import { getEmotionData } from "@/lib/apiClient";

// 실제 호출:
// 개발 환경 (프록시): GET http://localhost:8080/api/emotion
//   → 프록시가 https://www.jinwook.shop/api/emotion로 전달
// 프로덕션: GET https://www.jinwook.shop/api/emotion
const emotionData = await getEmotionData();
```

### 예시 2: 시 추천 생성

```typescript
// src/pages/RecBook.tsx
import { createPoemRecommendation } from "@/lib/apiClient";

// 실제 호출:
// GET https://www.jinwook.shop/api/recommend/poem/create
// Headers: Authorization: Bearer {accessToken}
const recommendation = await createPoemRecommendation();
```

## ⚠️ 주의사항

### 1. 인증이 필요한 엔드포인트

백엔드 API는 대부분 인증이 필요합니다. 로그인 후 `accessToken`이 localStorage에 저장되어야 합니다.

```typescript
// 로그인 성공 시
localStorage.setItem("accessToken", "your-token-here");

// 이후 모든 API 요청에 자동으로 토큰이 포함됨
```

### 2. CORS 문제

프로덕션 환경에서 직접 백엔드로 요청할 때 CORS 문제가 발생할 수 있습니다. 백엔드 서버에서 CORS 설정이 필요합니다:

```
Access-Control-Allow-Origin: https://your-frontend-domain.com
Access-Control-Allow-Credentials: true
```

### 3. 프록시 vs 직접 연결

| 방식 | 장점 | 단점 |
|------|------|------|
| 프록시 (개발) | CORS 문제 없음, 개발 편의성 | 개발 서버에서만 작동 |
| 직접 연결 | 간단한 설정 | CORS 설정 필요 |

## 🧪 테스트 방법

### 1. 개발 환경 테스트

```bash
# 1. .env 파일 생성
echo "VITE_API_URL=" > .env

# 2. 개발 서버 실행
pnpm dev

# 3. 브라우저 콘솔에서 확인
# "🔧 API 설정:" 로그 확인
```

### 2. 프로덕션 빌드 테스트

```bash
# 1. .env.production 파일 생성
echo "VITE_API_URL=https://www.jinwook.shop" > .env.production

# 2. 빌드
pnpm build

# 3. 빌드된 파일 확인
# dist 폴더의 파일들이 올바른 API URL을 사용하는지 확인
```

## 📝 현재 설정 확인

### vite.config.ts 프록시 설정

```typescript
server: {
  proxy: {
    '/api': {
      target: 'https://www.jinwook.shop',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '/api'),
    }
  }
}
```

이 설정은 개발 서버(`pnpm dev`)에서만 작동합니다.

### apiClient.ts 설정

```typescript
// 현재 설정된 내용:
- 개발 환경: 프록시 사용 또는 직접 URL
- 프로덕션: https://www.jinwook.shop (기본값)
- 인증 토큰: 자동 추가
- 에러 처리: 자동 처리
```

## ✅ 체크리스트

- [ ] `.env` 파일 생성 (개발용)
- [ ] `.env.production` 파일 생성 (프로덕션용)
- [ ] 로그인 후 토큰이 localStorage에 저장되는지 확인
- [ ] API 요청 시 토큰이 헤더에 포함되는지 확인
- [ ] 개발 서버에서 API 호출 테스트
- [ ] 프로덕션 빌드에서 API 호출 테스트

## 🐛 문제 해결

### 문제 1: API 요청이 localhost로 가는 경우

**원인**: `VITE_API_URL`이 설정되지 않았고, 개발 환경에서 프록시가 작동하지 않음

**해결**: 
- `.env` 파일에 `VITE_API_URL=https://www.jinwook.shop` 추가
- 또는 프록시 설정 확인

### 문제 2: 401 Unauthorized 에러

**원인**: 인증 토큰이 없거나 만료됨

**해결**:
- 로그인 후 토큰이 localStorage에 저장되는지 확인
- 토큰이 만료되었으면 재로그인

### 문제 3: CORS 에러

**원인**: 백엔드 서버의 CORS 설정 문제

**해결**:
- 개발 환경에서는 프록시 사용
- 프로덕션에서는 백엔드에서 CORS 설정 필요

## 📚 참고 자료

- [Vite 환경 변수 문서](https://vitejs.dev/guide/env-and-mode.html)
- [Axios 인터셉터 문서](https://axios-http.com/docs/interceptors)
- [CORS 설명](https://developer.mozilla.org/ko/docs/Web/HTTP/CORS)

