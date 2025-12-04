import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useUserData } from "@/hooks/useUserData";  // ⭐ 추가
import { useProfileData } from "@/hooks/useProfileData";

export default function App() {
  const navigate = useNavigate();
  const { user, refetch } = useUserData();  // ⭐ fetchUserData 제거하고 이것만 사용
  const { profileImage, nickName } = useProfileData();
  const [isEdit, setIsEdit] = useState(false);
  const [editData, setEditData] = useState({
    nickname: "",
    email: "",
    phone: "",
    location: "",
    bio: "",
  });

  useEffect(() => {
    if (user) {
      setEditData({
        nickname: user.nickname || "",
        email: user.email || "",
        phone: user.phone || "",
        location: user.location || "",
        bio: user.bio || "",
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setEditData({ ...editData, [e.target.name]: e.target.value });
  };

  // ⭐ 프로필 저장 API
  const handleSave = async () => {
    try {
      const res = await fetch(`/api/user/${user.username}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(editData),
      });

      if (!res.ok) throw new Error("저장 실패");

      alert("프로필 저장 완료!");

      // 최신 정보 다시 가져오기
      await refetch();

      setIsEdit(false);
    } catch (err) {
      alert("저장 중 오류가 발생했습니다.");
    }
  };
  const handleLogout = () => {
    alert("로그아웃 되었습니다.");
    navigate("/login");
  };

  return (
    <div className="w-full min-h-screen bg-gradient-to-b from-[#FFFBEF] via-[#FFEAB1] to-[#F8EFAA]">

      {/* ----------------- NavBar ----------------- */}
      <div className="w-full bg-gradient-to-b from-[#FFFBEF] via-[#FFEAB1] to-[#F8EFAA] shadow-lg">
        <div className="max-w-screen-xl mx-auto flex items-center justify-between px-8 py-4">

          {/* Left - Logo */}
          <div className="flex items-center gap-2 cursor-pointer" onClick={() => navigate('/main')}>
            <img src="/diaryImg.png" className="h-12 w-12" />
            <span className="text-[#7A4E32] font-serif text-2xl">mooDiary</span>
          </div>

          {/* Center - Menu */}
          <div className="flex items-center justify-center">
            <div className="bg-[#F7E59E] shadow-md rounded-xl px-6 py-3 flex gap-6 text-[#CAAA74] font-medium text-[15px]">
              
              <button onClick={() => navigate('/main')} className="text-[#C18E00] hover:text-[#8B6E4E] transition-colors">
                홈
              </button>

              <button onClick={() => navigate('/write')} className="hover:text-[#C18E00] transition-colors">
                일기 작성
              </button>

              <button onClick={() => navigate('/results')} className="hover:text-[#C18E00] transition-colors">
                감정 분석
              </button>

              <button onClick={() => navigate('/records')} className="hover:text-[#C18E00] transition-colors">
                지난 일기
              </button>

              <button onClick={() => navigate('/bookmark')} className="hover:text-[#C18E00] transition-colors">
                북마크
              </button>

              <button onClick={() => navigate('/profile')} className="hover:text-[#C18E00] transition-colors">
                프로필
              </button>

              <button onClick={() => navigate('/recommendation')} className="hover:text-[#C18E00] transition-colors">
                추천 콘텐츠
              </button>

            </div>
          </div>

          {/* Right - Profile */}
          <div className="flex items-center gap-4">
            <button 
              onClick={handleLogout}
              className="px-5 py-2 rounded-lg bg-[#FF8F34] text-white font-medium shadow hover:bg-[#e6802e] transition-colors"
            >
              Logout
            </button>

            <img
              src={user?.profileImage || profileImage}
              className="w-10 h-10 rounded-full border bg-white object-cover cursor-pointer"
              alt="profile"
              onClick={() => navigate('/profile')}
            />

            <span className="text-sm text-gray-700">
              안녕하세요, <b>{user?.nickname || "게스트"}님!</b>
            </span>
          </div>
        </div>

        <div className="w-full flex justify-between px-14 pb-2">
          {Array.from({ length: 10 }).map((_, i) => (
            <div
              key={i}
              className="w-3 h-3 bg-[#FFF59D] border-2 border-[#F4D96B] rounded-full"
            ></div>
          ))}
        </div>
      </div>
      {/* ----------------- END NavBar ----------------- */}

      {/* ----------------- Profile Page ----------------- */}
      <div className="w-full min-h-screen bg-gradient-to-b from-[#FFFBEF] via-[#FFEAB1] to-[#F8EFAA] px-10 py-14">
        <div className="max-w-screen-xl mx-auto">
          <h1 className="text-3xl font-semibold text-[#7A4E32] mb-2">내 프로필</h1>
          <p className="text-[#B08A62] mb-10">개인정보와 설정을 관리하세요.</p>

          <div className="flex gap-10">

            {/* Left - Info */}
            <div className="flex-1 bg-white/60 rounded-xl shadow-md p-10 border border-[#F0D9A5]">

              <div className="flex justify-between mb-6">
                <h2 className="text-xl font-medium text-[#7A4E32]">개인정보</h2>

                {isEdit ? (
                  <div className="flex gap-2">
                    <button onClick={handleSave} className="px-5 py-1 bg-[#FFC94D] text-[#7A4E32] shadow rounded-lg">저장</button>
                    <button onClick={() => setIsEdit(false)} className="px-5 py-1 bg-gray-300 text-gray-700 shadow rounded-lg">취소</button>
                  </div>
                ) : (
                  <button onClick={() => setIsEdit(true)} className="px-5 py-1 bg-[#FFE7A3] text-[#C18E00] shadow rounded-lg">편집</button>
                )}
              </div>

              <div className="flex items-center gap-6 mb-8">
                <img
                  src={user?.profileImage || profileImage}
                  className="w-28 h-28 rounded-full border-2 border-[#F3D9A1] object-cover bg-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-6 mb-6">

                {/* 닉네임 */}
                <div className="flex flex-col">
                  <label className="text-[#7A4E32] mb-1">닉네임</label>
                  <input
                    type="text"
                    name="nickname"
                    value={editData.nickname}
                    onChange={handleChange}
                    readOnly={!isEdit}
                    className={`border border-[#F0C98C] rounded-md p-2 ${!isEdit && "bg-white/70"}`}
                  />
                </div>

                {/* 이메일 */}
                <div className="flex flex-col">
                  <label className="text-[#7A4E32] mb-1">이메일</label>
                  <input
                    type="text"
                    value={editData.email}
                    readOnly
                    className="border border-[#F0C98C] rounded-md p-2 bg-white/50 text-gray-500"
                  />
                </div>
              </div>

              {/* Bio */}
              <div className="flex flex-col">
                <label className="text-[#7A4E32] mb-2">자기소개</label>
                <textarea
                  name="bio"
                  rows={6}
                  value={editData.bio}
                  onChange={handleChange}
                  readOnly={!isEdit}
                  className={`border border-[#F0C98C] rounded-md p-3 resize-none ${!isEdit && "bg-white/70"}`}
                />
              </div>

            </div>


            {/* Right - Stats */}
            
          </div>
        </div>
      </div>
      {/* ----------------- END Profile Page ----------------- */}

      {/* ----------------- Setting Page ----------------- */}
      <div className="w-full min-h-screen bg-gradient-to-b from-[#FFFBEF] via-[#FFEAB1] to-[#F8EFAA] px-12 py-12">
        <div className="max-w-screen-xl mx-auto">
          <div className="w-[70%] h-[600px] bg-white/60 rounded-xl border border-[#F5B46A] shadow-md p-8">
            <h2 className="text-lg font-medium text-[#7A4E32] mb-4">설정</h2>
          </div>
        </div>
      </div>

      <footer className="fixed bottom-0 left-0 w-full bg-gradient-to-r from-[#FAD7A1] to-[#F7A54A] text-center text-[#b86b3b] text-sm py-4 shadow-inner">
        2025년, mooDiary 와 함께 매일매일을 특별한 일상으로 꾸며보세요.
      </footer>
    </div>
  );
}
