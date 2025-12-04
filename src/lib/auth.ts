// 인증 관련 유틸리티 함수

import type { AuthTokens, LoginRequest, RegisterRequest, SocialLoginRequest, User } from "@shared/types";

const API_BASE = import.meta.env.VITE_API_URL 
  ? `${import.meta.env.VITE_API_URL}`
  : import.meta.env.PROD 
    ? "https://www.jinwook.shop"
    : ""; // 개발 환경에서는 프록시 사용

export class AuthError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "AuthError";
  }
}

/**
 * 일반 로그인
 * POST /api/users/login
 */
export const login = async (credentials: LoginRequest): Promise<AuthTokens> => {
  try {
    const response = await fetch(`${API_BASE}/api/users/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
      credentials: "include",
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new AuthError(`로그인 실패: ${errorText}`);
    }

    const data: AuthTokens = await response.json();
    return data;
  } catch (error) {
    if (error instanceof AuthError) {
      throw error;
    }
    throw new AuthError("로그인 중 오류가 발생했습니다.");
  }
};

/**
 * 회원가입
 * POST /api/users/create
 * @returns 생성된 사용자의 PK
 */
export const register = async (userData: RegisterRequest): Promise<number> => {
  try {
    const response = await fetch(`${API_BASE}/api/users/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
      credentials: "include",
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new AuthError(`회원가입 실패: ${errorText}`);
    }

    const userId: number = await response.json();
    return userId;
  } catch (error) {
    if (error instanceof AuthError) {
      throw error;
    }
    throw new AuthError("회원가입 중 오류가 발생했습니다.");
  }
};

/**
 * 소셜 로그인 (기본키 기반)
 * POST /api/users/social/login
 */
export const socialLogin = async (userID: number): Promise<AuthTokens> => {
  try {
    const response = await fetch(`${API_BASE}/api/users/social/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ userID }),
      credentials: "include",
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new AuthError(`소셜 로그인 실패: ${errorText}`);
    }

    const data: AuthTokens = await response.json();
    return data;
  } catch (error) {
    if (error instanceof AuthError) {
      throw error;
    }
    throw new AuthError("소셜 로그인 중 오류가 발생했습니다.");
  }
};

export const saveTokens = (tokens: AuthTokens): void => {
  localStorage.setItem("accessToken", tokens.accessToken);
  localStorage.setItem("refreshToken", tokens.refreshToken);
  localStorage.setItem("tokenType", tokens.tokenType);
  localStorage.setItem("expiresIn", tokens.expiresIn.toString());
  localStorage.setItem("authToken", tokens.accessToken); // AuthContext 호환성
  
  // 사용자 정보도 저장
  if (tokens.user) {
    localStorage.setItem("user", JSON.stringify(tokens.user));
  }
};

export const clearTokens = (): void => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("tokenType");
  localStorage.removeItem("expiresIn");
  localStorage.removeItem("authToken");
  localStorage.removeItem("user"); // 사용자 정보도 삭제
};

export const getAccessToken = (): string | null => {
  return localStorage.getItem("accessToken");
};

export const getUserInfo = (): User | null => {
  const userStr = localStorage.getItem("user");
  if (!userStr) return null;
  try {
    return JSON.parse(userStr);
  } catch {
    return null;
  }
};

export const isAuthenticated = (): boolean => {
  return !!getAccessToken();
};

/**
 * 현재 환경의 프론트엔드 URL 가져오기
 */
const getFrontendUrl = (): string => {
  // 환경 변수로 명시적으로 설정된 경우
  if (import.meta.env.VITE_FRONTEND_URL) {
    return import.meta.env.VITE_FRONTEND_URL;
  }
  
  // 프로덕션 환경
  if (import.meta.env.PROD) {
    return "https://moo-diary-fe.vercel.app";
  }
  
  // 개발 환경: 현재 브라우저의 origin 사용
  if (typeof window !== "undefined") {
    return window.location.origin;
  }
  
  // 기본값 (서버 사이드 렌더링 등)
  return "http://localhost:5173";
};

/**
 * OAuth 로그인 URL 생성
 * 지원: kakao, google, naver
 * 백엔드에 등록된 기본 리다이렉트 URL 사용
 */
export const getOAuthUrl = (provider: "kakao" | "google" | "naver"): string => {
  const OAUTH_BASE = API_BASE || "https://www.jinwook.shop";
  const oauthUrl = `${OAUTH_BASE}/api/oauth2/authorization/${provider}`;
  
  console.log("🔗 OAuth URL:", oauthUrl);
  
  return oauthUrl;
};

/**
 * OAuth 리다이렉트 URL에서 사용자 ID 추출
 * 예: http://localhost:3000/member/login/present?member=808033069
 * → member 파라미터의 마지막 8자리 뒤 숫자(9)가 사용자 ID
 * 예: http://localhost:3000/member/login/create?member=300063979
 * → member 파라미터의 마지막 8자리 뒤 숫자(9)가 사용자 ID
 */
export const extractUserIdFromRedirect = (memberParam: string): number => {
  // member 파라미터가 8자리 이상인 경우, 마지막 숫자를 추출
  // 예: "808033069" → 9, "300063979" → 9
  if (memberParam.length >= 8) {
    const lastDigit = parseInt(memberParam.slice(-1), 10);
    if (!isNaN(lastDigit)) {
      return lastDigit;
    }
  }
  throw new AuthError("유효하지 않은 사용자 ID입니다.");
};

