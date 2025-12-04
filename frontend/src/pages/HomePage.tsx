import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import DiaryForm from '../components/DiaryForm';
import EmotionAnalysis from '../components/EmotionAnalysis';

interface EmotionAnalysisResult {
  textEmotion?: {
    emotion: string;
    score: number;
    confidence: number;
  };
  imageEmotion?: {
    emotion: string;
    score: number;
    confidence: number;
  };
  integratedEmotion?: {
    emotion: string;
    score: number;
    confidence: number;
  };
  keywords?: string;
}

const HomePage: React.FC = () => {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisResult, setAnalysisResult] = useState<EmotionAnalysisResult | null>(null);
  const [userInfo, setUserInfo] = useState<any>(null);
  const navigate = useNavigate();

  useEffect(() => {
    // 로컬 스토리지에서 사용자 정보 가져오기
    const storedUser = localStorage.getItem('mooDiaryUser');
    if (storedUser) {
      setUserInfo(JSON.parse(storedUser));
    }
  }, []);

  const handleDiarySubmit = async (content: string, imageFile?: File) => {
    setIsAnalyzing(true);
    setAnalysisResult(null);

    try {
      const formData = new FormData();
      formData.append('content', content);
      if (imageFile) {
        formData.append('image', imageFile);
      }

      const response = await fetch('http://localhost:8080/api/diaries/with-image?userId=1', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        const result = await response.json();
        setAnalysisResult({
          textEmotion: result.textEmotion ? {
            emotion: result.textEmotion,
            score: result.textEmotionScore || 0,
            confidence: result.textEmotionConfidence || 0,
          } : undefined,
          imageEmotion: result.facialEmotion ? {
            emotion: result.facialEmotion,
            score: result.facialEmotionScore || 0,
            confidence: result.facialEmotionConfidence || 0,
          } : undefined,
          integratedEmotion: result.overallEmotion ? {
            emotion: result.overallEmotion,
            score: result.overallEmotionScore || 0,
            confidence: result.integratedEmotionConfidence || 0,
          } : undefined,
          keywords: result.keywords,
        });
      } else {
        const error = await response.text();
        alert(`일기 저장 실패: ${error}`);
      }
    } catch (error) {
      console.error('일기 저장 오류:', error);
      alert('일기 저장 중 오류가 발생했습니다.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  // 로그인되지 않은 사용자에게는 로그인 페이지로 리다이렉트
  if (!userInfo) {
    navigate('/login');
    return null;
  }

  return (
    <div className="main-container">
      <div className="diary-page">
        <div className="diary-content">

          <div style={{ marginBottom: '40px' }}>
            <DiaryForm onSubmit={handleDiarySubmit} isLoading={isAnalyzing} />
            {analysisResult && <EmotionAnalysis analysis={analysisResult} />}
          </div>

          <div className="diary-grid">
            <div className="diary-card">
              <div className="emotion-badge emotion-happy">🧠</div>
              <h3>감정 분석</h3>
              <p>텍스트와 이미지를 통한 정확한 감정 분석으로 나의 마음을 더 깊이 이해해보세요.</p>
            </div>
            <div className="diary-card">
              <div className="emotion-badge emotion-calm">📊</div>
              <h3>트렌드 시각화</h3>
              <p>나의 감정 변화를 한눈에 확인하고 패턴을 발견해보세요.</p>
            </div>
            <div className="diary-card">
              <div className="emotion-badge emotion-excited">💡</div>
              <h3>맞춤 추천</h3>
              <p>감정에 맞는 콘텐츠와 활동을 추천받아 더 나은 하루를 만들어보세요.</p>
            </div>
          </div>
        </div>
      </div>

      <style>{`
        .diary-actions {
          text-align: center;
          margin-bottom: 30px;
          padding: 20px 0;
        }

        .open-diary-btn {
          background: linear-gradient(135deg, #e8dcc0 0%, #d4c4a8 100%);
          border: 3px solid #c4b59a;
          border-radius: 30px;
          padding: 15px 35px;
          font-family: 'Quicksand', sans-serif;
          font-weight: 700;
          color: #5d4e37;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 6px 15px rgba(93, 78, 55, 0.3);
          font-size: 1.2rem;
          text-transform: uppercase;
          letter-spacing: 1px;
        }

        .open-diary-btn:hover {
          background: linear-gradient(135deg, #d4c4a8 0%, #c4b59a 100%);
          transform: translateY(-3px);
          box-shadow: 0 10px 25px rgba(93, 78, 55, 0.4);
        }

        .open-diary-btn:active {
          transform: translateY(-1px);
          box-shadow: 0 4px 10px rgba(93, 78, 55, 0.3);
        }
      `}</style>
    </div>
  );
};

export default HomePage;
