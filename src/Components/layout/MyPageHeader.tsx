import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import { Menu, X } from "lucide-react";
import { useProfileData } from "@/hooks/useProfileData";
import { NAV_ITEMS } from "@/constants/navigation";
import { useAuth } from "@/context/AuthContext";
import { clearTokens } from "@/lib/auth";

export default function MyPageHeader() {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const { profileImage, nickName } = useProfileData();
  const { logout } = useAuth();

  const handleLogout = () => {
    clearTokens();
    logout();
    navigate("/login");
  };

  return (
    <header className="sticky mt-12 w-full border-b bg-white/80 backdrop-blur supports-[backdrop-filter]:bg-white/60">
      <div className="container w-full flex h-[153px] items-center px-4 sm:px-6 items-center justify-between">
        <a 
          href="/"
          className="flex items-center gap-1 font-semibold text-gray-900"
        >
          <img src="/diaryImg.png" className="h-12 w-12" />
          <span className="text-[32px] font-['jsMath-cmti10'] text-[#8E573E]">mooDiary</span>
        </a>

        {/* Desktop nav */}
        <div className="w-[543px] h-[70px] items-center flex border">
          <nav className="md:flex w-530 items-center gap-5 text-base justify-around ml-5">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  cn(
                    "whitespace-nowrap transition-colors hover:text-gray-900 border-b-2 border-transparent hover:border-gray-300 pb-2",
                    isActive ? "text-gray-900" : "text-gray-500",
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>  

        <div className="flex items-center gap-4">
          <button className="w-14 h-14 rounded mt-4">
            <img src={profileImage} alt="프로필 이미지" className="h-full w-full object-contain" />
            <span className="text-[12px] whitespace-nowrap">안녕하세요, {nickName}님</span>
          </button>

          <div>
          <button
            className="w-[104px] h-[35px] border rounded-sm ml-4 text-white bg-gradient-to-r from-[#FF9E0D] to-[#FF5B3A] hover:from-[#FFB347] hover:to-[#FF795E] transition-colors"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
        </div>

        {/* Mobile */}
        <button
          className="md:hidden inline-flex items-center justify-center rounded-md border px-2.5 py-2 text-gray-700"
          onClick={() => setOpen((v) => !v)}
          aria-label="Toggle menu"
        >
          {open ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
        </button>
      </div>

      {/* Mobile menu panel */}
      {open && (
        <div className="md:hidden border-t bg-white">
          <div className="container mx-auto px-4 py-3 flex flex-col">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => setOpen(false)}
                className={({ isActive }) =>
                  cn(
                    "rounded-md px-2 py-2 text-sm transition-colors hover:bg-gray-50",
                    isActive ? "text-gray-900" : "text-gray-600",
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
          </div>
        </div>
      )}
    </header>
  );
}

