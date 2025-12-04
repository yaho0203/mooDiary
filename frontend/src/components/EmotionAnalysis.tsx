import React from 'react';

interface EmotionAnalysisProps {
  analysis: {
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
  };
  isLoading?: boolean;
}

const EmotionAnalysis: React.FC<EmotionAnalysisProps> = ({ analysis, isLoading = false }) => {
  if (isLoading) {
    return (
      <div className="emotion-analysis loading">
        <div className="loading-spinner"></div>
        <p>AI가 감정을 분석하고 있습니다...</p>
      </div>
    );
  }

  if (!analysis) {
    return null;
  }

  const getEmotionColor = (emotion: string) => {
    const emotionColors: { [key: string]: string } = {
      '행복': '#10b981',
      '기쁨': '#10b981',
      '만족': '#10b981',
      '평온': '#3b82f6',
      '슬픔': '#6b7280',
      '우울': '#6b7280',
      '실망': '#6b7280',
      '분노': '#ef4444',
      '화남': '#ef4444',
      '불안': '#f59e0b',
    };
    return emotionColors[emotion] || '#6b7280';
  };

  const getEmotionIcon = (emotion: string) => {
    // 이모티콘 대신 텍스트로 표시
    return '';
  };

  const getScoreLevel = (score: number) => {
    if (score >= 80) return '매우 높음';
    if (score >= 60) return '높음';
    if (score >= 40) return '보통';
    if (score >= 20) return '낮음';
    return '매우 낮음';
  };

  const getConfidenceLevel = (confidence: number) => {
    if (confidence >= 80) return '매우 높음';
    if (confidence >= 60) return '높음';
    if (confidence >= 40) return '보통';
    if (confidence >= 20) return '낮음';
    return '매우 낮음';
  };

  return (
    <div className="emotion-analysis">
      <div className="diary-form-header">
        <h3 className="handwriting">AI 감정 분석 결과</h3>
        <p className="diary-date">당신의 마음을 분석해드렸어요</p>
      </div>
      
      {analysis.integratedEmotion && (
        <div className="main-emotion">
          <div className="emotion-card primary">
            <div className="emotion-header">
              <div className="emotion-icon">
                {analysis.integratedEmotion.emotion === '행복' || analysis.integratedEmotion.emotion === '기쁨' ? '😊' :
                 analysis.integratedEmotion.emotion === '슬픔' || analysis.integratedEmotion.emotion === '우울' ? '😢' :
                 analysis.integratedEmotion.emotion === '분노' || analysis.integratedEmotion.emotion === '화남' ? '😠' :
                 analysis.integratedEmotion.emotion === '평온' || analysis.integratedEmotion.emotion === '차분' ? '😌' :
                 analysis.integratedEmotion.emotion === '불안' || analysis.integratedEmotion.emotion === '걱정' ? '😰' :
                 analysis.integratedEmotion.emotion === '설렘' || analysis.integratedEmotion.emotion === '흥분' ? '🤩' : '😐'}
              </div>
              <span className="emotion-name handwriting" style={{ color: getEmotionColor(analysis.integratedEmotion.emotion) }}>
                {analysis.integratedEmotion.emotion}
              </span>
            </div>
            <div className="emotion-details">
              <div className="score-item">
                <span className="label handwriting">감정 강도</span>
                <div className="score-bar">
                  <div 
                    className="score-fill" 
                    style={{ 
                      width: `${analysis.integratedEmotion.score}%`,
                      backgroundColor: getEmotionColor(analysis.integratedEmotion.emotion)
                    }}
                  ></div>
                </div>
                <span className="score-value">{analysis.integratedEmotion.score}%</span>
              </div>
              <div className="confidence-item">
                <span className="label handwriting">분석 신뢰도</span>
                <span className="confidence-value">
                  {analysis.integratedEmotion.confidence}% ({getConfidenceLevel(analysis.integratedEmotion.confidence)})
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="detailed-analysis">
        {analysis.textEmotion && (
          <div className="emotion-card">
            <h4 className="handwriting">📝 텍스트 분석</h4>
            <div className="emotion-item">
              <span className="emotion-name handwriting" style={{ color: getEmotionColor(analysis.textEmotion.emotion) }}>
                {analysis.textEmotion.emotion}
              </span>
              <span className="emotion-score">{analysis.textEmotion.score}%</span>
            </div>
            <div className="confidence">
              신뢰도: {analysis.textEmotion.confidence}%
            </div>
          </div>
        )}

        {analysis.imageEmotion && (
          <div className="emotion-card">
            <h4 className="handwriting">이미지 분석</h4>
            <div className="emotion-item">
              <span className="emotion-name handwriting" style={{ color: getEmotionColor(analysis.imageEmotion.emotion) }}>
                {analysis.imageEmotion.emotion}
              </span>
              <span className="emotion-score">{analysis.imageEmotion.score}%</span>
            </div>
            <div className="confidence">
              신뢰도: {analysis.imageEmotion.confidence}%
            </div>
          </div>
        )}
      </div>

      {analysis.keywords && (
        <div className="keywords-section">
          <h4 className="handwriting">발견된 키워드</h4>
          <div className="keywords">
            {analysis.keywords.split(',').map((keyword, index) => (
              <span key={index} className="keyword">
                {keyword.trim()}
              </span>
            ))}
          </div>
        </div>
      )}

      <style>{`
        .emotion-analysis {
          margin-top: 30px;
          padding: 30px;
          background: #fefcf7;
          border: 2px solid #d4c4a8;
          border-radius: 12px;
          box-shadow: 
            0 0 0 1px #e8dcc0,
            0 4px 20px rgba(93, 78, 55, 0.15);
          position: relative;
        }

        .emotion-analysis::before {
          content: '';
          position: absolute;
          left: 20px;
          top: 0;
          bottom: 0;
          width: 2px;
          background: linear-gradient(to bottom, 
            transparent 0%, 
            #c4b59a 20%, 
            #c4b59a 80%, 
            transparent 100%);
        }

        .diary-form-header {
          text-align: center;
          margin-bottom: 30px;
          padding-bottom: 20px;
          border-bottom: 2px solid #e8dcc0;
        }

        .emotion-analysis h3 {
          margin: 0 0 10px 0;
          color: #8b7355;
          font-size: 1.8rem;
        }

        .loading {
          text-align: center;
          padding: 40px;
        }

        .loading-spinner {
          width: 40px;
          height: 40px;
          border: 4px solid #e8dcc0;
          border-top: 4px solid #c4b59a;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin: 0 auto 16px;
        }

        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }

        .main-emotion {
          margin-bottom: 25px;
        }

        .emotion-card {
          background: #fefcf7;
          border: 2px solid #e8dcc0;
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 15px;
          box-shadow: 0 2px 8px rgba(93, 78, 55, 0.1);
          position: relative;
        }

        .emotion-card.primary {
          border: 3px solid #c4b59a;
          background: linear-gradient(135deg, #fefcf7 0%, #f8f6f0 100%);
        }

        .emotion-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 15px;
        }

        .emotion-icon {
          font-size: 2rem;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 50px;
          height: 50px;
          background: rgba(212, 196, 168, 0.2);
          border-radius: 50%;
          border: 2px solid #d4c4a8;
        }

        .emotion-name {
          font-size: 1.5rem;
          font-weight: 600;
        }

        .emotion-details {
          display: flex;
          flex-direction: column;
          gap: 15px;
        }

        .score-item {
          display: flex;
          align-items: center;
          gap: 15px;
        }

        .label {
          font-size: 1rem;
          color: #5d4e37;
          min-width: 100px;
          font-weight: 600;
        }

        .score-bar {
          flex: 1;
          height: 12px;
          background-color: #e8dcc0;
          border-radius: 6px;
          overflow: hidden;
          border: 1px solid #d4c4a8;
        }

        .score-fill {
          height: 100%;
          transition: width 0.5s ease;
          border-radius: 6px;
        }

        .score-value {
          font-weight: 600;
          color: #5d4e37;
          min-width: 50px;
          text-align: right;
          font-size: 1.1rem;
        }

        .confidence-item {
          display: flex;
          align-items: center;
          gap: 15px;
        }

        .confidence-value {
          font-size: 1rem;
          color: #8b7355;
          font-weight: 500;
        }

        .detailed-analysis {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 15px;
          margin-bottom: 25px;
        }

        .emotion-card h4 {
          margin: 0 0 15px 0;
          font-size: 1.1rem;
          color: #8b7355;
          font-weight: 600;
        }

        .emotion-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;
        }

        .emotion-score {
          font-weight: 600;
          color: #5d4e37;
          font-size: 1.1rem;
        }

        .confidence {
          font-size: 0.9rem;
          color: #8b7355;
        }

        .keywords-section h4 {
          margin: 0 0 15px 0;
          font-size: 1.1rem;
          color: #8b7355;
          font-weight: 600;
        }

        .keywords {
          display: flex;
          flex-wrap: wrap;
          gap: 10px;
        }

        .keyword {
          background: linear-gradient(135deg, #e8dcc0 0%, #d4c4a8 100%);
          color: #5d4e37;
          padding: 6px 12px;
          border-radius: 20px;
          font-size: 0.9rem;
          font-weight: 600;
          border: 1px solid #c4b59a;
          box-shadow: 0 2px 4px rgba(93, 78, 55, 0.1);
        }

        @media (max-width: 768px) {
          .detailed-analysis {
            grid-template-columns: 1fr;
          }
          
          .score-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 10px;
          }
          
          .confidence-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 10px;
          }

          .emotion-header {
            flex-direction: column;
            text-align: center;
            gap: 10px;
          }
        }
      `}</style>
    </div>
  );
};

export default EmotionAnalysis;
