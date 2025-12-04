import React, { createContext, useContext, useState, PropsWithChildren, useEffect } from 'react';

// AuthContext 타입 정의
interface AuthContextType {
  isLoggedIn: boolean;
  login: () => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// AuthProvider 컴포넌트
export const AuthProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 초기 상태: 로그아웃

  const login = () => setIsLoggedIn(true);
  const logout = () => setIsLoggedIn(false);
  // AuthProvider에 추가
    useEffect(() => {
        const token = localStorage.getItem('authToken'); // 예: 로컬 스토리지 토큰 확인
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook으로 사용
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};