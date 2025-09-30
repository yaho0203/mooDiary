import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const MyPage: React.FC = () => {
  const [userInfo, setUserInfo] = useState<any>(null);
  const navigate = useNavigate();

  useEffect(() => {
    // 로컬 스토리지에서 사용자 정보 가져오기
    const storedUser = localStorage.getItem('mooDiaryUser');
    if (storedUser) {
      setUserInfo(JSON.parse(storedUser));
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('mooDiaryUser');
    navigate('/login');
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
          <div className="mypage-container">
            <div className="mypage-header">
              <h1 className="diary-title handwriting">마이페이지</h1>
              <p className="diary-date">나의 정보를 확인하고 관리하세요</p>
            </div>

            <div className="mypage-content">
              <div className="user-info-card">
                <div className="user-info-header">
                  <h2 className="handwriting">사용자 정보</h2>
                </div>
                <div className="user-info-details">
                  <div className="info-item">
                    <label className="handwriting">닉네임</label>
                    <span className="info-value">{userInfo.nickname}</span>
                  </div>
                  <div className="info-item">
                    <label className="handwriting">이메일</label>
                    <span className="info-value">{userInfo.email}</span>
                  </div>
                  <div className="info-item">
                    <label className="handwriting">가입일</label>
                    <span className="info-value">
                      {userInfo.signupDate ? new Date(userInfo.signupDate).toLocaleDateString('ko-KR') : '정보 없음'}
                    </span>
                  </div>
                </div>
              </div>

              <div className="mypage-actions">
                <button 
                  onClick={handleLogout}
                  className="diary-button logout-button"
                >
                  로그아웃
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <style>{`
        .mypage-container {
          max-width: 600px;
          margin: 0 auto;
          padding: 20px;
        }

        .mypage-header {
          text-align: center;
          margin-bottom: 40px;
        }

        .mypage-content {
          display: flex;
          flex-direction: column;
          gap: 30px;
        }

        .user-info-card {
          background: #fefcf7;
          border: 2px solid #d4c4a8;
          border-radius: 12px;
          padding: 30px;
          box-shadow: 0 8px 16px rgba(93, 78, 55, 0.1);
          position: relative;
        }

        .user-info-card::before {
          content: '';
          position: absolute;
          left: 20px;
          top: 20px;
          right: 20px;
          bottom: 20px;
          border: 1px solid #e8dcc0;
          border-radius: 8px;
          pointer-events: none;
        }

        .user-info-header {
          margin-bottom: 25px;
          text-align: center;
        }

        .user-info-header h2 {
          margin: 0;
          color: #8b7355;
          font-size: 1.5rem;
        }

        .user-info-details {
          display: flex;
          flex-direction: column;
          gap: 20px;
        }

        .info-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 15px 0;
          border-bottom: 1px solid #e8dcc0;
        }

        .info-item:last-child {
          border-bottom: none;
        }

        .info-item label {
          font-weight: bold;
          color: #8b7355;
          font-size: 1.1rem;
        }

        .info-value {
          color: #5d4e37;
          font-size: 1rem;
        }

        .mypage-actions {
          text-align: center;
        }

        .logout-button {
          background: #d4c4a8;
          color: #5d4e37;
          border: 2px solid #8b7355;
          padding: 12px 30px;
          font-size: 1.1rem;
          font-weight: bold;
          transition: all 0.3s ease;
        }

        .logout-button:hover {
          background: #8b7355;
          color: #fefcf7;
          transform: translateY(-2px);
          box-shadow: 0 4px 8px rgba(93, 78, 55, 0.2);
        }

        @media (max-width: 768px) {
          .mypage-container {
            padding: 15px;
          }

          .user-info-card {
            padding: 20px;
          }

          .info-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 5px;
          }
        }
      `}</style>
    </div>
  );
};

export default MyPage;
