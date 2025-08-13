import React from 'react';

const HomePage: React.FC = () => {
  return (
    <div className="home-page">
      <h1>mooDiary에 오신 것을 환영합니다</h1>
      <p>AI와 함께하는 감정 일기 플랫폼</p>
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
    </div>
  );
};

export default HomePage;
