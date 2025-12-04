import React, { useState, useRef, useEffect } from 'react';
import { PageLayout } from '../components/common/PageLayout'; 
import { useNavigate } from 'react-router-dom';
// [수정] 수정된 API 함수들 import
import { 
  createDiary, 
  updateDiary, 
  uploadFile, 
  getDiaryAnalysis, 
  getUserId,
  type DiaryDtoResponse,
  type EmotionAnalysisResponse 
} from '../lib/apiClient';

interface LoadingState {
  save: boolean;
  analyze: boolean;
  complete: boolean;
}

interface EmotionItem {
  id: string;
  label: string;
  boxTop: string;
  boxLeft: string;
  iconTop: string;
  iconLeft: string;
  labelTop: string;
  labelLeft: string;
}

const EMOTIONS: EmotionItem[] = [
  { id: 'HAPPY', label: '기쁨', boxLeft: '507px', boxTop: '68px', iconLeft: '590px', iconTop: '79px', labelLeft: '572px', labelTop: '98px' },
  { id: 'CALM', label: '평온', boxLeft: '507px', boxTop: '140px', iconLeft: '590px', iconTop: '152px', labelLeft: '572px', labelTop: '171px' },
  { id: 'ANGRY', label: '화남', boxLeft: '507px', boxTop: '212px', iconLeft: '590px', iconTop: '224px', labelLeft: '572px', labelTop: '243px' },
  { id: 'EXCITED', label: '흥분', boxLeft: '701px', boxTop: '68px', iconLeft: '784px', iconTop: '80px', labelLeft: '766px', labelTop: '99px' },
  { id: 'ANXIOUS', label: '불안', boxLeft: '701px', boxTop: '140px', iconLeft: '784px', iconTop: '152px', labelLeft: '766px', labelTop: '171px' },
  { id: 'SAD', label: '우울', boxLeft: '701px', boxTop: '212px', iconLeft: '784px', iconTop: '225px', labelLeft: '766px', labelTop: '244px' },
];

function WriteEdit() {
  const navigate = useNavigate();

  const today = new Date();
  const dateString = `${today.getFullYear()} - ${String(today.getMonth() + 1).padStart(2, '0')} - ${String(today.getDate()).padStart(2, '0')}`;
  const apiDateString = today.toISOString().split('T')[0];

  const [userId, setUserId] = useState<number | null>(null);
  const [diaryId, setDiaryId] = useState<string | number | null>(null);
  const [diaryTitle, setDiaryTitle] = useState<string>('');
  const [diaryContent, setDiaryContent] = useState<string>('');
  const [selectedEmotion, setSelectedEmotion] = useState<string | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [currentImageUrl, setCurrentImageUrl] = useState<string | undefined>(undefined);
  
  const [loadingState, setLoadingState] = useState<LoadingState>({
    save: false,
    analyze: false,
    complete: false,
  });

  const fileInputRef = useRef<HTMLInputElement>(null);
  const minLength = 10;
  const maxLength = 1000;
  
  const isAnyLoading = Object.values(loadingState).some(state => state);

  // [초기화]
  useEffect(() => {
    const id = getUserId();
    setUserId(id);
  }, []);

  // --- Handlers ---

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      setImagePreview(URL.createObjectURL(file));
      setImageFile(file);
    }
  };

  const handleTriggerFileUpload = () => {
    fileInputRef.current?.click();
  };

  const handleRemoveImage = (e: React.MouseEvent) => {
    e.stopPropagation();
    setImagePreview(null);
    setImageFile(null);
    setCurrentImageUrl(undefined);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const handleEmotionClick = (id: string) => {
    setSelectedEmotion(id);
  };

  /**
   * [핵심] 저장/수정 로직
   * - userId 체크
   * - diaryId 유무에 따라 createDiary / updateDiary 호출
   */
  const performSaveOrUpdate = async (): Promise<{ id: number, emotionAnalysis?: EmotionAnalysisResponse }> => {
    if (!userId) {
      const id = getUserId();
      if (!id) throw new Error("로그인이 필요합니다.");
    }

    let response: DiaryDtoResponse;

    try {
      if (diaryId) {
        // [수정] Update
        let finalImageUrl = currentImageUrl;

        // 새 이미지 업로드 필요 시
        if (imageFile) {
           finalImageUrl = await uploadFile(imageFile);
           setCurrentImageUrl(finalImageUrl);
           setImageFile(null);
        }

        response = await updateDiary(userId!, diaryId, diaryContent, finalImageUrl);
      } else {
        // [생성] Create (이미지 유무는 createDiary 내부에서 분기)
        response = await createDiary(userId!, diaryContent, imageFile || undefined);
        
        if (response.imageUrl) setCurrentImageUrl(response.imageUrl);
      }

      console.log('performSaveOrUpdate 응답:', response);
      
      if (!response) {
        console.error('응답이 없습니다.');
        throw new Error("서버로부터 응답을 받지 못했습니다.");
      }
      
      if (!response.id) {
        console.error('응답에 ID가 없습니다. 응답 데이터:', response);
        throw new Error("서버로부터 일기 ID를 받아오지 못했습니다. 응답: " + JSON.stringify(response));
      }
      
      setDiaryId(response.id);
      return { id: response.id, emotionAnalysis: response.emotionAnalysis };

    } catch (error) {
      console.error("API 요청 실패:", error);
      if (error instanceof Error) {
        console.error("에러 메시지:", error.message);
        console.error("에러 스택:", error.stack);
      }
      throw error;
    }
  };

  // --- Actions ---

  const handleSave = async () => {
    if (isAnyLoading) return;
    if (!diaryTitle.trim()) {
      alert("제목을 입력해주세요.");
      return;
    }
    setLoadingState(prev => ({ ...prev, save: true }));
    try {
      await performSaveOrUpdate();
      alert('일기가 저장되었습니다.');
    } catch (error) {
      console.error(error);
      alert("일기 저장에 실패했습니다.");
    } finally {
      setLoadingState(prev => ({ ...prev, save: false }));
    }
  };

  const handleAnalyze = async () => {
    if (isAnyLoading) return;
    if (diaryContent.length < minLength) {
      alert(`내용을 최소 ${minLength}자 이상 작성해야 분석할 수 있습니다.`);
      return;
    }
    setLoadingState(prev => ({ ...prev, analyze: true }));
    try {
      const { id, emotionAnalysis } = await performSaveOrUpdate();
      
      let analysisData = emotionAnalysis;

      if (!analysisData) {
        analysisData = await getDiaryAnalysis(userId!, id);
      }
      
      if (analysisData && analysisData.integratedEmotion) {
        const { emotion, score } = analysisData.integratedEmotion;
        alert(`분석 완료!\n감정: ${emotion} (감정 온도: ${score}℃)`);
      } else {
        alert("분석 결과가 없습니다.");
      }
    } catch (error) {
      console.error(error);
      alert("감정 분석 중 오류가 발생했습니다.");
    } finally {
      setLoadingState(prev => ({ ...prev, analyze: false }));
    }
  };

  const handleComplete = async () => {
    if (isAnyLoading) return;
    if (!diaryTitle.trim()) { alert("제목을 입력해주세요."); return; }
    if (diaryContent.length < minLength) { alert(`일기 내용은 최소 ${minLength}자 이상이어야 합니다.`); return; }
    if (!selectedEmotion) { alert("오늘의 감정을 선택해주세요."); return; }
    
    setLoadingState(prev => ({ ...prev, complete: true }));
    try {
      const { id } = await performSaveOrUpdate();
      alert('일기 작성이 완료되었습니다.');
      navigate(`/results`);
    } catch (error) {
      console.error(error);
      alert("일기 저장에 실패했습니다.");
    } finally {
      setLoadingState(prev => ({ ...prev, complete: false }));
    }
  };

  return (
    <PageLayout>
      <div className="flex justify-center w-full h-full">
        <div className="relative w-full h-full bg-orange-100/40 overflow-hidden rounded-[10px]">
          
          {/* === [Section 1] 우측 이미지 업로드 === */}
          <div className="w-[685px] h-[552px] left-[168px] top-[100px] absolute overflow-hidden"> 
             {imagePreview ? (
                <div className="absolute left-[42px] top-[225px] w-[600px] h-[288px] bg-white rounded-lg shadow-md overflow-hidden z-10 border-[3px] border-yellow-400">
                    <img src={imagePreview} alt="Preview" className="w-full h-full object-cover" />
                    <button 
                      onClick={handleRemoveImage}
                      className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600 transition-colors"
                      title="이미지 삭제"
                    >
                      <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"/></svg>
                    </button>
                </div>
             ) : (
               <>
                  <div className="w-[600px] h-28 left-[42px] top-[14px] absolute text-center justify-center text-yellow-800 text-5xl font-medium font-['jsMath-cmti10'] italic leading-7">mooDiary</div>
                  <div className="w-[600px] h-16 left-[42px] top-[132px] absolute text-center justify-center text-yellow-800 text-2xl font-medium font-['Inter'] leading-7">당신의 소중한 순간을 기록해보세요.</div>
                  <div className="w-[600px] h-72 left-[42px] top-[225px] absolute bg-orange-100 rounded-tl-xl rounded-tr-[10px] rounded-bl-md rounded-br-md border-[3px] border-yellow-400 flex items-center justify-center"></div>
                  
                  {/* 데코레이션 아이콘들 */}
                  <div className="left-[30px] top-[212px] absolute"><svg width="28" height="28" viewBox="0 0 28 28" fill="none"><circle cx="14" cy="14" r="14" fill="#FFBE4D"/><circle cx="14" cy="14" r="12.5" stroke="#FFFAEF" strokeOpacity="0.9" strokeWidth="3"/></svg></div>
                  <div className="left-[620px] top-[212px] absolute"><svg width="36" height="36" viewBox="0 0 36 36" fill="none"><circle cx="18" cy="18" r="18" fill="#FFD900"/><circle cx="18" cy="18" r="16.5" stroke="#FFFAEF" strokeOpacity="0.9" strokeWidth="3"/></svg></div>
                  
                  <div className="w-100 h-8 left-[143px] top-[354px] absolute text-center justify-center text-yellow-800 text-2xl font-normal font-['Inter'] leading-7 z-20">오늘의 순간을 담은 사진을 올려보세요.</div>
                  
                  <div className="w-24 h-24 left-[297px] top-[252px] absolute overflow-hidden z-20 flex items-center justify-center">
                    <svg width="80" height="80" viewBox="0 0 80 80" fill="none" className="absolute">
                      <rect x="5" y="5" width="70" height="70" rx="10" stroke="#FF9326" strokeWidth="5" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    <div className="absolute top-[20px] left-[50%] translate-x-[-50%]">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="7" stroke="#FF9326" strokeWidth="5" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </div>
                    {/* 내부 산 */}
                    <div className="absolute bottom-[15px] left-[50%] translate-x-[-50%]">
                      <svg width="64" height="32" viewBox="0 0 64 32" fill="none">
                        <path d="M4 28L24 8L44 28L56 16L68 28" stroke="#FF9326" strokeWidth="5" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </div>
                  </div>
               </>
             )}

            <input 
              type="file" 
              ref={fileInputRef} 
              onChange={handleImageChange} 
              className="hidden" 
              accept="image/png, image/jpeg, image/gif"
            />
            <button onClick={handleTriggerFileUpload} className="w-56 h-12 p-2 left-[228px] top-[421px] absolute inline-flex justify-start items-center gap-2.5 z-30 group">
              <div className="w-56 h-12 px-5 py-2 left-0 top-0 absolute bg-gradient-to-r from-amber-500 to-red-500 rounded-[5px] flex justify-start items-center gap-2.5 group-hover:opacity-90 transition-opacity">
              </div>
              <div className="w-52 h-7 text-center justify-center text-white text-xl font-semibold font-['Inter'] tracking-wide relative z-10">
                {imagePreview ? "이미지 변경" : "이미지 업로드"}
              </div>
            </button>
            <div className="w-64 h-7 left-[213px] top-[481px] absolute text-center justify-center text-orange-400 text-xs font-medium font-['Inter'] capitalize leading-7 tracking-tight">이미지 업로드는 선택 사항입니다.</div>
          </div>

          {/* === [Section 2] 상단 입력 폼 (날짜/제목/내용/감정) === */}
          <div className="w-[953px] h-[680px] left-[34px] top-[700px] absolute bg-gradient-to-b from-orange-50 to-orange-100 rounded-[10px] outline outline-[3px] outline-offset-[-3px] outline-orange-300 overflow-hidden">
            
            <div className="w-96 h-44 left-[41px] top-[25px] absolute inline-flex flex-col justify-start items-start gap-2">
              <div className="w-14 justify-center text-stone-400 text-xl font-semibold font-['Inter']">날짜</div>
              <div className="w-96 h-11 relative bg-black/0">
                <div className="w-96 h-10 left-0 top-[1px] absolute bg-white rounded-[10px] border border-orange-300" />
                <div className="w-80 h-4 left-[12px] top-[13px] absolute justify-center text-orange-300 text-xs font-normal font-['Inter']">
                  {dateString}
                </div>
              </div>
              
              <div className="w-14 justify-center text-stone-400 text-xl font-bold font-['Inter'] mt-3">제목</div>
              <div className="w-96 h-11 relative bg-black/0">
                <input 
                  type="text"
                  value={diaryTitle}
                  onChange={(e) => setDiaryTitle(e.target.value)}
                  className="w-96 h-10 left-0 top-[2px] absolute bg-white rounded-[10px] border border-orange-300 px-3 text-orange-800 text-sm focus:outline-none focus:ring-2 focus:ring-orange-200"
                  placeholder="오늘 일기의 제목을 입력하세요"
                />
              </div>
            </div>
            
            <div className="w-32 left-[43px] top-[276px] absolute justify-center text-stone-400 text-xl font-bold font-['Inter']">내용</div>

            <div className="w-[874px] h-[330px] left-[43px] top-[320px] absolute bg-black/0">
              <div className="w-[869px] h-80 left-0 top-0 absolute bg-stone-50 rounded-[10px] border border-orange-300 pointer-events-none" />
              {[42, 84, 126, 168, 210, 252, 294].map((top) => (
                  <div key={top} className={`w-[868px] h-0 left-0 absolute outline outline-1 outline-offset-[-0.50px] outline-dashed outline-orange-300/50`} style={{top: `${top}px`}}></div>
              ))}
              <textarea
                value={diaryContent}
                onChange={(e) => setDiaryContent(e.target.value)}
                placeholder={`오늘 있었던 일, 느낀 감정, 생각들을 자유롭게 적어주세요.\n( 최소 ${minLength}자 / 최대 ${maxLength}자 )`}
                className="absolute left-[13px] top-[11px] w-[848px] h-[300px] bg-transparent border-none resize-none focus:ring-0 text-orange-800 text-base font-normal font-['Inter'] leading-[42px]"
                style={{ lineHeight: '42px' }}
                maxLength={maxLength}
              />
            </div>

            <div className="w-32 left-[507px] top-[25px] absolute justify-center text-stone-400 text-xl font-semibold font-['Inter']">오늘의 감정</div>
            
            {EMOTIONS.map((emotion) => (
              <React.Fragment key={emotion.id}>
                <div 
                  onClick={() => handleEmotionClick(emotion.id)}
                  className={`w-44 h-14 absolute rounded-[10px] border-[3px] cursor-pointer transition-all duration-200 flex items-center justify-center
                    ${selectedEmotion === emotion.id 
                      ? 'border-green-600 bg-green-200 shadow-inner' 
                      : 'border-yellow-400 bg-white hover:border-green-300 hover:bg-green-50' 
                    }`}
                  style={{ left: emotion.boxLeft, top: emotion.boxTop }}
                  title={emotion.label}
                />
                
                <div 
                  className="absolute pointer-events-none" 
                  style={{ left: emotion.iconLeft, top: emotion.iconTop }}
                >
                  <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                    <path d="M7.5 13.75C10.9518 13.75 13.75 10.9518 13.75 7.5C13.75 4.04822 10.9518 1.25 7.5 1.25C4.04822 1.25 1.25 4.04822 1.25 7.5C1.25 10.9518 4.04822 13.75 7.5 13.75Z" stroke="#1E1E1E" strokeLinecap="round" strokeLinejoin="round"/>
                    <path d="M5 8.75C5 8.75 5.9375 10 7.5 10C9.0625 10 10 8.75 10 8.75" stroke="#1E1E1E" strokeLinecap="round" strokeLinejoin="round"/>
                    <path d="M5.625 5.62549H5.63125" stroke="#1E1E1E" strokeLinecap="round" strokeLinejoin="round"/>
                    <path d="M9.375 5.62549H9.38125" stroke="#1E1E1E" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                </div>

                <div 
                  className="w-12 h-4 absolute text-center justify-center text-black text-[10px] font-normal font-['Inter'] pointer-events-none"
                  style={{ left: emotion.labelLeft, top: emotion.labelTop }}
                >
                  {emotion.label}
                </div>
              </React.Fragment>
            ))}
          </div>
          
          <div className="left-[968px] top-[690px] absolute"><svg width="36" height="36" viewBox="0 0 36 36" fill="none"><circle cx="18" cy="18" r="18" fill="#FFD900"/><circle cx="18" cy="18" r="16.5" stroke="#FFFAEF" strokeOpacity="0.9" strokeWidth="3"/></svg></div>
          <div className="left-[22px] top-[688px] absolute"><svg width="28" height="28" viewBox="0 0 28 28" fill="none"><circle cx="14" cy="14" r="14" fill="#FFBE4D"/><circle cx="14" cy="14" r="12.5" stroke="#FFFAEF" strokeOpacity="0.9" strokeWidth="3"/></svg></div>
          
          {/* === [Section 3] 액션 버튼 그룹 === */}
          <div className="w-[949px] h-[230px] left-[36px] top-[1430px] absolute bg-gradient-to-b from-orange-50 to-orange-100 rounded-[10px] outline outline-[3px] outline-orange-300/50 overflow-hidden">
            <div className="left-[21px] top-[19px] absolute">
                <svg width="22" height="24" viewBox="0 0 22 24" fill="none"><path d="M0 0L21.0608 12L0 24V0Z" fill="#8E573E"/></svg>
            </div>
            <div className="left-[54px] top-[17px] absolute text-yellow-800 text-2xl font-medium font-['Inter'] capitalize tracking-tight">액션 버튼</div>
            
            <div className="left-[23px] top-[58px] absolute inline-flex gap-6">
              {/* 1. 임시저장 */}
              <button 
                onClick={handleSave} 
                disabled={isAnyLoading} 
                className="w-[280px] h-12 bg-neutral-100/80 rounded-[10px] outline outline-2 outline-offset-[-2px] outline-orange-400 flex justify-center items-center gap-2.5 hover:bg-neutral-200 transition-colors disabled:opacity-50"
              >
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16L21 8V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21Z" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/><path d="M17 21V13H7V21" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/><path d="M7 3V8H15" stroke="#8E573E" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
                  <span className="text-yellow-800 text-2xl font-medium font-['Inter'] capitalize tracking-tight whitespace-nowrap">
                    {loadingState.save ? "저장 중..." : "임시저장"}
                  </span>
              </button>
              
              {/* 2. 분석 시작 */}
              <button 
                onClick={handleAnalyze} 
                disabled={isAnyLoading} 
                className="w-[280px] h-12 bg-gradient-to-r from-amber-500 to-red-500 rounded-[10px] outline outline-2 outline-offset-[-2px] outline-orange-400 flex justify-center items-center gap-2.5 hover:opacity-90 transition-opacity disabled:opacity-50"
              >
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M11 19C15.4183 19 19 15.4183 19 11C19 6.58172 15.4183 3 11 3C6.58172 3 3 6.58172 3 11C3 15.4183 6.58172 19 11 19Z" stroke="#212121" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/><path d="M21 20.9999L16.65 16.6499" stroke="#212121" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
                  <span className="text-black text-2xl font-medium font-['Inter'] capitalize tracking-tight whitespace-nowrap">
                      {loadingState.analyze ? "분석 중..." : "분석 시작"}
                  </span>
              </button>
              
              {/* 3. 완료 */}
              <button 
                onClick={handleComplete} 
                disabled={isAnyLoading} 
                className="w-[280px] h-12 bg-yellow-50 rounded-[10px] outline outline-2 outline-offset-[-2px] outline-orange-400 flex justify-center items-center gap-2.5 hover:bg-yellow-100 transition-colors disabled:opacity-50"
              >
                  <span className="text-black text-2xl font-medium font-['Inter'] capitalize tracking-tight whitespace-nowrap">
                    {loadingState.complete ? "전송 중..." : "완료"}
                  </span>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M20 6L9 17L4 12" stroke="#212121" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
              </button>
            </div>
            
            <div className="left-[18px] top-[127px] absolute justify-center text-yellow-900/40 text-l font-medium font-['Inter'] capitalize leading-7 tracking-tight">임시저장 : 임시저장 이후 나중에 이어서 작성할 수 있습니다.<br/>분석 시작 : 현재 내용을 바탕으로 감정 분석을 실행합니다.<br/>완료 : 일기 작성 및 분석을 완료하고, 저장합니다. ( 결과 페이지로 이동 )</div>
          </div>
        </div>
      </div>
    </PageLayout>
  );
}

export default WriteEdit;