// 네비게이션 상수 정의

export interface NavItem {
  to: string;
  label: string;
}

export const NAV_ITEMS: NavItem[] = [
  { to: "/main", label: "홈" },
  { to: "/write", label: "일기 작성" },
  { to: "/results", label: "감정 분석" },
  { to: "/records", label: "지난 일기" },
  { to: "/bookmark", label: "북마크" },
  { to: "/profile", label: "프로필" },
  { to: "/recommendation", label: "추천 컨텐츠" },
];

export const RECOMMENDATION_CATEGORIES = [
  { id: "book", label: "도서", icon: "/book.png" },
  { id: "movies", label: "영화", icon: "/movie.png" },
  { id: "music", label: "음악", icon: "/music.png" },
  { id: "poem", label: "시", icon: "/poem.svg" },
  { id: "phrase", label: "명언", icon: "/phrase.png" },
] as const;

