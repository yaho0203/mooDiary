import { useEffect, useState } from "react";
import Header from "../components/layout/Header";
import { useNavigate } from "react-router-dom";
import { getUserDiaries, getUserId, type DiaryDtoResponse } from "../lib/apiClient";

// [추가] 백엔드 영문 감정 -> 프론트엔드 한글 매핑
// 백엔드는 Enum(영어)으로 저장해야 오류가 안 나므로, 표시할 때만 변환합니다.
const EMOTION_TRANSLATION: Record<string, string> = {
  HAPPY: "기쁨",
  EXCITED: "흥분",
  CALM: "평온",
  ANXIOUS: "불안",
  ANGRY: "화남",
  SAD: "우울",
  // 예외 처리 (매핑되지 않은 감정이 올 경우 '평온'으로 처리)
  NEUTRAL: "평온",
  SURPRISED: "흥분",
};

// [수정] 요청하신 6가지 감정 순서 및 색상 정의
const DISTRIBUTION_ORDER = ["기쁨", "흥분", "평온", "불안", "화남", "우울"];
// 색상 매칭 (순서대로): 노랑(기쁨), 주황(흥분), 초록(평온), 보라(불안), 빨강(화남), 파랑(우울)
const DISTRIBUTION_COLORS = ["#FACC15", "#FB923C", "#A3E635", "#C084FC", "#F87171", "#60A5FA"];

export default function EmoResult() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [stats, setStats] = useState({
    avgTemp: 36.5,
    mostEmotion: "분석 중...",
    totalDiaries: 0,
    totalTemp: 36.5,
  });

  const [trendData, setTrendData] = useState<Array<{ date: string; x: number; y: number; value: number }>>([]);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [weeklyBars, setWeeklyBars] = useState<number[]>([]); 
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [weeklyRange, setWeeklyRange] = useState<{ min: number; max: number }>({ min: 36, max: 38 });
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [weeklyTemps, setWeeklyTemps] = useState<number[]>([]); 
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [hoveredBar, setHoveredBar] = useState<number | null>(null);
  const [distribution, setDistribution] = useState<Record<string, number>>({});
  const [diariesData, setDiariesData] = useState<DiaryDtoResponse[]>([]);

  useEffect(() => {
    let mounted = true;

    const processData = (diaries: DiaryDtoResponse[]) => {
      // 1. 데이터 매핑 및 한글 변환
      const points = diaries
        .map((d) => {
          
          const analysis = d.emotionAnalysis?.integratedEmotion;
          // 점수가 없으면 기본값 36.5, 숫자가 아닌 경우도 방어
          const rawScore = analysis?.score;
          // null, undefined, NaN 모두 처리
          let temp = 36.5;
          
          // 감정 영문 -> 한글 변환
          const rawEmotion = analysis?.emotion || "CALM";
          const emotionUpper = rawEmotion.toUpperCase();
          
          if (rawScore != null && typeof rawScore === 'number' && !isNaN(rawScore) && isFinite(rawScore)) {
            // 감정 타입에 따라 다른 온도 변환 적용 (백엔드와 동일한 로직)
            if (rawScore >= 0 && rawScore <= 100) {
              if (emotionUpper === "ANGRY" || emotionUpper === "FRUSTRATED" || rawEmotion === "분노" || rawEmotion === "화남" || rawEmotion === "좌절") {
                // 화남: 점수에 따라 38-40도 (화가 나면 온도가 올라감)
                // 0-100점을 38-40도로 매핑 (화남은 항상 높은 온도)
                if (rawScore >= 80) {
                  temp = 36.5 + ((rawScore - 80) / 20.0) * 3.5; // 80점=36.5도, 100점=40도
                } else {
                  // 80점 미만이어도 화남 감정이면 최소 38도 이상으로 설정
                  // 0-80점을 38-36.5도로 매핑 (하지만 최소 38도)
                  temp = Math.max(38.0, 36.5 + (rawScore / 80.0) * 0.0); // 최소 38도
                }
              } else if (emotionUpper === "SAD" || emotionUpper === "DEPRESSED" || rawEmotion === "슬픔" || rawEmotion === "우울") {
                // 슬픔: 0-30점 → 34-36.5도 (슬플 때 34도까지 내려감)
                if (rawScore <= 30) {
                  temp = 36.5 - ((30 - rawScore) / 30.0) * 2.5; // 0점=34도, 30점=36.5도
                } else {
                  temp = 36.5; // 30점 초과면 기본값
                }
              } else if (emotionUpper === "CALM" || emotionUpper === "NEUTRAL" || emotionUpper === "HAPPY" || emotionUpper === "JOYFUL" || emotionUpper === "SATISFIED" || 
                         rawEmotion === "평온" || rawEmotion === "중립" || rawEmotion === "행복" || rawEmotion === "기쁨" || rawEmotion === "만족") {
                // 평온/기분 좋음: 항상 36.5도 고정
                temp = 36.5;
              } else {
                // 기타 감정: 일반 변환 (30.5 + (score/100) * 12)
                temp = 30.5 + (rawScore / 100.0) * 12.0;
              }
            } else if (rawScore >= 30 && rawScore <= 42) {
              // 이미 온도 형식인 경우 그대로 사용
              temp = rawScore;
            } else {
              // 범위를 벗어난 경우 기본값 사용
              temp = 36.5;
            }
          }
          
          // 평온/중립 감정이면 강제로 36.5도로 설정 (점수와 무관하게)
          if (emotionUpper === "CALM" || emotionUpper === "NEUTRAL" || rawEmotion === "평온" || rawEmotion === "중립") {
            temp = 36.5;
          }
          
          // 화남/분노 감정이면 최소 38도 이상으로 설정 (화가 나면 온도가 올라감)
          if (emotionUpper === "ANGRY" || emotionUpper === "FRUSTRATED" || rawEmotion === "분노" || rawEmotion === "화남" || rawEmotion === "좌절") {
            if (temp < 38.0) {
              temp = 38.0; // 화남은 최소 38도
            }
            // 점수가 없거나 유효하지 않은 경우에도 화남이면 38도로 설정
            if (rawScore == null || Number.isNaN(rawScore) || !Number.isFinite(rawScore)) {
              temp = 38.0;
            }
          }
          
          const emotion = EMOTION_TRANSLATION[rawEmotion] || "평온";
          
          // createdAt이 배열 형식인 경우 처리
          let dateValue = d.createdAt;
          if (Array.isArray(d.createdAt) && d.createdAt.length >= 3) {
            // 배열 형식: [2025, 12, 4, 19, 30, 38, 671545000] -> "2025-12-04T19:30:38"
            const year = d.createdAt[0];
            const month = d.createdAt[1];
            const day = d.createdAt[2];
            const hour = d.createdAt[3] || 0;
            const minute = d.createdAt[4] || 0;
            const second = d.createdAt[5] || 0;
            dateValue = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
          }
          
          return {
            date: dateValue,
            temp,
            emotion,
          };
        })
        .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

      if (points.length === 0) {
        // 데이터 없을 시 초기화
        if (mounted) {
            setStats({ avgTemp: 36.5, mostEmotion: "기록 없음", totalDiaries: 0, totalTemp: 0 });
            setTrendData([]);
        }
        return;
      }

      // 2. 통계 계산
      const totalTemp = points.reduce((acc, curr) => acc + curr.temp, 0);
      const avgTemp = points.length > 0 
        ? Number((totalTemp / points.length).toFixed(1))
        : 36.5; // 데이터가 없으면 기본값 36.5
      
      const counts: Record<string, number> = {};
      // 요청하신 6가지 키로 초기화 (0건이어도 그래프에 표시되도록)
      DISTRIBUTION_ORDER.forEach(key => counts[key] = 0);

      points.forEach(p => { 
          // 매핑된 한글 감정이 우리가 원하는 6가지 안에 있으면 카운트
          if (counts[p.emotion] !== undefined) {
              counts[p.emotion]++;
          } else {
              // 혹시 다른 감정이면 '평온' 등에 합치거나 무시
              counts["평온"]++;
          }
      });
      
      let modeEmotion = "-";
      let maxCount = -1;
      for (const [k, v] of Object.entries(counts)) {
        if (v > maxCount) { maxCount = v; modeEmotion = k; }
      }

      setStats({
        avgTemp,
        mostEmotion: modeEmotion,
        totalDiaries: points.length,
        totalTemp,
      });
      setDistribution(counts);

      // 3. Trend Line Chart 데이터 생성
      const tempsOnly = points.map(p => p.temp).filter(t => !isNaN(t) && isFinite(t));
      if (tempsOnly.length === 0) {
        if (mounted) {
          setTrendData([]);
        }
        return;
      }
      
      const BASE_TEMP = 36.5; // 기준 온도 (중앙값)
      const actualMinT = Math.min(...tempsOnly);
      const actualMaxT = Math.max(...tempsOnly);
      
      // 36.5도를 중심으로 대칭 범위 설정
      const distFromBase = Math.max(
        Math.abs(actualMaxT - BASE_TEMP),
        Math.abs(BASE_TEMP - actualMinT)
      );
      
      // 최소 범위 보장 (너무 좁으면 일직선이 되는 것 방지)
      const minRange = 3;
      const range = Math.max(distFromBase * 2, minRange);
      
      let minT = BASE_TEMP - range / 2;
      let maxT = BASE_TEMP + range / 2;

      const mapTempToY = (t: number) => {
        const top = 20;    // 위쪽 (높은 온도)
        const bottom = 180; // 아래쪽 (낮은 온도)
        const midY = (top + bottom) / 2; // 중앙 Y 좌표 (36.5도 위치)
        if (maxT === minT) return midY;
        const ratio = (t - minT) / (maxT - minT); // 0 (minT) ~ 1 (maxT)
        if (isNaN(ratio)) return midY;
        // 온도가 높을수록 위로 (y값이 작아짐), 낮을수록 아래로 (y값이 커짐)
        return Math.round(bottom - ratio * (bottom - top));
      };

      const widthLeft = 50;
      const widthRight = 950;
      const n = points.length;
      
      // 유효한 온도 값만 필터링
      const validPoints = points.filter(p => !isNaN(p.temp) && isFinite(p.temp));
      const validN = validPoints.length;
      
      const generatedTrend = validPoints.map((p, i) => {
        // x좌표 계산 시 n=1일 때 0으로 나누기 방지
        const x = validN <= 1 ? (widthLeft + widthRight) / 2 : Math.round(widthLeft + (i / (validN - 1)) * (widthRight - widthLeft));
        const y = mapTempToY(p.temp);
        
        // 날짜 파싱 - 배열 형식 우선 처리
        let mmdd = '--';
        if (p.date) {
          // 배열 형식인 경우: [2025, 12, 4, 19, 30, 38, 671545000]
          if (Array.isArray(p.date) && p.date.length >= 3) {
            const month = p.date[1];
            const day = p.date[2];
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
              mmdd = `${String(month).padStart(2, '0')}/${String(day).padStart(2, '0')}`;
            }
          } else {
            // 문자열 형식인 경우
            const dateStr = String(p.date);
            // ISO 형식에서 직접 추출: "2025-01-15T10:30:00" -> "01/15"
            const isoMatch = dateStr.match(/(\d{4})-(\d{2})-(\d{2})/);
            if (isoMatch && isoMatch[2] && isoMatch[3]) {
              mmdd = `${isoMatch[2]}/${isoMatch[3]}`;
            } else {
              // Date 객체로 파싱 시도
              const d = new Date(p.date);
              const timestamp = d.getTime();
              if (!Number.isNaN(timestamp) && timestamp > 0) {
                const month = d.getMonth() + 1;
                const day = d.getDate();
                if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                  mmdd = `${String(month).padStart(2, '0')}/${String(day).padStart(2, '0')}`;
                }
              }
            }
          }
        }
        
        return { 
            date: mmdd, 
            x: isNaN(x) || !isFinite(x) ? 500 : x, 
            y: isNaN(y) || !isFinite(y) ? 100 : y, 
            value: p.temp 
        };
      });
      setTrendData(generatedTrend);

      // 4. 주간 바 데이터 (Weekly Bars) - NaN 방지
      const buckets = Array.from({ length: 4 }, (_, i) => {
        const startIdx = Math.floor((i * n) / 4);
        const endIdx = Math.floor(((i + 1) * n) / 4);
        return points.slice(startIdx, endIdx);
      });
      
      const weeks = buckets.map(bucket => {
        if (!bucket.length) return 0; // 데이터 없으면 0 처리
        const sum = bucket.reduce((s, p) => s + p.temp, 0);
        return sum / bucket.length;
      });

      // 바 높이 계산
      const barMin = 30;
      const barMax = 170;
      
      // 유효한 데이터만 필터링해서 min/max 계산
      const validWeeks = weeks.filter(t => t > 0);
      const minBarT = validWeeks.length ? Math.min(...validWeeks) : 0;
      const maxBarT = validWeeks.length ? Math.max(...validWeeks) : 100;
      
      const barHeights = weeks.map(t => {
        if (t === 0) return barMin; // 데이터 없으면 최소 높이
        if (maxBarT === minBarT) return (barMin + barMax) / 2;
        const ratio = (t - minBarT) / (maxBarT - minBarT);
        if (isNaN(ratio)) return barMin;
        return Math.round(barMin + ratio * (barMax - barMin));
      });

      setWeeklyBars(barHeights);
      setWeeklyTemps(weeks);
      setWeeklyRange({ min: minBarT, max: maxBarT });
    };

    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const userId = getUserId();
        const diaries = await getUserDiaries(userId);
        
        if (mounted) {
          if (diaries && diaries.length > 0) {
            setDiariesData(diaries);
            processData(diaries);
          } else {
            setDiariesData([]);
            setTrendData([]); // 데이터 없음 처리
          }
        }
      } catch (e: any) {
        console.error("EmoResult load error:", e);
        if (mounted) setError("데이터를 불러오는데 실패했습니다.");
      } finally {
        if (mounted) setLoading(false);
      }
    };

    load();

    return () => { mounted = false; };
  }, []);

  // 도넛 차트 계산 로직
  const distributionTotal = Object.values(distribution).reduce((a, b) => a + b, 0) || 1;
  const mostEmotionPercent = distributionTotal > 0 
    ? Math.round(((distribution[stats.mostEmotion] || 0) / distributionTotal) * 100)
    : 0; // 데이터가 없으면 0%
  const CIRCUMFERENCE = 2 * Math.PI * 120;
  
  const segments = (() => {
    const total = distributionTotal || 1;
    // 데이터가 하나라도 있는지 확인
    const hasData = Object.values(distribution).some((v) => v > 0);
    let acc = 0;
    
    return DISTRIBUTION_ORDER.map((key, i) => {
      const count = hasData ? (distribution[key] || 0) : 0; // 데이터 없으면 0 처리
      
      // 데이터가 아예 없으면 회색 링 등을 보여주기 위해 균등 분배할 수도 있지만,
      // 여기서는 0으로 처리하되 strokeWidth를 유지
      const frac = total > 0 ? count / total : 0;
      const dash = Math.max(0.001, frac * CIRCUMFERENCE);
      const offset = -acc;
      acc += dash;
      return { key, color: DISTRIBUTION_COLORS[i], dash, offset, hasData };
    });
  })();

  return (
    <div className="flex justify-center w-full font-sans bg-white">
      <div 
        className="w-[1217px] min-h-screen flex flex-col"
        style={{ background: "linear-gradient(90deg, #FFEAB1 7.55%, #FFDED3 121.31%)" }}
      >
        <div className="mt-12"><Header /></div>

        <main className="flex flex-col px-16 py-12 gap-12">
          
          <section className="flex flex-col gap-2">
            <h1 className="text-[#8E573E] text-4xl font-bold font-['Inter']">감정 분석 📊</h1>
            <div className="flex items-center gap-4">
              <p className="text-[#8E573E]/50 text-xl font-normal font-['Inter']">당신의 감정 패턴과 변화를 분석해보세요.</p>
            </div>
          </section>

          <section className="grid grid-cols-3 gap-8">
            <StatCard title="최근 평균 온도" value={stats.avgTemp} unit="°C" desc="최근 작성한 일기 기준" icon={<SmileIcon />} />
            <StatCard title="가장 많은 감정" value={stats.mostEmotion} desc={`전체 비율 중 ${mostEmotionPercent}%`} icon={<SmileIcon />} />
            <StatCard title="전체 일기" value={stats.totalDiaries} desc="누적 작성 일기 수" icon={<SmileIcon />} />
          </section>

          {/* 감정 온도 분포 */}
          <section className="bg-[#FFFBF2]/50 rounded-xl p-8 border-2 border-[#FFD900] shadow-sm">
            <h2 className="text-[#8E573E] text-2xl font-semibold mb-8 flex items-center gap-2"><SmileIcon small /> 감정 온도 분포</h2>
            <div className="flex flex-col justify-center items-center gap-10">
                <div className="relative w-72 h-72">
                  <svg viewBox="0 0 288 288" className="w-full h-full transform -rotate-90">
                    {/* 배경 원 (데이터 없을 때 보임) */}
                    <circle cx={144} cy={144} r={120} fill="none" stroke="#E5E7EB" strokeWidth={24} />
                    
                    {segments.map((s) => (
                      <circle key={s.key} cx={144} cy={144} r={120} fill="none" stroke={s.color} strokeWidth={24} strokeLinecap="round" strokeDasharray={`${s.dash} ${Math.max(0.001, CIRCUMFERENCE - s.dash)}`} strokeDashoffset={s.offset} />
                    ))}
                  </svg>
                  <div className="absolute inset-0 flex flex-col items-center justify-center">
                    <span className="text-lg font-bold text-gray-800 mb-1 font-['Inter']">Avg</span>
                    <span className="text-5xl font-black text-black tracking-tight font-['Inter']">{stats.avgTemp}°C</span>
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-x-16 gap-y-4">
                  {DISTRIBUTION_ORDER.map((key, i) => (
                    <LegendItem key={key} color={`bg-[${DISTRIBUTION_COLORS[i]}]`} label={key} value={distributionTotal > 0 ? ((distribution[key] || 0) / distributionTotal * 100) : 0} />
                  ))}
                </div>
            </div>
          </section>

          {/* 기분 변화 추이 */}
          <section className="bg-[#FFFBF2]/50 rounded-xl p-8 border-2 border-[#FFD900]">
            <h2 className="text-[#8E573E] text-2xl font-semibold mb-6 flex items-center gap-2"><SmileIcon small /> 기분 변화 추이</h2>
            <div className="w-full h-64 relative flex items-end p-4 pl-12">
                {trendData.length > 0 ? (
                  <svg className="absolute inset-0 w-full h-full p-10 pl-16 overflow-visible" viewBox="0 0 1000 200" preserveAspectRatio="none">
                    <line x1="0" y1="0" x2="0" y2="200" stroke="#8E573E" strokeWidth="2" />
                    <line x1="0" y1="200" x2="1000" y2="200" stroke="#8E573E" strokeWidth="2" />
                    {/* Y축 레이블 (온도 값) - 위에서 아래로: 높은 온도부터 낮은 온도까지, 중앙은 36.5도 */}
                    {(() => {
                      const tempsOnly = trendData.map(p => p.value).filter(v => !isNaN(v) && isFinite(v));
                      if (tempsOnly.length === 0) return null;
                      
                      const BASE_TEMP = 36.5;
                      const actualMinT = Math.min(...tempsOnly);
                      const actualMaxT = Math.max(...tempsOnly);
                      
                      // 36.5도를 중심으로 대칭 범위 설정
                      const distFromBase = Math.max(
                        Math.abs(actualMaxT - BASE_TEMP),
                        Math.abs(BASE_TEMP - actualMinT)
                      );
                      const minRange = 3;
                      const range = Math.max(distFromBase * 2, minRange);
                      
                      let minT = BASE_TEMP - range / 2;
                      let maxT = BASE_TEMP + range / 2;
                      
                      const top = 20;    // 위쪽 (높은 온도)
                      const bottom = 180; // 아래쪽 (낮은 온도)
                      const midY = (top + bottom) / 2; // 중앙 Y 좌표 (36.5도 위치)
                      const steps = 5;
                      
                      return Array.from({ length: steps + 1 }, (_, i) => {
                        const ratio = i / steps; // 0 (위) ~ 1 (아래)
                        // 위에서 아래로: maxT부터 minT까지
                        const temp = maxT - (ratio * range);
                        const y = top + (ratio * (bottom - top));
                        const displayTemp = (isNaN(temp) || !isFinite(temp)) ? '--' : temp.toFixed(1);
                        return (
                          <text key={i} x="-10" y={y} textAnchor="end" fill="#8E573E" className="text-xs" style={{ fontSize: '12px' }}>
                            {displayTemp}°C
                          </text>
                        );
                      });
                    })()}
                    {/* Trend Path: 데이터가 1개일 경우 점만 찍히도록 처리 */}
                    <path 
                      d={trendData.length > 1 
                          ? `M ${trendData[0].x} ${trendData[0].y} ` + trendData.slice(1).map(p => `L ${p.x} ${p.y}`).join(' ') 
                          : ""} 
                      fill="none" 
                      stroke="#8E573E" 
                      strokeWidth="3" 
                    />
                    {trendData.map((point, index) => {
                      const displayValue = (isNaN(point.value) || !isFinite(point.value)) ? '--' : point.value.toFixed(1);
                      // 날짜가 없거나 '--'인 경우 다시 파싱 시도
                      let displayDate = point.date;
                      if (!displayDate || displayDate === '--') {
                        // 원본 데이터에서 다시 찾아서 파싱
                        const originalDiary = diariesData.find(d => {
                          const analysis = d.emotionAnalysis?.integratedEmotion;
                          const rawScore = analysis?.score;
                          // 같은 온도로 매칭 시도 (약간의 오차 허용)
                          return Math.abs((rawScore || 0) - point.value) < 0.5;
                        });
                        if (originalDiary?.createdAt) {
                          // 배열 형식인 경우
                          if (Array.isArray(originalDiary.createdAt) && originalDiary.createdAt.length >= 3) {
                            const month = originalDiary.createdAt[1];
                            const day = originalDiary.createdAt[2];
                            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                              displayDate = `${String(month).padStart(2, '0')}/${String(day).padStart(2, '0')}`;
                            }
                          } else {
                            // 문자열 형식인 경우
                            const dateStr = String(originalDiary.createdAt);
                            const isoMatch = dateStr.match(/(\d{4})-(\d{2})-(\d{2})/);
                            if (isoMatch && isoMatch[2] && isoMatch[3]) {
                              displayDate = `${isoMatch[2]}/${isoMatch[3]}`;
                            } else {
                              const d = new Date(originalDiary.createdAt);
                              if (!Number.isNaN(d.getTime())) {
                                const month = d.getMonth() + 1;
                                const day = d.getDate();
                                displayDate = `${String(month).padStart(2, '0')}/${String(day).padStart(2, '0')}`;
                              }
                            }
                          }
                        }
                      }
                      if (!displayDate || displayDate === '--') {
                        displayDate = '--';
                      }
                      return (
                        <g key={index}>
                          <circle cx={point.x} cy={point.y} r="6" fill="#8E573E" />
                          <text x={point.x} y="230" textAnchor="middle" fill="#8E573E" className="text-xs font-bold" style={{ fontSize: '18px' }}>{displayDate}</text>
                          <text x={point.x} y={point.y - 15} textAnchor="middle" fill="#8E573E" className="text-xs" style={{ fontSize: '10px' }}>{displayValue}°C</text>
                        </g>
                      );
                    })}
                  </svg>
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-gray-400">
                    데이터가 충분하지 않습니다.
                  </div>
                )}
            </div>
          </section>

          {/* 하단 CTA */}
          <section className="mt-8 bg-gradient-to-b from-orange-200 to-orange-100 rounded-xl p-8 border-2 border-[#FFD900] flex flex-col items-center gap-8">
            <h3 className="text-[#8E573E] text-3xl font-medium">더 많은 추억을 만들어보세요.</h3>
            <div className="flex gap-8">
              <ActionButton label="새 일기 작성하기" onClick={() => navigate('/write')} primary />
              <ActionButton label="임시저장 페이지" onClick={() => navigate('/drafts')} icon="save" />
              <ActionButton label="지난 분석 보기" onClick={() => navigate('/records')} icon="search" />
            </div>
          </section>

        </main>
      </div>
    </div>
  );
}

// ... 서브 컴포넌트 (디자인 유지) ...
function StatCard({ title, value, unit, desc, icon }: any) {
  // NaN 체크 및 기본값 처리
  const displayValue = (typeof value === 'number' && (isNaN(value) || !isFinite(value))) 
    ? (unit === '°C' ? '36.5' : '0') 
    : value;
  
  return (
    <div className="bg-[#FFFBF2] rounded-xl p-6 border-2 border-[#FFD900] flex flex-col items-center text-center shadow-md-custom">
       <div className="mb-4">{icon}</div>
       <div className="text-[#8E573E] text-4xl font-semibold mb-2">{displayValue}<span className="font-normal">{unit}</span></div>
       <div className="text-[#8E573E]/70 text-xl mb-4">{title}</div>
       <div className="text-orange-400 text-sm">{desc}</div>
    </div>
  );
}

function LegendItem({ color, label, value }: { color: string, label: string, value?: number }) {
  return (
    <div className="flex items-center gap-3">
      <div className="w-4 h-4 rounded-full ring-2 ring-white shadow-sm" style={{ backgroundColor: color.replace('bg-[', '').replace(']', '') }}></div>
      <span className="text-xl font-bold text-gray-700">{label}</span>
      {typeof value === 'number' && <span className="text-sm text-[#8E573E] ml-2">{Math.round(value)}%</span>}
    </div>
  );
}

function ActionButton({ label, onClick, primary, icon }: any) {
  return (
    <button onClick={onClick} className={`w-64 h-14 rounded-xl flex items-center justify-center gap-3 text-xl font-medium transition-all ${primary ? 'bg-gradient-to-r from-[#FF9E0D] to-[#FF5B3A] text-white hover:brightness-110 shadow-md' : 'bg-white border-2 border-orange-300 text-[#8E573E] hover:bg-orange-50'}`}> 
      {icon && <span className="text-lg">📄</span>} {label}
    </button>
  );
}

function SmileIcon({ small }: { small?: boolean }) {
  const size = small ? 30 : 40;
  return (
    <svg width={size} height={size} viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M20.0002 36.6668C29.2049 36.6668 36.6668 29.2049 36.6668 20.0002C36.6668 10.7954 29.2049 3.3335 20.0002 3.3335C10.7954 3.3335 3.3335 10.7954 3.3335 20.0002C3.3335 29.2049 10.7954 36.6668 20.0002 36.6668Z" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M13.3335 23.333C13.3335 23.333 15.8335 26.6663 20.0002 26.6663C24.1668 26.6663 26.6668 23.333 26.6668 23.333" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M15 15.001H15.0167" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M25 15.001H25.0167" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}