import axios, { AxiosInstance, AxiosError } from "axios";
import type { Recommendation, EmotionData, ContentType, ApiError, DiaryResponse, UserProfile, BookmarkItem, BookmarkWithStats } from "@shared/types";
import { getAccessToken } from "./auth";

/**
 * API Base URL 설정
 */
const getApiBaseUrl = (): string => {
  const envUrl = import.meta.env.VITE_API_URL as string;
  if (envUrl) return envUrl;
  if (import.meta.env.PROD) {
    return "https://www.jinwook.shop";
  }
  return "";
};

const API_BASE = getApiBaseUrl();

export const api: AxiosInstance = axios.create({
  baseURL: API_BASE,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    
    // FormData인 경우 Content-Type 헤더를 제거하여 Axios가 자동으로 multipart/form-data 설정하도록 함
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    console.error("API ERROR Response:", error.response?.data);
    console.error("API ERROR Status:", error.response?.status);
    return Promise.reject(error);
  }
);

// 에러 핸들링 헬퍼 (최상단 1회 정의)
const handleApiError = (error: unknown, defaultMessage: string): never => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string }>;
    // 서버에서 보내준 에러 메시지를 우선적으로 사용
    const serverMessage = typeof axiosError.response?.data === 'string' 
      ? axiosError.response?.data 
      : axiosError.response?.data?.message;
      
    const message = serverMessage || axiosError.message || defaultMessage;
    
    throw {
      message,
      status: axiosError.response?.status,
      code: axiosError.code,
    } as ApiError;
  }
  throw new Error(defaultMessage);
};

// ============================================================================
// [Existing APIs] 기존 페이지에서 사용하는 API (유지)
// ============================================================================

export const getEmotionData = async (): Promise<EmotionData> => {
  try {
    const response = await api.get<EmotionData>("/emotion");
    return response.data;
  } catch (error) { handleApiError(error, "감정 데이터 조회 실패"); }
};

const createRecommendation = async (type: Lowercase<ContentType>): Promise<Recommendation> => {
  try {
    const response = await api.get<Recommendation>(`/recommend/${type}/create`);
    return response.data;
  } catch (error) { handleApiError(error, `${type} 추천 생성 실패`); }
};

export const createBookRecommendation = () => createRecommendation("book"); 
export const createMovieRecommendation = () => createRecommendation("movie");
export const createMusicRecommendation = () => createRecommendation("music");
export const createPoemRecommendation = () => createRecommendation("poem");
export const createWiseSayingRecommendation = () => createRecommendation("wise-saying");

export const getRecommendationList = async (year: number, month: number, contentType: ContentType): Promise<Recommendation[]> => {
  try {
    const response = await api.get<Recommendation[]>("/recommend/read", { params: { year, month, contentType } });
    return response.data;
  } catch (error) { handleApiError(error, "추천 목록 조회 실패"); }
};

export const getRecommendationDetail = async (id: string | number): Promise<Recommendation> => {
  try {
    const response = await api.get<Recommendation>(`/recommend/read/${id}`);
    return response.data;
  } catch (error) { handleApiError(error, "추천 상세 조회 실패"); }
};

export const getUserProfile = async (): Promise<UserProfile> => {
  try {
    const response = await api.get<UserProfile>("/main/user/profile");
    return response.data;
  } catch (error) { handleApiError(error, "사용자 프로필 조회 실패"); }
};

export const getTodayDiary = async (): Promise<DiaryResponse | null> => {
  try {
    const response = await api.get<DiaryResponse>("/main/diary/today");
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 204) return null;
    handleApiError(error, "오늘 일기 조회 실패");
  }
};

export const getRecentDiaries = async (): Promise<DiaryResponse[]> => {
  try {
    const response = await api.get<DiaryResponse[]>("/main/diary/recent");
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) { return []; }
};

export const deleteDiary = async (diaryId: number): Promise<void> => {
  try { 
    await api.delete(`/api/diaries/${diaryId}`); 
  } 
  catch (error) { 
    handleApiError(error, "일기 삭제 실패"); 
  }
};

export const getBookmarksWithStats = async (): Promise<BookmarkWithStats> => {
  try {
    const response = await api.get<BookmarkWithStats>("/bookmarks/registered");
    return response.data;
  } catch (error) { throw error; }
};

export const getAllBookmarks = async (): Promise<BookmarkItem[]> => {
  try {
    const response = await api.get<BookmarkItem[]>("/bookmarks/all");
    return response.data;
  } catch (error) { return []; }
};

export const addBookmark = async (diaryId: number): Promise<void> => {
  try { await api.post(`/bookmarks/${diaryId}`); } 
  catch (error) { handleApiError(error, "북마크 추가 실패"); }
};

export const removeBookmark = async (diaryId: number): Promise<void> => {
  try { await api.delete(`/bookmarks/${diaryId}`); } 
  catch (error) { handleApiError(error, "북마크 삭제 실패"); }
};

// ============================================================================
// [NEW APIs] WriteEdit & Results 페이지 전용 (백엔드 DTO 반영)
// ============================================================================

export interface EmotionScore {
  emotion: string;
  score: number;
  confidence: number;
}

export interface EmotionAnalysisResponse {
  textEmotion: EmotionScore;
  facialEmotion?: EmotionScore;
  integratedEmotion: EmotionScore;
  keywords: string[];
  timestamp: string;
}

export interface DiaryDtoResponse {
  id: number;
  userId: number;
  content: string;
  imageUrl?: string;
  emotionAnalysis?: EmotionAnalysisResponse;
  createdAt: string;
  updatedAt: string;
}

export interface FileUploadResponse {
  filename: string;
  url: string;
  size: number;
}

// 1. 파일 업로드 (경로 수정: /files -> /api/files, 헤더 제거)
export const uploadFile = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    // [중요] 'Content-Type': 'multipart/form-data' 헤더를 제거해야 Axios가 Boundary를 자동 생성함
    const response = await api.post<FileUploadResponse>('/api/files/upload', formData);
    return response.data.url;
  } catch (error) {
    console.error("파일 업로드 실패:", error);
    throw error;
  }
};

// [Helper] 감정을 내용에 포함시키는 함수 (백엔드 분석 유도용)
const appendEmotionToContent = (content: string, emotion?: string | null): string => {
  if (!emotion) return content;
  // AI가 감정을 인식하도록 내용 앞단에 힌트 추가
  return `[Current Mood: ${emotion}] \n${content}`;
};

// 2. 일기 작성 (경로 수정: /diaries -> /api/diaries, 이미지 유무 분기)
export const createDiary = async (userId: number, content: string, imageFile?: File): Promise<DiaryDtoResponse> => {
  try {
    let response;
    if (imageFile) {
      // (A) 이미지 포함 -> Multipart 전송 (POST /api/diaries/with-image)
      const formData = new FormData();
      formData.append('content', content); // @RequestParam
      formData.append('image', imageFile); // @RequestPart

      // Axios가 FormData를 자동으로 multipart/form-data로 처리하도록 함
      // Content-Type 헤더를 명시적으로 설정하지 않음 (boundary 자동 생성)
      // 백엔드는 JWT 토큰에서 userId를 가져오므로 쿼리 파라미터 제거
      response = await api.post<DiaryDtoResponse>('/api/diaries/with-image', formData);
    } else {
      // (B) 텍스트만 -> JSON 전송 (POST /api/diaries)
      // [수정] imageUrl 필드를 아예 생략하거나 빈 문자열로 보냄
      const requestBody = { 
        content, 
        imageUrl: "" // null 대신 빈 문자열 시도 (DTO 호환성)
      };
      
      response = await api.post<DiaryDtoResponse>('/api/diaries', requestBody, {
        params: { userId }
      });
    }
    
    console.log('createDiary 응답:', response.data);
    
    if (!response.data || !response.data.id) {
      console.error('응답 데이터 구조:', response.data);
      throw new Error('서버 응답에 일기 ID가 없습니다.');
    }
    
    return response.data;
  } catch (error) {
    console.error('createDiary 에러:', error);
    // 에러 발생 시 상세 내용을 throw하여 WriteEdit에서 확인 가능하도록 함
    handleApiError(error, '일기 작성 실패');
  }
};

// 3. 일기 수정 (경로 수정: /api/diaries/{diaryId})
export const updateDiary = async (userId: number, diaryId: number | string, content: string, imageUrl?: string): Promise<DiaryDtoResponse> => {
  const requestBody = {
    content,
    imageUrl: imageUrl || "" // null 대신 빈 문자열 처리
  };
  try {
    const response = await api.put<DiaryDtoResponse>(`/api/diaries/${diaryId}`, requestBody, {
      params: { userId }
    });
    
    console.log('updateDiary 응답:', response.data);
    
    if (!response.data || !response.data.id) {
      console.error('응답 데이터 구조:', response.data);
      throw new Error('서버 응답에 일기 ID가 없습니다.');
    }
    
    return response.data;
  } catch (error) {
    console.error('updateDiary 에러:', error);
    handleApiError(error, '일기 수정 실패');
  }
};

// 4. 감정 분석 조회 (경로 수정)
export const getDiaryAnalysis = async (userId: number, diaryId: number | string): Promise<EmotionAnalysisResponse> => {
  try {
    const response = await api.get<EmotionAnalysisResponse>(`/api/diaries/${diaryId}/analysis`, {
      params: { userId }
    });
    return response.data;
  } catch (error) {
    handleApiError(error, '감정 분석 조회 실패');
  }
};

// 5. 일기 상세 조회 (경로 수정)
export const getDiaryById = async (userId: number, diaryId: string | number): Promise<DiaryDtoResponse> => {
  try {
    const response = await api.get<DiaryDtoResponse>(`/api/diaries/${diaryId}`, {
      params: { userId }
    });
    return response.data;
  } catch (error) {
    handleApiError(error, '일기 상세 조회 실패');
  }
};

// 6. 사용자별 일기 목록 조회 (EmoResult용)
// 반환 타입을 DiaryResponse -> DiaryDtoResponse[] 로 수정하여 타입 일치
export const getUserDiaries = async (userId: number): Promise<DiaryDtoResponse[]> => {
  try {
    // 백엔드는 JWT 토큰에서 userId를 가져오므로 경로에 userId를 포함하지 않음
    // /api/diaries/user 엔드포인트 사용 (JWT 토큰 기반)
    const response = await api.get<any>(`/api/diaries/user`, {
      params: {
        page: 0,
        size: 100, // 충분히 큰 값으로 설정하여 모든 일기 가져오기
        sort: 'createdAt,asc' // 오름차순 정렬 (오래된 날짜가 먼저, 최신 날짜가 나중에)
      }
    });
    // 백엔드가 Page 객체(content 필드 등)를 반환하므로 content 필드 확인
    const data = response.data;
    if (Array.isArray(data)) return data;
    if (data && Array.isArray(data.content)) return data.content; // Page 객체 대응
    console.warn("예상하지 못한 응답 형식:", data);
    return [];
  } catch (error) {
    console.error("일기 목록 조회 실패:", error);
    return [];
  }
};

// 7. 사용자 ID Helper
export const getUserId = (): number => {
  const storedId = localStorage.getItem('userId');
  if (storedId) return parseInt(storedId, 10);
  return 1; 
};

// [LEGACY Stubs] WriteEdit 호환성 유지용 (실제 사용은 위 함수들로 대체됨)
export const saveDraft = async (formData: FormData): Promise<any> => { return {}; };
export const analyzeEmotion = async (formData: FormData): Promise<any> => { return {}; };
export const submitDiary = async (formData: FormData): Promise<any> => { return {}; };