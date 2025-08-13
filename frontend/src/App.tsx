import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';

// 임시 컴포넌트들 (나중에 실제 컴포넌트로 교체)
const Home = () => <div>홈 페이지</div>;
const Diary = () => <div>일기 작성 페이지</div>;
const Dashboard = () => <div>대시보드 페이지</div>;
const Community = () => <div>커뮤니티 페이지</div>;
const Login = () => <div>로그인 페이지</div>;

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/diary" element={<Diary />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/community" element={<Community />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
