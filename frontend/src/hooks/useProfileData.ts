import { useState, useEffect } from "react";
import defaultImg from "@/assets/defaultImg.png";
import { getAccessToken } from "@/lib/auth";

interface ProfileData {
  profileImage: string;
  nickname: string;
}

interface UseProfileDataReturn {
  profileImage: string;
  nickName: string;
  loading: boolean;
  error: string | null;
  isAuthenticated: boolean;
}

export const useProfileData = (): UseProfileDataReturn => {
  const [profileImage, setProfileImage] = useState(defaultImg);
  const [nickName, setNickname] = useState("사용자");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const fetchProfileData = async () => {
      // 인증 토큰 확인
      const token = getAccessToken();
      if (!token) {
        console.log("인증 토큰이 없습니다. 기본 프로필을 사용합니다.");
        setIsAuthenticated(false);
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setIsAuthenticated(true);

        const response = await fetch("/api/main/user/profile", {
          credentials: "include",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        // 로그인 페이지로 리다이렉트된 경우
        if (response.url.includes("/login")) {
          console.warn("인증이 필요합니다. 로그인 페이지로 리다이렉트되었습니다.");
          setIsAuthenticated(false);
          return;
        }

        if (!response.ok) {
          console.warn("프로필 API 비정상 응답:", response.status, response.url);
          setIsAuthenticated(false);
          return;
        }

        const contentType = response.headers.get("content-type") || "";
        if (!contentType.includes("application/json")) {
          const text = await response.text();
          console.warn("프로필 API: JSON 아닌 응답을 받음 (로그인 필요)", {
            status: response.status,
            url: response.url,
            bodyPreview: text.slice(0, 100),
          });
          setIsAuthenticated(false);
          return;
        }

        const data: ProfileData = await response.json();
        setProfileImage(data.profileImage || defaultImg);
        setNickname(data.nickname || "사용자");
      } catch (err) {
        console.error("프로필 데이터 로드 실패:", err);
        setError("프로필 데이터를 불러올 수 없습니다.");
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    fetchProfileData();
  }, []);

  return { profileImage, nickName, loading, error, isAuthenticated };
};

