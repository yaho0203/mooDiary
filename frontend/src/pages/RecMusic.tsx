import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import basicBookImg from "../assets/basicBookImg.png";
import {
  getEmotionData,
  createBookRecommendation,
  createMovieRecommendation,
  createMusicRecommendation,
  createPoemRecommendation,
} from "../lib/apiClient";
import { CATEGORY_COLORS } from "../constants/colors";
import { PageLayout } from "../components/common/PageLayout";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorDisplay } from "../components/common/ErrorDisplay";
import type { EmotionData, Recommendation } from "../../shared/types";

interface Category {
  id: string;
  label: string;
  icon: string;
  disabled?: boolean;
}

export default function RecMusic() {
  const navigate = useNavigate();
  const [emotionData, setEmotionData] = useState<EmotionData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>("music");
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [currentIndex, setCurrentIndex] = useState<number>(0);
  const [recommendLoading, setRecommendLoading] = useState<boolean>(false);

  const recommendation = recommendations[currentIndex] || null;

  // 감정 데이터 로드 (axios api 사용)
  useEffect(() => {
    const loadEmotionData = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getEmotionData();
        setEmotionData(data || null);
      } catch (err: any) {
        console.error("감정 데이터 로드 실패:", err);
        
        // 401 에러인 경우 (인증 실패)
        if (err?.status === 401) {
          setError("로그인이 필요합니다. 로그인 페이지로 이동해주세요.");
        } else {
          setError(err.message || "감정 데이터를 불러오지 못했습니다.");
        }
      } finally {
        setLoading(false);
      }
    };

    loadEmotionData();
  }, []);

  // 페이지 로드 시 자동으로 음악 추천 가져오기
  useEffect(() => {
    if (emotionData && recommendations.length === 0) {
      handleCategorySelect("music");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [emotionData]);

  // 카테고리 -> 페이지 라우팅 매핑
  const categoryRouteMap: Record<string, string> = {
    book: "/recommendation",
    movie: "/movies",
    music: "/music",
    poem: "/poem",
    quote: "/phrase",
  };

  // 추천 콘텐츠 로드 (여러 개) 또는 페이지 이동
  const handleCategorySelect = async (category: string) => {
    // music 카테고리가 아니면 해당 페이지로 라우팅
    if (category !== "music") {
      const route = categoryRouteMap[category];
      if (route) {
        navigate(route);
      }
      return;
    }

    // music 카테고리인 경우 현재 페이지에서 API 호출
    if (selectedCategory === category && recommendations.length > 0) return;

    setSelectedCategory(category);
    setRecommendLoading(true);
    setError(null);
    setCurrentIndex(0);

    try {
      // 여러 개의 추천을 가져오기 (3개)
      const recommendationPromises = Array(3).fill(null).map(() => createMusicRecommendation());
      const results = await Promise.all(recommendationPromises);
      const validResults = results.filter((r): r is Recommendation => r !== null);
      
      setRecommendations(validResults);
    } catch (err: any) {
      console.error(`추천 콘텐츠 로드 실패 (${category}):`, err);
      setError(err.message || `추천 콘텐츠를 불러오지 못했습니다: ${category}`);
      setRecommendations([]);
    } finally {
      setRecommendLoading(false);
    }
  };

  // 다음 추천으로 이동
  const handleNextRecommendation = () => {
    if (currentIndex < recommendations.length - 1) {
      setCurrentIndex(prev => prev + 1);
    }
  };

  // 이전 추천으로 이동
  const handlePrevRecommendation = () => {
    if (currentIndex > 0) {
      setCurrentIndex(prev => prev - 1);
    }
  };

  const categories: Category[] = [
    { id: "book", label: "책", icon: "📚" },
    { id: "movie", label: "영화", icon: "🎬" },
    { id: "music", label: "음악", icon: "🎵" },
    { id: "poem", label: "시", icon: "📜" },
    { id: "quote", label: "명언", icon: "💭" },
  ];

  const getCategoryColor = (category: string): string => {
    return CATEGORY_COLORS[category] || "from-gray-500 to-gray-600";
  };

  if (loading) {
    return <LoadingSpinner message="감정 분석 중입니다..." />;
  }

  if (error) {
    return (
      <ErrorDisplay 
        error={error} 
        onRetry={() => {
          // 인증 에러인 경우 로그인 페이지로
          if (error.includes("로그인")) {
            window.location.href = "/login";
          } else {
            window.location.reload();
          }
        }} 
      />
    );
  }

  return (
    <PageLayout>

            {/* 메인 콘텐츠 */}
            <section className="flex flex-col items-center mt-12 max-w-[1021px] mx-auto">
              {/* 타이틀 섹션 */}
              <div className="text-center mb-12">
                <div className="flex justify-center">
                  <span className="text-5xl font-['jsMath-cmti10'] text-[#8E573E] font-normal">
                    mooDiary
                  </span>
                </div>
                <div className="flex justify-center mt-6">
                  <span className="text-2xl font-normal font-['Inter'] text-[#8E573E]">
                    당신의 감정에 맞는 특별한 추천
                  </span>
                </div>
                <div className="flex justify-center mt-2">
                  <span className="text-lg font-['Inter'] text-[#FDA54E] max-w-2xl">
                    AI가 분석한 감정을 바탕으로 책, 영화, 음악 등을 추천해드려요!
                  </span>
                </div>
              </div>

              {/* 감정 표시 섹션 */}
              <div
                className={`flex flex-col justify-center items-center w-full max-w-[915px] min-h-[260px] rounded-lg p-8 shadow-lg mb-8 border-2 ${
                  emotionData?.temperature === "따뜻함"
                    ? "bg-gradient-to-br from-green-100 via-emerald-100 to-teal-100 border-green-300"
                    : "bg-gradient-to-br from-green-100 via-emerald-100 to-teal-100 border-green-300"
                }`}
              >
                {emotionData ? (
                  <>
                    <div className="flex flex-col items-center mb-4">
                      <div className="text-7xl mb-4">
                        {emotionData.emoji || "😊"}
                      </div>
                      <h3 className="text-3xl font-semibold text-gray-800 mb-2">
                        오늘의 감정 : {emotionData.emotion || "기쁨"}
                      </h3>
                    </div>
                    <div className="text-center mb-4 max-w-[700px]">
                      <p className="text-gray-700 text-lg">
                        {emotionData.description ||
                          "오늘의 감정이 담은 따뜻한 이야기를 들려드릴게요."}
                      </p>
                    </div>
                    
                    {/* 감정 온도 표시 */}
                    <div className="w-full max-w-[600px] mt-4">
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-sm text-gray-600 font-medium">감정 온도 :</span>
                        <span className="text-base font-semibold text-gray-700">
                          37.5°C
                        </span>
                      </div>
                      <div className="w-full h-5 bg-gray-200 rounded-full overflow-hidden shadow-inner">
                        <div
                          className="h-full bg-gradient-to-r from-yellow-400 via-orange-400 to-red-500 rounded-full transition-all duration-500"
                          style={{ width: "75%" }}
                        />
                      </div>
                    </div>
                  </>
                ) : (
                  <div className="flex items-center justify-center h-full">
                    <p className="text-gray-500">감정 데이터를 불러오는 중입니다...</p>
                  </div>
                )}
              </div>

              {/* 카테고리 버튼들 */}
              <div className="flex flex-wrap justify-center gap-4 w-full max-w-[915px] mb-8">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    onClick={() => handleCategorySelect(category.id)}
                    disabled={recommendLoading || category.disabled}
                    className={`flex items-center justify-center gap-2 px-8 py-3 rounded-lg font-semibold text-white shadow-md transform transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none min-w-[140px] ${
                      selectedCategory === category.id
                        ? "bg-gradient-to-r from-[#FF9E0D] to-[#FF5B3A]"
                        : "bg-gradient-to-r from-[#FFB84D] to-[#FFA033] hover:from-[#FF9E0D] hover:to-[#FF5B3A]"
                    }`}
                  >
                    <span className="text-2xl">{category.icon}</span>
                    <span className="text-lg">{category.label}</span>
                  </button>
                ))}
              </div>

              {/* 추천 콘텐츠 표시 영역 */}
              {recommendLoading ? (
                <div className="mt-8 flex items-center justify-center w-full max-w-[915px]">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#8E573E]"></div>
                  <span className="ml-2 text-gray-600">추천을 불러오는 중입니다...</span>
                </div>
              ) : recommendation ? (
                <div className="w-full max-w-[930px] bg-gradient-to-b from-[#FFFBF2] to-[#FFF3D7] p-8 rounded-lg shadow-lg border-2 border-[#F4D03F]">
                  {selectedCategory === "book" ? (
                    <div className="flex flex-col md:flex-row gap-6">
                      {/* 추천 도서 섹션 */}
                      <div className="flex-1 bg-[#FFFEF5] p-6 rounded-lg shadow-md border border-[#F4D03F]">
                        <div className="flex items-center gap-2 mb-6">
                          <span className="text-2xl">📚</span>
                          <h3 className="text-xl font-bold text-[#7D4D37]">오늘의 추천 도서</h3>
                        </div>
                        <div className="flex justify-center mb-6">
                          <img
                            src={recommendation.imageUrl || basicBookImg}
                            alt={`책 표지: ${recommendation.title || "추천 도서"}`}
                            className="w-[240px] h-[320px] object-cover rounded-lg shadow-lg"
                          />
                        </div>
                        <div className="mt-4 bg-[#FFF9E6] p-4 rounded-md">
                          <p className="text-base text-[#7D4D37] mb-2">
                            <span className="font-bold">제목 :</span> {recommendation.title || "추천 도서"}
                          </p>
                        </div>
                      </div>
                      {/* 작품 설명 섹션 */}
                      <div className="flex-1 bg-[#FFFEF5] p-6 rounded-lg shadow-md border border-[#F4D03F]">
                        <div className="flex items-center gap-2 mb-6">
                          <span className="text-2xl">📖</span>
                          <h3 className="text-xl font-bold text-[#7D4D37]">작품 설명</h3>
                        </div>
                        <div className="text-base text-[#7D4D37] leading-relaxed space-y-3">
                          {recommendation.content ? (
                            <div className="whitespace-pre-line">
                              {recommendation.content.split('\n').map((line, idx) => (
                                <p key={idx} className="mb-2">• {line}</p>
                              ))}
                            </div>
                          ) : (
                            <>
                              <p>• 작가 : 앙투안 드 생텍쥐페리</p>
                              <p>• 어린 왕자는 작은 별에서 온 소년으로, 다양한 별들을 여행하며 어린 인물들을 만나며 여행</p>
                              <p>• 어린 왕자의 관점, 하염없, 집착 등 인간의 여러 식욕을 상징하는 어른들을 통해 삶의 본질을 묻는 작지</p>
                              <p>• 사구에 도착한 어린 왕자는 여우와의 만남을 통해 '길들임'의 의미 사랑, 관계의 중요함 학습</p>
                              <p>• 어린 왕자는 잃어버린 대한 사랑과 책임감을 다시 확인한 지구를 떠나 사라짐</p>
                              <p>• 따뜻한 화풍로 보낸 사랑에게 추억되는 도서</p>
                            </>
                          )}
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="w-full p-6 bg-[#FFFEF5] rounded-lg shadow-md border border-[#F4D03F]">
                      <div className="flex items-center gap-2 mb-4">
                        <span className="text-2xl">{categories.find((cat) => cat.id === selectedCategory)?.icon}</span>
                        <h3 className="text-xl font-bold text-[#7D4D37]">추천 {categories.find((cat) => cat.id === selectedCategory)?.label}</h3>
                      </div>
                      {recommendation.imageUrl && (
                        <div className="flex justify-center mb-4">
                          <img
                            src={recommendation.imageUrl}
                            alt={`추천 ${selectedCategory}: ${recommendation.title || "추천 콘텐츠"}`}
                            className="w-[240px] h-[320px] object-cover rounded-lg shadow-lg"
                          />
                        </div>
                      )}
                      <div className="bg-[#FFF9E6] p-4 rounded-md mb-4">
                        <p className="text-base font-bold text-[#7D4D37] mb-2">제목: {recommendation.title || "알 수 없음"}</p>
                      </div>
                      <p className="text-base text-[#7D4D37] leading-relaxed whitespace-pre-line">{recommendation.content || "추천 콘텐츠가 준비되었습니다."}</p>
                    </div>
                  )}
                </div>
              ) : (
                <div className="w-full max-w-[915px] p-6 bg-white rounded-lg shadow-lg">
                  <p className="text-gray-500">추천 콘텐츠를 선택해주세요.</p>
                </div>
              )}

              {/* 인디케이터와 다음 버튼 */}
              {recommendations.length > 0 && (
                <div className="flex flex-col items-center gap-6 mt-8 w-full max-w-[930px]">
                  {/* 인디케이터 */}
                  <div className="flex items-center gap-2">
                    {recommendations.map((_, index) => (
                      <button
                        key={index}
                        onClick={() => setCurrentIndex(index)}
                        className={`w-3 h-3 rounded-full transition-all duration-300 ${
                          index === currentIndex
                            ? "bg-[#FF9E0D] w-8"
                            : "bg-gray-300 hover:bg-gray-400"
                        }`}
                        aria-label={`${index + 1}번째 추천으로 이동`}
                      />
                    ))}
                  </div>

                  {/* 다음/이전 버튼 */}
                  <div className="flex items-center gap-4">
                    {currentIndex > 0 && (
                      <button
                        onClick={handlePrevRecommendation}
                        className="px-8 py-3 rounded-lg bg-gradient-to-r from-gray-400 to-gray-500 text-white font-semibold text-lg hover:brightness-110 transition-all shadow-lg"
                      >
                        &lt;&lt; 이전 추천 콘텐츠 보러가기
                      </button>
                    )}
                    
                    {currentIndex < recommendations.length - 1 && (
                      <button
                        onClick={handleNextRecommendation}
                        className="px-8 py-3 rounded-lg bg-gradient-to-r from-[#FF9E0D] to-[#FF5B3A] text-white font-semibold text-lg hover:brightness-110 transition-all shadow-lg"
                      >
                        &gt;&gt; 다음 추천 콘텐츠 보러가기
                      </button>
                    )}
                  </div>
                </div>
              )}
            </section>
    </PageLayout>
  );
}
