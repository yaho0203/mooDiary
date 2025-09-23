import React, { useState } from 'react';
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

  return (
    <div className="home-page">
      <div className="hero-section">
        <h1>mooDiary에 오신 것을 환영합니다</h1>
        <p>AI와 함께하는 감정 일기 플랫폼</p>
      </div>

      <div className="main-content">
        <DiaryForm onSubmit={handleDiarySubmit} isLoading={isAnalyzing} />
        {analysisResult && <EmotionAnalysis analysis={analysisResult} />}
      </div>

      <div className="features">
        <div className="feature">
          <h3>감정 분석</h3>
          <p>텍스트와 이미지를 통한 정확한 감정 분석</p>
        </div>
        <div className="feature">
          <h3>트렌드 시각화</h3>
          <p>나의 감정 변화를 한눈에 확인</p>
        </div>
        <div className="feature">
          <h3>맞춤 추천</h3>
          <p>감정에 맞는 콘텐츠 추천</p>
        </div>
      </div>

      <style>{`
        .home-page {
          min-height: 100vh;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          padding: 20px;
        }

        .hero-section {
          text-align: center;
          color: white;
          margin-bottom: 40px;
        }

        .hero-section h1 {
          font-size: 2.5rem;
          margin-bottom: 16px;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .hero-section p {
          font-size: 1.2rem;
          opacity: 0.9;
        }

        .main-content {
          max-width: 800px;
          margin: 0 auto 40px;
        }

        .features {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
          gap: 20px;
          max-width: 1000px;
          margin: 0 auto;
        }

        .feature {
          background: rgba(255, 255, 255, 0.1);
          backdrop-filter: blur(10px);
          border-radius: 12px;
          padding: 24px;
          text-align: center;
          color: white;
          border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .feature h3 {
          margin: 0 0 12px 0;
          font-size: 1.3rem;
        }

        .feature p {
          margin: 0;
          opacity: 0.9;
          line-height: 1.5;
        }

        @media (max-width: 768px) {
          .hero-section h1 {
            font-size: 2rem;
          }
          
          .hero-section p {
            font-size: 1rem;
          }
          
          .features {
            grid-template-columns: 1fr;
          }
        }
      `}</style>
    </div>
  );
};

export default HomePage;
