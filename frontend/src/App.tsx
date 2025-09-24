import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation, useNavigate } from 'react-router-dom';
import './App.css';
import HomePage from './pages/HomePage';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import UserDiaryPage from './pages/UserDiaryPage';
import MyPage from './pages/MyPage';

// 루트 경로 조건부 컴포넌트
const ConditionalRootPage: React.FC = () => {
  const [userInfo, setUserInfo] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem('mooDiaryUser');
    if (storedUser) {
      setUserInfo(JSON.parse(storedUser));
    }
    setIsLoading(false);
  }, []);

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  // 로그인된 사용자에게는 LoginPage의 로그인 후 화면 표시
  if (userInfo) {
    return <LoginPage />;
  }

  // 로그인되지 않은 사용자에게는 LoginPage 표시
  return <LoginPage />;
};

// 다이어리 네비게이션 컴포넌트
const DiaryNavigation: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState<any>(null);

  // 사용자 정보 확인
  React.useEffect(() => {
    const storedUser = localStorage.getItem('mooDiaryUser');
    if (storedUser) {
      setUserInfo(JSON.parse(storedUser));
    }
  }, [location.pathname]);

  
  // 루트 경로, 로그인 페이지, 회원가입 페이지, 사용자 다이어리 페이지에서는 네비게이션 숨김
  if (location.pathname === '/' || location.pathname === '/login' || location.pathname === '/signup' || location.pathname.startsWith('/diary/')) {
    return null;
  }
  
  return (
    <nav className="diary-nav">
      <div className="nav-container">
        <Link to="/" className="diary-logo">
          mooDiary
        </Link>
        <ul className="nav-links">
          <li>
            <Link 
              to="/home" 
              className={`nav-link ${location.pathname === '/home' ? 'active' : ''}`}
            >
              홈
            </Link>
          </li>
          <li>
            <Link 
              to="/diary" 
              className={`nav-link ${location.pathname === '/diary' ? 'active' : ''}`}
            >
              일기쓰기
            </Link>
          </li>
          <li>
            <Link 
              to="/dashboard" 
              className={`nav-link ${location.pathname === '/dashboard' ? 'active' : ''}`}
            >
              대시보드
            </Link>
          </li>
          <li>
            <Link 
              to="/community" 
              className={`nav-link ${location.pathname === '/community' ? 'active' : ''}`}
            >
              커뮤니티
            </Link>
          </li>
          <li>
            <Link 
              to="/mypage" 
              className={`nav-link ${location.pathname === '/mypage' ? 'active' : ''}`}
            >
              마이페이지
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

// 임시 컴포넌트들 (나중에 실제 컴포넌트로 교체)
const Diary = () => (
  <div className="main-container">
    <div className="diary-page">
      <div className="diary-header">
        <h1 className="diary-title">새로운 일기</h1>
        <p className="diary-date">{new Date().toLocaleDateString('ko-KR', { 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric',
          weekday: 'long'
        })}</p>
      </div>
      <div className="diary-content">
        <p>일기 작성 페이지가 여기에 표시됩니다.</p>
        <div className="sticker" style={{ top: '20px', right: '20px' }}>😊</div>
      </div>
    </div>
  </div>
);

const Dashboard = () => (
  <div className="main-container">
    <div className="diary-page">
      <div className="diary-header">
        <h1 className="diary-title">감정 대시보드</h1>
        <p className="diary-date">나의 감정 변화를 확인해보세요</p>
      </div>
      <div className="diary-content">
        <div className="diary-grid">
          <div className="diary-card">
            <div className="emotion-badge emotion-happy">😊 행복</div>
            <h3>이번 주 감정</h3>
            <p>주로 긍정적인 감정을 느끼고 계시네요!</p>
          </div>
          <div className="diary-card">
            <div className="emotion-badge emotion-calm">😌 평온</div>
            <h3>감정 트렌드</h3>
            <p>안정적인 감정 상태를 유지하고 있습니다.</p>
          </div>
          <div className="diary-card">
            <div className="emotion-badge emotion-excited">🤩 설렘</div>
            <h3>특별한 순간</h3>
            <p>최근에 특별한 일들이 있었나 보네요!</p>
          </div>
        </div>
      </div>
    </div>
  </div>
);

const Community = () => (
  <div className="main-container">
    <div className="diary-page">
      <div className="diary-header">
        <h1 className="diary-title">커뮤니티</h1>
        <p className="diary-date">다른 사람들과 감정을 나눠보세요</p>
      </div>
      <div className="diary-content">
        <p>커뮤니티 페이지가 여기에 표시됩니다.</p>
        <div className="sticker" style={{ top: '30px', right: '30px' }}>💝</div>
        <div className="sticker" style={{ top: '80px', right: '10px' }}>🌟</div>
      </div>
    </div>
  </div>
);


function App() {
  return (
    <Router>
      <div className="App">
        <DiaryNavigation />
        <Routes>
          <Route path="/" element={<ConditionalRootPage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/diary/:nickname" element={<UserDiaryPage />} />
          <Route path="/diary" element={<Diary />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/community" element={<Community />} />
          <Route path="/mypage" element={<MyPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
