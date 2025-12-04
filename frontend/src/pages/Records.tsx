import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/common/PageLayout";
import { deleteDiary, type DiaryDtoResponse, api } from "@/lib/apiClient";
import { useUserData } from "@/hooks/useUserData";

// 감정별 온도 및 진행도 매핑
const EMOTION_MAPPING: Record<string, { temperature: string; progress: number }> = {
  HAPPY: { temperature: "38.5", progress: 90 },
  SAD: { temperature: "35.0", progress: 30 },
  ANGRY: { temperature: "39.0", progress: 95 },
  NEUTRAL: { temperature: "36.5", progress: 50 },
  ANXIOUS: { temperature: "37.0", progress: 60 },
  SURPRISED: { temperature: "38.0", progress: 85 },
  DISGUST: { temperature: "34.5", progress: 25 },
};

// 날짜 포맷 변환 함수
const formatDate = (dateString: string | null | undefined): string => {
  if (!dateString) {
    return "날짜 없음";
  }
  
  try {
    const date = new Date(dateString);
    
    // 유효한 날짜인지 확인
    if (Number.isNaN(date.getTime())) {
      // 유효하지 않은 날짜인 경우, 정규식으로 직접 추출 시도
      const dateStr = String(dateString);
      
      // ISO 형식: "2025-12-04T00:00:00" 또는 "2025-12-04"
      const isoMatch = dateStr.match(/(\d{4})-(\d{2})-(\d{2})/);
      if (isoMatch) {
        const [, year, month, day] = isoMatch;
        return `${year}.${month}.${day}`;
      }
      
      // 쉼표 구분 형식: "2025,12,4"
      const commaMatch = dateStr.match(/(\d{4}),\s*(\d{1,2}),\s*(\d{1,2})/);
      if (commaMatch) {
        const [, year, month, day] = commaMatch;
        return `${year}.${String(month).padStart(2, "0")}.${String(day).padStart(2, "0")}`;
      }
      
      // 슬래시 구분 형식: "2025/12/04"
      const slashMatch = dateStr.match(/(\d{4})\/(\d{1,2})\/(\d{1,2})/);
      if (slashMatch) {
        const [, year, month, day] = slashMatch;
        return `${year}.${String(month).padStart(2, "0")}.${String(day).padStart(2, "0")}`;
      }
      
      console.warn("날짜 파싱 실패:", dateString);
      return "날짜 없음";
    }
    
    // 유효한 날짜인 경우 포맷팅
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}.${month}.${day}`;
  } catch (e) {
    console.error("날짜 포맷 변환 예외:", dateString, e);
    return "날짜 없음";
  }
};

// 첫 50자만 추출하는 함수
const getTitleFromContent = (content: string): string => {
  return content.length > 50 ? content.substring(0, 50) + "..." : content;
};

export default function Records() {
  const navigate = useNavigate();
  const { user } = useUserData();
  const [diaries, setDiaries] = useState<DiaryDtoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6;

  // 일기 목록 가져오기
  useEffect(() => {
    const fetchDiaries = async () => {
      if (!user.id) {
        console.warn("사용자 ID가 없습니다.");
        return;
      }

      try {
        console.log(`🔍 Records 페이지: 사용자 ID ${user.id}의 일기 목록 조회 시작`);
        setLoading(true);
        setError(null);
        
        // Records 페이지에서는 최신 일기가 먼저 보이도록 백엔드에서 내림차순 정렬 요청
        const response = await api.get<any>(`/api/diaries/user`, {
          params: {
            page: 0,
            size: 100,
            sort: 'createdAt,desc' // 최신 일기가 먼저
          }
        });
        
        // 백엔드가 Page 객체를 반환할 수 있으므로 content 필드 확인
        const rawData = response.data;
        const data = Array.isArray(rawData) ? rawData : (rawData?.content || []);
        console.log(`📊 Records 페이지: 받아온 일기 개수 = ${data.length}개`);
        console.log("📝 받아온 일기 데이터 (정렬 전):", data);
        
        // 백엔드에서 이미 내림차순으로 정렬되어 오지만, 혹시 모를 경우를 대비해 다시 정렬
        const sortedData = [...data].sort((a, b) => {
          if (!a.createdAt || !b.createdAt) {
            return 0;
          }
          
          const dateA = new Date(a.createdAt).getTime();
          const dateB = new Date(b.createdAt).getTime();
          
          // NaN 체크
          if (Number.isNaN(dateA) || Number.isNaN(dateB)) {
            console.warn("날짜 파싱 실패:", { a: a.createdAt, b: b.createdAt });
            return 0;
          }
          
          // 내림차순 정렬 (최신 날짜가 먼저 = 큰 값이 먼저)
          return dateB - dateA;
        });
        
        console.log("📝 정렬된 일기 데이터 (최신순):", sortedData.map(d => ({ 
          id: d.id, 
          date: d.createdAt,
          formatted: formatDate(d.createdAt)
        })));
        
        setDiaries(sortedData);
      } catch (err) {
        console.error("❌ Records 페이지: 일기 목록 조회 실패:", err);
        
        // 인증 에러인 경우
        if (err instanceof Error && err.message === "로그인이 필요합니다.") {
          setError("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
          setTimeout(() => {
            navigate("/login");
          }, 2000);
        } else {
          setError("일기 목록을 불러올 수 없습니다.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchDiaries();
  }, [user.id, navigate]);

  // 페이지네이션 계산
  const totalPages = Math.ceil(diaries.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentDiaries = diaries.slice(startIndex, endIndex);

  const handleEdit = (id: number) => {
    console.log("수정하기:", id);
    navigate(`/write?id=${id}`);
  };

  const handleDelete = async (id: number) => {
    if (confirm("정말 삭제하시겠습니까?")) {
      try {
        await deleteDiary(id);
        // 삭제 후 목록 새로고침
        setDiaries(diaries.filter((diary) => diary.id !== id));
        alert("일기가 삭제되었습니다.");
      } catch (err) {
        console.error("일기 삭제 실패:", err);
        alert("일기 삭제에 실패했습니다.");
      }
    }
  };

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  };

  const handleWriteNew = () => {
    navigate("/write");
  };

  // 로딩 중
  if (loading) {
    return (
      <PageLayout>
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-[#8E573E] mb-4"></div>
            <p className="text-lg text-[#8E573E]">일기를 불러오는 중...</p>
          </div>
        </div>
      </PageLayout>
    );
  }

  // 에러 발생
  if (error) {
    return (
      <PageLayout>
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <p className="text-lg text-red-500 mb-4">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="px-6 py-2 bg-[#8E573E] text-white rounded-md hover:bg-[#7A4A35] transition-colors"
            >
              다시 시도
            </button>
          </div>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      {/* 메인 타이틀 */}
      <div className="text-center mt-24 mb-8">
        <h1 className="text-5xl font-['jsMath-cmti10'] text-[#8E573E] mb-4">
          mooDiary
        </h1>
        <h2 className="text-3xl font-semibold text-[#8E573E] mb-2">
          지난 일기
        </h2>
        <p className="text-lg text-[#C49B7A]">
          지금까지 기록한 모든 순간들을 되돌아보세요.
        </p>
      </div>

      {/* 통계 */}
      <div className="text-center mb-12">
        <h3 className="text-2xl font-semibold text-[#8E573E]">
          총 {diaries.length} 개의 기록
        </h3>
      </div>

      {/* 일기가 없을 때 */}
      {diaries.length === 0 ? (
        <div className="text-center py-20">
          <p className="text-xl text-gray-500 mb-8">
            아직 작성한 일기가 없습니다.
          </p>
          <button
            onClick={handleWriteNew}
            className="px-8 py-3 bg-gradient-to-r from-[#FF9E0D] to-[#FF5B3A] text-white rounded-lg shadow-lg hover:brightness-110 transition-all"
          >
            첫 일기 작성하기 →
          </button>
        </div>
      ) : (
        <>
          {/* 일기 카드 그리드 */}
          <div className="max-w-[900px] mx-auto mb-12 px-8">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {currentDiaries.map((diary) => {
                const emotion = diary.emotionAnalysis?.integratedEmotion?.emotion || "NEUTRAL";
                const emotionData = EMOTION_MAPPING[emotion] || EMOTION_MAPPING.NEUTRAL;
                // 실제 API에서 받은 온도 값 사용 (없으면 하드코딩된 값 사용)
                const actualTemperature = diary.emotionAnalysis?.integratedEmotion?.score 
                  ? diary.emotionAnalysis.integratedEmotion.score.toFixed(1) 
                  : emotionData.temperature;
                
                return (
                  <div
                    key={diary.id}
                    className="bg-[#FFFEF9] rounded-lg p-6 shadow-md border-4 border-[#FFD66B] relative"
                  >
                    {/* 기록 표시 아이콘 */}
                    <div className="absolute top-4 right-4 flex items-center gap-1">
                      <div className="w-6 h-6 rounded-full bg-green-500 flex items-center justify-center">
                        <span className="text-white text-xs font-bold">✓</span>
                      </div>
                      <span className="text-sm text-gray-600">기록</span>
                    </div>

                    {/* 제목 (첫 50자) */}
                    <h3 className="text-xl font-semibold text-gray-800 mb-2 pr-16">
                      {getTitleFromContent(diary.content)}
                    </h3>
                    <p className="text-sm text-gray-500 mb-4">
                      {formatDate(diary.createdAt)}
                    </p>

                    {/* 내용 미리보기 */}
                    <p className="text-sm text-gray-700 mb-4 line-clamp-3">
                      {diary.content}
                    </p>

                    {/* 감정 키워드 */}
                    {diary.emotionAnalysis?.keywords && diary.emotionAnalysis.keywords.length > 0 && (
                      <div className="mb-4 flex flex-wrap gap-2">
                        {diary.emotionAnalysis.keywords.slice(0, 3).map((keyword, idx) => (
                          <span
                            key={idx}
                            className="px-2 py-1 bg-[#FFF9E6] text-[#8E573E] text-xs rounded-full"
                          >
                            #{keyword}
                          </span>
                        ))}
                      </div>
                    )}

                    {/* 기분 슬라이더 */}
                    <div className="mb-4">
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-xs text-gray-500">
                          감정: {emotion}
                        </span>
                        <span className="text-sm font-semibold text-gray-700">
                          {actualTemperature}°C
                        </span>
                      </div>
                      <div className="w-full h-3 bg-gray-200 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-gradient-to-r from-yellow-400 via-orange-400 to-red-500 rounded-full transition-all"
                          style={{ width: `${emotionData.progress}%` }}
                        />
                      </div>
                    </div>

                    {/* 버튼 그룹 */}
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleEdit(diary.id)}
                        className="flex-1 py-2 px-4 bg-white border-2 border-[#FFD66B] text-gray-700 rounded-md hover:bg-[#FFF9E6] transition-colors text-sm font-medium"
                      >
                        수정하기
                      </button>
                      <button
                        onClick={() => handleDelete(diary.id)}
                        className="flex-1 py-2 px-4 bg-[#FF6B6B] text-white rounded-md hover:bg-[#FF5252] transition-colors text-sm font-medium"
                      >
                        삭제하기
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-2 mb-12">
              {Array.from({ length: Math.min(totalPages, 5) }, (_, i) => {
                const pageNum = i + 1;
                return (
                  <button
                    key={pageNum}
                    onClick={() => handlePageChange(pageNum)}
                    className={`w-8 h-8 rounded ${
                      currentPage === pageNum
                        ? "bg-[#8E573E] text-white font-semibold"
                        : "bg-white text-gray-700 hover:bg-gray-100"
                    } transition-colors`}
                  >
                    {pageNum}
                  </button>
                );
              })}
            </div>
          )}

          {/* 새로운 일기 작성 버튼 */}
          <div className="flex justify-center mb-16">
            <button
              onClick={handleWriteNew}
              className="px-8 py-3 bg-gradient-to-r from-[#FF9E0D] to-[#FF5B3A] text-white rounded-lg shadow-lg hover:brightness-110 transition-all flex items-center gap-2 text-lg font-semibold"
            >
              새로운 일기 작성하기 →
            </button>
          </div>
        </>
      )}
    </PageLayout>
  );
}

