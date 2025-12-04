import "./global.css";
import { createRoot } from "react-dom/client";
import { TooltipProvider } from "./components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Main from "./pages/Main";
import NotFound from "./pages/NotFound";
import Results from "./pages/EmoResult";
import Bookmark from "./pages/Bookmark";
import Profile from "./pages/Profile";
import RecBook from "./pages/RecBook";
import RecMovie from "./pages/RecMovie";
import RecMusic from "./pages/RecMusic";
import RecPoem from "./pages/RecPoem";
import RecPhrase from "./pages/RecPhrase";
import WriteEdit from "./pages/WriteEdit";
import Records from "./pages/Records";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Onboarding from "./pages/Onboarding";
import SocialLoginCallback from "./pages/SocialLoginCallback";

const queryClient = new QueryClient();
const App = () => (
  <QueryClientProvider client={queryClient}>
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* 기본 페이지는 로그인 */}
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/onboarding" element={<Onboarding />} />
          {/* 소셜 로그인 리다이렉트 처리 */}
          <Route path="/member/login/present" element={<SocialLoginCallback />} />
          <Route path="/member/login/create" element={<SocialLoginCallback />} />
          
          {/* 인증 후 접근 가능한 페이지들 */}
          <Route path="/main" element={<Main />} />
          <Route path="/write" element={<WriteEdit />} />
          <Route path="/results" element={<Results />} />
          <Route path="/bookmark" element={<Bookmark />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/recommendation" element={<RecBook />} />
          <Route path="/records" element={<Records />} />
          <Route path="/movies" element={<RecMovie />} />
          <Route path="/music" element={<RecMusic />} />
          <Route path="/poem" element={<RecPoem />} />
          <Route path="/phrase" element={<RecPhrase />} />
          
          {/* 404 페이지 */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  </QueryClientProvider>
);

export default App;
