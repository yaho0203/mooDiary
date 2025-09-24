import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const SignupPage: React.FC = () => {
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (password !== confirmPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (!nickname.trim() || !email.trim() || !password.trim()) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    setIsLoading(true);
    
    try {
      // 백엔드 회원가입 API 호출
      const response = await fetch('http://localhost:8080/api/users/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nickname,
          email,
          password
        }),
      });

      if (response.ok) {
        const userData = await response.json();
        
        // 사용자 정보를 로컬 스토리지에 저장
        const userInfo = {
          id: userData.id,
          nickname: userData.nickname,
          email: userData.email,
          signupDate: new Date().toISOString()
        };
        
        localStorage.setItem('mooDiaryUser', JSON.stringify(userInfo));
        
        // 회원가입 완료 후 루트 경로로 이동
        navigate('/');
      } else {
        const error = await response.text();
        alert(`회원가입 실패: ${error}`);
      }
    } catch (error) {
      console.error('회원가입 오류:', error);
      alert('회원가입 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="main-container">
      <div className="diary-cover">
        <div className="diary-cover-front">
          <div className="diary-cover-title">
            <h1 className="diary-title handwriting">mooDiary</h1>
            <p className="diary-subtitle handwriting">AI와 함께하는 감정 일기</p>
          </div>
          
          <div className="diary-cover-decoration">
            <div className="cover-sticker sticker-1">🌸</div>
            <div className="cover-sticker sticker-2">✨</div>
            <div className="cover-sticker sticker-3">💝</div>
            <div className="cover-sticker sticker-4">🌟</div>
          </div>
        </div>

        <div className="diary-cover-back">
          <div className="signup-form-container">
            <div className="signup-form">
              <div className="diary-form-header">
                <h2 className="handwriting">회원가입</h2>
                <p className="diary-date">mooDiary에 오신 것을 환영합니다</p>
              </div>
              
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label htmlFor="nickname" className="handwriting">닉네임</label>
                  <input
                    type="text"
                    id="nickname"
                    value={nickname}
                    onChange={(e) => setNickname(e.target.value)}
                    className="diary-input"
                    placeholder="사용할 닉네임을 입력하세요"
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="email" className="handwriting">이메일</label>
                  <input
                    type="email"
                    id="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="diary-input"
                    placeholder="이메일을 입력하세요"
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="password" className="handwriting">비밀번호</label>
                  <input
                    type="password"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="diary-input"
                    placeholder="비밀번호를 입력하세요"
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="confirmPassword" className="handwriting">비밀번호 확인</label>
                  <input
                    type="password"
                    id="confirmPassword"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="diary-input"
                    placeholder="비밀번호를 다시 입력하세요"
                    disabled={isLoading}
                  />
                </div>

                <div className="form-actions">
                  <button
                    type="submit"
                    disabled={isLoading}
                    className="diary-button"
                  >
                    {isLoading ? (
                      <>
                        <span className="diary-spinner"></span> 가입 중...
                      </>
                    ) : (
                      '회원가입'
                    )}
                  </button>
                  
                  <div className="login-link">
                    <p>이미 계정이 있으신가요?</p>
                    <button
                      type="button"
                      onClick={() => navigate('/login')}
                      className="diary-button secondary"
                    >
                      로그인하기
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <style>{`
        .diary-cover {
          max-width: 900px;
          margin: 0 auto;
          display: flex;
          perspective: 1000px;
          min-height: 600px;
        }

        .diary-cover-front {
          flex: 1;
          background: linear-gradient(135deg, #f0e6d2 0%, #e8dcc0 50%, #d4c4a8 100%);
          border: 3px solid #c4b59a;
          border-radius: 12px 0 0 12px;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          position: relative;
          box-shadow: 
            -5px 0 15px rgba(93, 78, 55, 0.3),
            inset 0 0 0 1px rgba(255, 255, 255, 0.2);
        }

        .diary-cover-front::before {
          content: '';
          position: absolute;
          left: -12px;
          top: 15%;
          bottom: 15%;
          width: 16px;
          background: linear-gradient(45deg, 
            #c4b59a 0%, 
            #a68b5b 25%, 
            #8b7355 50%, 
            #a68b5b 75%, 
            #c4b59a 100%);
          border-radius: 8px;
          box-shadow: 
            -2px 0 4px rgba(139, 115, 85, 0.3),
            inset -1px 0 2px rgba(139, 115, 85, 0.2);
        }

        .diary-cover-front::after {
          content: '';
          position: absolute;
          left: -20px;
          top: 20%;
          bottom: 20%;
          width: 8px;
          background: linear-gradient(45deg, 
            #8b7355 0%, 
            #a68b5b 50%, 
            #8b7355 100%);
          border-radius: 4px;
          box-shadow: 
            -1px 0 2px rgba(139, 115, 85, 0.4),
            inset -1px 0 1px rgba(139, 115, 85, 0.3);
        }

        .diary-cover-back {
          flex: 1;
          background: #fefcf7;
          border: 3px solid #c4b59a;
          border-left: none;
          border-radius: 0 12px 12px 0;
          display: flex;
          align-items: center;
          justify-content: center;
          position: relative;
          box-shadow: 
            5px 0 15px rgba(93, 78, 55, 0.3),
            inset 0 0 0 1px rgba(255, 255, 255, 0.2);
        }

        .diary-cover-title {
          text-align: center;
          z-index: 2;
        }

        .diary-cover-title h1 {
          font-size: 3.5rem;
          color: #8b7355;
          margin: 0 0 20px 0;
          text-shadow: 2px 2px 4px rgba(139, 115, 85, 0.3);
        }

        .diary-cover-title p {
          font-size: 1.3rem;
          color: #a68b5b;
          margin: 0;
          font-weight: 400;
        }

        .diary-cover-decoration {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          pointer-events: none;
        }

        .cover-sticker {
          position: absolute;
          font-size: 2rem;
          animation: float 4s ease-in-out infinite;
        }

        .sticker-1 {
          top: 20%;
          left: 15%;
          animation-delay: 0s;
        }

        .sticker-2 {
          top: 30%;
          right: 20%;
          animation-delay: 1s;
        }

        .sticker-3 {
          bottom: 25%;
          left: 20%;
          animation-delay: 2s;
        }

        .sticker-4 {
          bottom: 35%;
          right: 15%;
          animation-delay: 3s;
        }

        @keyframes float {
          0%, 100% { transform: translateY(0px) rotate(0deg); }
          50% { transform: translateY(-10px) rotate(5deg); }
        }

        .signup-form-container {
          width: 100%;
          max-width: 400px;
          padding: 40px;
        }

        .signup-form {
          background: #fefcf7;
          border: 2px solid #d4c4a8;
          border-radius: 12px;
          padding: 30px;
          box-shadow: 
            0 0 0 1px #e8dcc0,
            0 4px 20px rgba(93, 78, 55, 0.15);
          position: relative;
        }

        .signup-form::before {
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

        .signup-form h2 {
          margin: 0 0 10px 0;
          color: #8b7355;
          font-size: 1.8rem;
        }

        .form-group {
          margin-bottom: 20px;
        }

        .form-group label {
          display: block;
          margin-bottom: 8px;
          font-weight: 600;
          color: #5d4e37;
          font-size: 1rem;
        }

        .diary-input {
          background: #fefcf7;
          border: 2px solid #e8dcc0;
          border-radius: 8px;
          padding: 12px;
          font-family: 'Kalam', cursive;
          font-size: 1rem;
          color: #5d4e37;
          width: 100%;
          transition: border-color 0.3s ease;
        }

        .diary-input:focus {
          outline: none;
          border-color: #c4b59a;
          box-shadow: 0 0 0 3px rgba(196, 181, 154, 0.2);
        }

        .diary-input:disabled {
          background-color: #f5f5f5;
          cursor: not-allowed;
        }

        .form-actions {
          text-align: center;
          margin-top: 30px;
        }

        .login-link {
          margin-top: 20px;
          padding-top: 20px;
          border-top: 1px solid #e8dcc0;
        }

        .login-link p {
          margin: 0 0 15px 0;
          color: #8b7355;
          font-size: 0.9rem;
        }

        .diary-button.secondary {
          background: linear-gradient(135deg, #f8f6f0 0%, #e8dcc0 100%);
          border: 2px solid #d4c4a8;
          color: #8b7355;
          font-size: 1rem;
          padding: 12px 25px;
        }

        .diary-button.secondary:hover {
          background: linear-gradient(135deg, #e8dcc0 0%, #d4c4a8 100%);
          color: #5d4e37;
        }

        .diary-button {
          background: linear-gradient(135deg, #e8dcc0 0%, #d4c4a8 100%);
          border: 2px solid #c4b59a;
          border-radius: 25px;
          padding: 15px 30px;
          font-family: 'Quicksand', sans-serif;
          font-weight: 600;
          color: #5d4e37;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 8px rgba(93, 78, 55, 0.2);
          font-size: 1.1rem;
          display: inline-flex;
          align-items: center;
          gap: 8px;
        }

        .diary-button:hover:not(:disabled) {
          background: linear-gradient(135deg, #d4c4a8 0%, #c4b59a 100%);
          transform: translateY(-2px);
          box-shadow: 0 6px 12px rgba(93, 78, 55, 0.3);
        }

        .diary-button:disabled {
          background: #e8dcc0;
          border-color: #d4c4a8;
          color: #a68b5b;
          cursor: not-allowed;
          transform: none;
          box-shadow: 0 2px 4px rgba(93, 78, 55, 0.1);
        }

        .diary-spinner {
          display: inline-block;
          width: 16px;
          height: 16px;
          border: 2px solid #e8dcc0;
          border-radius: 50%;
          border-top-color: #c4b59a;
          animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
          to { transform: rotate(360deg); }
        }

        @media (max-width: 768px) {
          .diary-cover {
            flex-direction: column;
            max-width: 500px;
          }

          .diary-cover-front {
            border-radius: 12px 12px 0 0;
            border-bottom: none;
            min-height: 300px;
          }

          .diary-cover-back {
            border-radius: 0 0 12px 12px;
            border-left: 3px solid #c4b59a;
            border-top: none;
          }

          .diary-cover-title h1 {
            font-size: 2.5rem;
          }

          .signup-form-container {
            padding: 20px;
          }
        }
      `}</style>
    </div>
  );
};

export default SignupPage;
