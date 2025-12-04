import { useState, useEffect } from "react";
import type { User } from "@shared/types";
import defaultImg from "@/assets/defaultImg.png";
import { getAccessToken, getUserInfo } from "@/lib/auth";

interface UseUserDataReturn {
  user: User;
  loading: boolean;
  error: string | null;
  isAuthenticated: boolean;
  refetch: () => Promise<void>;
}

export const useUserData = (): UseUserDataReturn => {
  const [user, setUser] = useState<User>({
    nickname: "사용자",
    avatarUrl: defaultImg,
    recentEmotion: "행복함",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const fetchUserData = async () => {
    // 인증 토큰 확인
    const token = getAccessToken();
    if (!token) {
      console.log("인증 토큰이 없습니다. 기본값을 사용합니다.");
      setIsAuthenticated(false);
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setIsAuthenticated(true);

      // localStorage에서 사용자 정보 가져오기
      const storedUser = getUserInfo();
      if (storedUser) {
        setUser({
          id: storedUser.id,
          email: storedUser.email,
          nickname: storedUser.nickname || "사용자",
          username: storedUser.username,
          phone: storedUser.phone,
          profileImage: storedUser.profileImage,
          avatarUrl: storedUser.profileImage || storedUser.avatarUrl || defaultImg,
          recentEmotion: storedUser.recentEmotion || "행복함",
          createdAt: storedUser.createdAt,
          updatedAt: storedUser.updatedAt,
        });
        console.log("✅ 저장된 사용자 정보 로드:", storedUser);
        return;
      }

      // localStorage에 정보가 없으면 API 호출 (fallback)
      const response = await fetch("/api/user/userdata", {
        credentials: "include",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      // 로그인 페이지로 리다이렉트된 경우 (HTML 응답)
      if (response.url.includes("/login")) {
        console.warn("인증이 필요합니다. 로그인 페이지로 리다이렉트되었습니다.");
        setIsAuthenticated(false);
        return;
      }

      if (!response.ok) {
        console.warn("사용자 API 비정상 응답:", response.status, response.url);
        setIsAuthenticated(false);
        return;
      }

      const contentType = response.headers.get("content-type") || "";
      if (!contentType.includes("application/json")) {
        const text = await response.text();
        console.warn("사용자 API: JSON 아닌 응답을 받음 (로그인 필요)", {
          status: response.status,
          url: response.url,
          bodyPreview: text.slice(0, 100),
        });
        setIsAuthenticated(false);
        return;
      }

      const data = await response.json();
      setUser({
        nickname: data.nickname || "사용자",
        avatarUrl: data.avatarUrl || defaultImg,
        recentEmotion: data.recentEmotion || "행복함",
      });
    } catch (err) {
      console.error("사용자 데이터 로드 실패:", err);
      setError("사용자 데이터를 불러올 수 없습니다.");
      setIsAuthenticated(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserData();
  }, []);

  return { user, loading, error, isAuthenticated, refetch: fetchUserData };
};

