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
      <h3>감정 분석 결과</h3>
      
      {analysis.integratedEmotion && (
        <div className="main-emotion">
          <div className="emotion-card primary">
            <div className="emotion-header">
              <span className="emotion-name" style={{ color: getEmotionColor(analysis.integratedEmotion.emotion) }}>
                {analysis.integratedEmotion.emotion}
              </span>
            </div>
            <div className="emotion-details">
              <div className="score-item">
                <span className="label">감정 강도</span>
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
                <span className="label">분석 신뢰도</span>
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
            <h4>📝 텍스트 분석</h4>
            <div className="emotion-item">
              <span className="emotion-name" style={{ color: getEmotionColor(analysis.textEmotion.emotion) }}>
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
            <h4>📷 이미지 분석</h4>
            <div className="emotion-item">
              <span className="emotion-name" style={{ color: getEmotionColor(analysis.imageEmotion.emotion) }}>
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
          <h4>🔍 키워드</h4>
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
          margin-top: 20px;
          padding: 20px;
          background: #f8fafc;
          border-radius: 12px;
          border: 1px solid #e2e8f0;
        }

        .emotion-analysis h3 {
          margin: 0 0 20px 0;
          color: #1e293b;
          text-align: center;
        }

        .loading {
          text-align: center;
          padding: 40px;
        }

        .loading-spinner {
          width: 40px;
          height: 40px;
          border: 4px solid #e2e8f0;
          border-top: 4px solid #4f46e5;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin: 0 auto 16px;
        }

        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }

        .main-emotion {
          margin-bottom: 20px;
        }

        .emotion-card {
          background: white;
          border-radius: 8px;
          padding: 16px;
          margin-bottom: 12px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .emotion-card.primary {
          border: 2px solid #4f46e5;
        }

        .emotion-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;
        }


        .emotion-name {
          font-size: 18px;
          font-weight: 600;
        }

        .emotion-details {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .score-item {
          display: flex;
          align-items: center;
          gap: 12px;
        }

        .label {
          font-size: 14px;
          color: #64748b;
          min-width: 80px;
        }

        .score-bar {
          flex: 1;
          height: 8px;
          background-color: #e2e8f0;
          border-radius: 4px;
          overflow: hidden;
        }

        .score-fill {
          height: 100%;
          transition: width 0.3s ease;
        }

        .score-value {
          font-weight: 600;
          color: #1e293b;
          min-width: 40px;
          text-align: right;
        }

        .confidence-item {
          display: flex;
          align-items: center;
          gap: 12px;
        }

        .confidence-value {
          font-size: 14px;
          color: #64748b;
        }

        .detailed-analysis {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 12px;
          margin-bottom: 20px;
        }

        .emotion-card h4 {
          margin: 0 0 12px 0;
          font-size: 14px;
          color: #64748b;
        }

        .emotion-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
        }

        .emotion-score {
          font-weight: 600;
          color: #1e293b;
        }

        .confidence {
          font-size: 12px;
          color: #64748b;
        }

        .keywords-section h4 {
          margin: 0 0 12px 0;
          font-size: 14px;
          color: #64748b;
        }

        .keywords {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
        }

        .keyword {
          background: #e2e8f0;
          color: #475569;
          padding: 4px 8px;
          border-radius: 16px;
          font-size: 12px;
          font-weight: 500;
        }

        @media (max-width: 768px) {
          .detailed-analysis {
            grid-template-columns: 1fr;
          }
          
          .score-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
          }
          
          .confidence-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
          }
        }
      `}</style>
    </div>
  );
};

export default EmotionAnalysis;
