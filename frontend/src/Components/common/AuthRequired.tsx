import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { isAuthenticated } from "@/lib/auth";
import { LoadingSpinner } from "./LoadingSpinner";

interface AuthRequiredProps {
  children: React.ReactNode;
  redirectTo?: string;
}

/**
 * 인증이 필요한 페이지를 감싸는 컴포넌트
 * 로그인하지 않은 사용자는 자동으로 로그인 페이지로 리다이렉트
 */
export const AuthRequired: React.FC<AuthRequiredProps> = ({ 
  children, 
  redirectTo = "/login" 
}) => {
  const navigate = useNavigate();
  const isAuth = isAuthenticated();

  useEffect(() => {
    if (!isAuth) {
      console.warn("인증이 필요합니다. 로그인 페이지로 이동합니다.");
      navigate(redirectTo, { replace: true });
    }
  }, [isAuth, navigate, redirectTo]);

  if (!isAuth) {
    return <LoadingSpinner message="인증 확인 중..." />;
  }

  return <>{children}</>;
};

