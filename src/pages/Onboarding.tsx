import React, { useState, useEffect } from "react";
import cameraIcon from "../assets/camera.png";
import checkIcon from "../assets/check.png";  
import bookIcon from "../assets/kakao.png";

export default function ProfileSetup() {
  const [nickname, setNickname] = useState("");
  const [username, setUsername] = useState("");
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  useEffect(() => {
    const storedName =
      localStorage.getItem("username") || localStorage.getItem("socialName");
    if (storedName) setUsername(storedName);
  }, []);

  // ✅ 파일 선택 시 미리보기 URL 생성
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setProfileImage(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleStartDiary = () => {
    if (!nickname.trim()) {
      alert("닉네임을 입력해주세요 😊");
      return;
    }
    alert(`환영합니다, ${nickname}님! ✨`);
  };

  return (
    <>
      <div className="flex flex-col justify-center items-center">
        <div className="bg-[#fff7e2] w-[1200px] h-[720px] rounded-xl shadow-[0_5px_20px_rgba(0,0,0,0.1)] flex overflow-hidden border border-[#f2cfa4]">
          {/* 왼쪽 영역 */}
          <div className="flex flex-col items-center justify-center w-1/2 bg-gradient-to-b from-[#FFFBEF] via-[#FFEAB1] to-[#F8EFAA] border-r border-8 border-orange-200 relative p-8">
            <h1 className="text-[36px] font-serif text-[#b86b3b] mb-3 flex items-center">
              <img src={bookIcon} alt="check" className="w-8 h-8 mr-2" />
              mooDiary
              <span className="ml-1 text-red-500">❤️</span>
            </h1>


            <p className="text-[20px] font-medium text-gray-700 mt-4">환영합니다!</p>
            <p className="text-[22px] font-semibold text-[#b86b3b] mt-1">
              {username ? `${username} 님` : "사용자 님"}
            </p>
            <p className="text-center text-gray-600 mt-6 text-[15px] flex items-center">
              ✨ <span className="mx-2">거의 다 왔어요</span> ✨
            </p>

            <div className="w-32 h-32 mt-8 rounded-full flex items-center justify-center">
              <div className="w-32 h-32 rounded-full flex items-center justify-center">
                <img
                  src={checkIcon}
                  alt="check"
                  className="w-32 h-32 object-contain translate-y-[4px]"
                />
              </div>
            </div>

            <p className="text-center text-gray-700 mt-10 text-[15px] leading-relaxed">
              당신만의 특별한 일기장을<br />
              만들어보세요
            </p>
            <p className="text-center text-gray-500 mt-1 text-sm">마지막 단계예요!</p>
          </div>

          {/* 오른쪽 영역 */}
          <div className="flex flex-col justify-center items-center w-1/2 bg-gradient-to-b from-[#FFFBEF] via-[#FFEAB1] to-[#F8EFAA] px-10 py-12 relative border-r border-8 border-orange-200 p-8">
            <h2 className="text-[24px] font-semibold text-[#b86b3b] mb-10">프로필 설정</h2>

            {/* ✅ 프로필 이미지 + 카메라 아이콘 */}
            <div className="relative w-32 h-32 mb-6">
              {previewUrl ? (
                <img
                  src={previewUrl}
                  alt="미리보기"
                  className="object-cover w-full h-full rounded-full border border-[#FFD28F]"
                />
              ) : (
                <div className="w-full h-full rounded-full flex items-center justify-center bg-[#FFF1C7] border-2 border-[#FFD28F]">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth={1.5}
                    stroke="#b86b3b"
                    className="w-14 h-14"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M15.75 7.5a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.5 19.5a8.25 8.25 0 0 1 15 0v.75A1.5 1.5 0 0 1 18 21.75H6a1.5 1.5 0 0 1-1.5-1.5v-.75Z"
                    />
                  </svg>
                </div>
              )}
              <div className="absolute bottom-2 right-2 w-8 h-8 rounded-full bg-white border border-[#FFD28F] shadow overflow-hidden">
                <img
                  src={cameraIcon}
                  alt="upload"
                  onClick={() => document.getElementById("fileInput")?.click()}
                  className="w-full h-full object-contain scale-[1.7] translate-y-[8px] cursor-pointer"
                />
              </div>
              <input
                id="fileInput"
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </div>

            <p className="text-gray-600 text-sm mb-8">
              사진을 클릭하여 이미지를 업로드하세요
            </p>

            <div className="w-full mb-6">
              <label className="text-sm text-gray-700 mb-2 block font-medium">
                닉네임*
              </label>
              <input
                type="text"
                placeholder="일기장에서 사용할 닉네임(20자 이내)"
                maxLength={20}
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                className="w-full border border-[#FFD28F] rounded-md px-4 py-2 bg-white/70 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-[#FFD28F] text-black"
              />
              <p className="text-right text-xs text-gray-400 mt-1">
                {nickname.length}/20 글자
              </p>
            </div>

            <button
              onClick={handleStartDiary}
              disabled={!nickname.trim()}
              className={`mt-4 w-full py-3 rounded-md text-white font-semibold shadow-md transition ${
                nickname.trim()
                  ? "bg-gradient-to-r from-[#F7B65E] to-[#F9A23C] hover:from-[#F9A23C] hover:to-[#F7B65E]"
                  : "bg-gray-300 cursor-not-allowed"
              }`}
            >
              일기장 시작하기
            </button>
          </div>
        </div>
        <footer className="fixed bottom-0 left-0 w-full bg-gradient-to-r from-[#FAD7A1] to-[#F7A54A] text-center text-[#b86b3b] text-sm py-4 shadow-inner">
          2025년, mooDiary 와 함께 매일매일을 특별한 일상으로 꾸며보세요.
        </footer>
      </div>
    </>
  );
}
