import { EmotionType } from './common';

// API 응답 기본 타입
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

// 인증 관련 타입
export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignUpRequest {
  email: string;
  password: string;
  nickname: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: {
    id: number;
    email: string;
    nickname: string;
    profileImage?: string;
  };
}

// 일기 관련 타입
export interface CreateDiaryRequest {
  content: string;
  imageUrl?: string;
}

export interface UpdateDiaryRequest {
  content: string;
  imageUrl?: string;
}

export interface DiaryResponse {
  id: number;
  userId: number;
  content: string;
  imageUrl?: string;
  emotionAnalysis: EmotionAnalysisResponse;
  createdAt: string;
  updatedAt: string;
}

export interface EmotionAnalysisResponse {
  textEmotion: EmotionScoreResponse;
  facialEmotion: EmotionScoreResponse;
  integratedEmotion: EmotionScoreResponse;
  keywords: string[];
  timestamp: string;
}

export interface EmotionScoreResponse {
  emotion: EmotionType;
  score: number;
  confidence: number;
}

export interface EmotionTrendResponse {
  date: string;
  dominantEmotion: EmotionType;
  averageScore: number;
  emotionBreakdown: EmotionScoreResponse[];
}

// 감정 통계 타입
export interface EmotionStatsResponse {
  totalEntries: number;
  averageEmotionScore: number;
  dominantEmotion: EmotionType;
  weeklyTrend: EmotionTrendResponse[];
  monthlyTrend: EmotionTrendResponse[];
}

// 콘텐츠 추천 타입
export interface ContentRecommendationRequest {
  emotionType: EmotionType;
  limit?: number;
}

export interface RecommendedContentResponse {
  id: number;
  type: 'QUOTE' | 'MUSIC' | 'VIDEO' | 'MEDITATION';
  title: string;
  content: string;
  url?: string;
  emotionType: EmotionType;
  createdAt: string;
}

// 커뮤니티 관련 타입
export interface CreatePostRequest {
  content: string;
  emotionType: EmotionType;
  isAnonymous: boolean;
}

export interface UpdatePostRequest {
  content: string;
  emotionType: EmotionType;
}

export interface PostResponse {
  id: number;
  userId: number;
  nickname: string;
  content: string;
  emotionType: EmotionType;
  isAnonymous: boolean;
  likeCount: number;
  commentCount: number;
  createdAt: string;
}

export interface CreateCommentRequest {
  content: string;
  isAnonymous: boolean;
}

export interface CommentResponse {
  id: number;
  postId: number;
  userId: number;
  nickname: string;
  content: string;
  isAnonymous: boolean;
  createdAt: string;
}

export interface PostDetailResponse extends PostResponse {
  comments: CommentResponse[];
}

// 페이지네이션 타입
export interface PaginationRequest {
  page: number;
  size: number;
  sort?: string;
}

export interface PaginationResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
  first: boolean;
  last: boolean;
}
