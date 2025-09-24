import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const UserDiaryPage: React.FC = () => {
  const { nickname: urlNickname } = useParams<{ nickname: string }>();
  const navigate = useNavigate();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [userInfo, setUserInfo] = useState<any>(null);

  // URL 파라미터가 없으면 로컬 스토리지에서 사용자 정보 가져오기
  useEffect(() => {
    if (!urlNickname) {
      const storedUser = localStorage.getItem('mooDiaryUser');
      if (storedUser) {
        setUserInfo(JSON.parse(storedUser));
      }
    }
  }, [urlNickname]);

  useEffect(() => {
    // 페이지 로드 시 현재 시간 업데이트
    const timer = setInterval(() => {
      setCurrentDate(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const handleStartDiary = () => {
    // 홈페이지로 이동
    navigate('/home');
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long'
    });
  };

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  return (
    <div className="main-container">
      <div className="diary-page">
        <div className="diary-header">
          <h1 className="diary-title handwriting">{(urlNickname || userInfo?.nickname) || '사용자'}의 다이어리</h1>
          <p className="diary-date">{formatDate(currentDate)}</p>
          <p className="diary-time">{formatTime(currentDate)}</p>
        </div>

        <div className="diary-content">
          <div className="diary-welcome">
            <div className="welcome-message">
              <h2 className="handwriting">🎉 환영합니다, {(urlNickname || userInfo?.nickname) || '사용자'}님!</h2>
              <p className="handwriting">
                당신만의 특별한 감정 일기를 시작해보세요.<br/>
                AI가 당신의 마음을 이해하고 함께 성장해나갈 거예요.
              </p>
            </div>

            <div className="diary-features">
              <div className="feature-card">
                <div className="feature-icon">📝</div>
                <h3 className="handwriting">일기 작성</h3>
                <p>매일의 감정과 생각을 자유롭게 기록하세요</p>
              </div>
              
              <div className="feature-card">
                <div className="feature-icon">🧠</div>
                <h3 className="handwriting">AI 분석</h3>
                <p>AI가 당신의 감정을 분석하고 인사이트를 제공해요</p>
              </div>
              
              <div className="feature-card">
                <div className="feature-icon">📊</div>
                <h3 className="handwriting">감정 트렌드</h3>
                <p>시간에 따른 감정 변화를 시각적으로 확인하세요</p>
              </div>
            </div>

            <div className="start-diary-section">
              <button 
                onClick={handleStartDiary}
                className="diary-button start-button"
              >
                나의 다이어리 시작하기
              </button>
            </div>
          </div>

          {/* 스티커들 */}
          <div className="sticker" style={{ top: '50px', right: '50px' }}>🌸</div>
          <div className="sticker" style={{ top: '150px', right: '30px' }}>✨</div>
          <div className="sticker" style={{ top: '250px', right: '60px' }}>💝</div>
          <div className="sticker" style={{ top: '350px', right: '40px' }}>🌟</div>
        </div>
      </div>

      <style>{`
        .diary-welcome {
          text-align: center;
          padding: 40px 0;
        }

        .welcome-message {
          margin-bottom: 40px;
        }

        .welcome-message h2 {
          font-size: 2.2rem;
          color: #8b7355;
          margin-bottom: 20px;
          text-shadow: 1px 1px 2px rgba(139, 115, 85, 0.3);
        }

        .welcome-message p {
          font-size: 1.2rem;
          color: #5d4e37;
          line-height: 1.8;
          margin: 0;
        }

        .diary-time {
          font-size: 1rem;
          color: #a68b5b;
          margin-top: 5px;
          font-family: 'Kalam', cursive;
        }

        .diary-features {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
          gap: 25px;
          margin: 40px 0;
        }

        .feature-card {
          background: #fefcf7;
          border: 2px solid #e8dcc0;
          border-radius: 12px;
          padding: 25px;
          text-align: center;
          transition: all 0.3s ease;
          position: relative;
          overflow: hidden;
        }

        .feature-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 25px rgba(93, 78, 55, 0.15);
          border-color: #c4b59a;
        }

        .feature-card::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 4px;
          background: linear-gradient(90deg, #ffd1dc 0%, #ffb3c6 50%, #ffd1dc 100%);
        }

        .feature-icon {
          font-size: 3rem;
          margin-bottom: 15px;
          display: block;
        }

        .feature-card h3 {
          font-size: 1.3rem;
          color: #8b7355;
          margin: 0 0 15px 0;
          font-weight: 600;
        }

        .feature-card p {
          color: #5d4e37;
          line-height: 1.6;
          margin: 0;
          font-size: 1rem;
        }

        .start-diary-section {
          margin-top: 50px;
        }

        .start-button {
          background: linear-gradient(135deg, #e8dcc0 0%, #d4c4a8 100%);
          border: 3px solid #c4b59a;
          border-radius: 30px;
          padding: 20px 40px;
          font-family: 'Quicksand', sans-serif;
          font-weight: 700;
          color: #5d4e37;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 6px 15px rgba(93, 78, 55, 0.3);
          font-size: 1.3rem;
          text-transform: uppercase;
          letter-spacing: 1px;
        }

        .start-button:hover {
          background: linear-gradient(135deg, #d4c4a8 0%, #c4b59a 100%);
          transform: translateY(-3px);
          box-shadow: 0 10px 25px rgba(93, 78, 55, 0.4);
        }

        .start-button:active {
          transform: translateY(-1px);
          box-shadow: 0 4px 10px rgba(93, 78, 55, 0.3);
        }

        @media (max-width: 768px) {
          .welcome-message h2 {
            font-size: 1.8rem;
          }

          .welcome-message p {
            font-size: 1rem;
          }

          .diary-features {
            grid-template-columns: 1fr;
            gap: 20px;
          }

          .feature-card {
            padding: 20px;
          }

          .start-button {
            padding: 15px 30px;
            font-size: 1.1rem;
          }
        }
      `}</style>
    </div>
  );
};

export default UserDiaryPage;
