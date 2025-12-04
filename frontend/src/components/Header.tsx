import React from 'react';

const Header: React.FC = () => {
  return (
    <header className="header">
      <h1>mooDiary</h1>
      <nav>
        <ul>
          <li><a href="/">홈</a></li>
          <li><a href="/diary">일기작성</a></li>
          <li><a href="/dashboard">대시보드</a></li>
          <li><a href="/community">커뮤니티</a></li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
