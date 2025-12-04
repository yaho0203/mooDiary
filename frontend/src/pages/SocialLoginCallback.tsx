import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { socialLogin, saveTokens, extractUserIdFromRedirect, AuthError } from "@/lib/auth";
import { useAuth } from "@/context/AuthContext";
import { LoadingSpinner } from "@/components/common/LoadingSpinner";
import { ErrorDisplay } from "@/components/common/ErrorDisplay";

/**
 * 소셜 로그인 리다이렉트 처리 페이지
 * 
 * 리다이렉트 URL 형식:
 * - 기존 멤버: /member/login/present?member=808033069
 * - 신규 멤버: /member/login/create?member=300063979
 * 
 * member 파라미터의 마지막 숫자가 사용자 ID
 */
export default function SocialLoginCallback() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login: authLogin } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    const processSocialLogin = async () => {
      try {
        // 전체 URL 및 파라미터 로그
        console.log("🔍 소셜 로그인 콜백:", {
          fullUrl: window.location.href,
          pathname: window.location.pathname,
          search: window.location.search,
          allParams: Object.fromEntries(searchParams.entries()),
        });

        // URL 파라미터에서 member 값 추출
        const memberParam = searchParams.get("member");

        if (!memberParam) {
          console.error("❌ member 파라미터가 없습니다:", {
            url: window.location.href,
            allParams: Object.fromEntries(searchParams.entries()),
          });
          throw new Error("사용자 정보를 찾을 수 없습니다. URL을 확인해주세요.");
        }

        console.log("📝 member 파라미터:", memberParam);

        // member 파라미터에서 사용자 ID 추출
        // 예: "808033069" → 9, "300063979" → 9
        const userId = extractUserIdFromRedirect(memberParam);
        console.log("🔑 추출된 사용자 ID:", userId);

        // 소셜 로그인 API 호출
        console.log("📡 소셜 로그인 API 호출 중...");
        const tokens = await socialLogin(userId);
        console.log("✅ 토큰 받음:", {
          hasAccessToken: !!tokens.accessToken,
          hasRefreshToken: !!tokens.refreshToken,
          hasUser: !!tokens.user,
        });

        // 토큰 저장
        saveTokens(tokens);
        console.log("💾 토큰 저장 완료");
        
        authLogin();
        console.log("🎉 로그인 완료! 메인 페이지로 이동");

        // 로그인 성공 후 메인 페이지로 이동
        navigate("/", { replace: true });
      } catch (err) {
        console.error("💥 소셜 로그인 처리 실패:", err);
        const message = err instanceof AuthError 
          ? err.message 
          : err instanceof Error 
            ? err.message 
            : "소셜 로그인 처리 중 오류가 발생했습니다.";
        setError(message);
        setIsProcessing(false);
      }
    };

    processSocialLogin();
  }, [searchParams, navigate, authLogin]);

  if (isProcessing) {
    return <LoadingSpinner message="소셜 로그인 처리 중..." />;
  }

  if (error) {
    return (
      <ErrorDisplay
        error={error}
        onRetry={() => navigate("/login")}
      />
    );
  }

  return null;
}

