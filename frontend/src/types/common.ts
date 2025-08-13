// 공통 타입 정의
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

export interface PaginationResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

// 감정 관련 타입
export enum EmotionType {
  HAPPY = 'HAPPY',
  SAD = 'SAD',
  ANGRY = 'ANGRY',
  DEPRESSED = 'DEPRESSED',
  CALM = 'CALM',
  EXCITED = 'EXCITED',
  ANXIOUS = 'ANXIOUS',
  NEUTRAL = 'NEUTRAL'
}

export interface EmotionScore {
  emotion: EmotionType;
  score: number;
  confidence: number;
}

export interface EmotionAnalysis {
  textEmotion: EmotionScore;
  facialEmotion: EmotionScore;
  integratedEmotion: EmotionScore;
  keywords: string[];
  timestamp: string;
}

// 사용자 관련 타입
export interface User {
  id: number;
  email: string;
  nickname: string;
  profileImage?: string;
  createdAt: string;
  updatedAt: string;
}

// 일기 관련 타입
export interface DiaryEntry {
  id: number;
  userId: number;
  content: string;
  imageUrl?: string;
  emotionAnalysis: EmotionAnalysis;
  createdAt: string;
  updatedAt: string;
}

// 콘텐츠 추천 타입
export interface RecommendedContent {
  id: number;
  type: 'QUOTE' | 'MUSIC' | 'VIDEO' | 'MEDITATION';
  title: string;
  content: string;
  url?: string;
  emotionType: EmotionType;
  createdAt: string;
}

// 커뮤니티 관련 타입
export interface CommunityPost {
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

export interface CommunityComment {
  id: number;
  postId: number;
  userId: number;
  nickname: string;
  content: string;
  isAnonymous: boolean;
  createdAt: string;
}
