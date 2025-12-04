// 색상 상수 정의

export const THEME_COLORS = {
  primary: "#8E573E",
  secondary: "#FDA54E",
  gradient: {
    main: "linear-gradient(90deg, #FFEAB1 7.55%, #FFDED3 121.31%)",
    button: "linear-gradient(to right, #FF9E0D, #FF5B3A)",
  },
} as const;

export const CATEGORY_COLORS: Record<string, string> = {
  book: "from-blue-500 to-purple-600",
  movie: "from-red-500 to-pink-600",
  music: "from-green-500 to-teal-600",
  poem: "from-indigo-500 to-blue-600",
  quote: "from-yellow-500 to-orange-600",
} as const;

export const EMOTION_COLORS = {
  warm: {
    bg: "bg-gradient-to-br from-green-100 via-emerald-100 to-teal-100",
    border: "border-green-200",
  },
  cool: {
    bg: "bg-gradient-to-br from-blue-100 via-indigo-100 to-purple-100",
    border: "border-purple-200",
  },
} as const;

