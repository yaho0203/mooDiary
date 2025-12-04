import type { Config } from "tailwindcss";

export default {
  darkMode: ["class"],
  // .js 파일의 내용을 반영하여 .html, .js, .jsx 파일도 포함하도록 수정
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  prefix: "",
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1400px",
      },
    },
    extend: {
      fontFamily: {
        // 기존 ts 파일의 sans 폰트 유지
        sans: [
          "Noto Sans KR",
          "Inter",
          "ui-sans-serif",
          "system-ui",
          "-apple-system",
          "Segoe UI",
          "Roboto",
          "Noto Sans",
          "Ubuntu",
          "Cantarell",
          "Helvetica Neue",
          "Arial",
          "sans-serif",
        ],
        // js 파일에서 가져온 폰트 추가
        default: ["Inter", "sans-serif"],
        alt: ["Poppins", "sans-serif"],
      },
      colors: {
        // --- 기존 ts 파일의 HSL 색상 (전부 유지) ---
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        sidebar: {
          DEFAULT: "hsl(var(--sidebar-background))",
          foreground: "hsl(var(--sidebar-foreground))",
          primary: "hsl(var(--sidebar-primary))",
          "primary-foreground": "hsl(var(--sidebar-primary-foreground))",
          accent: "hsl(var(--sidebar-accent))",
          "accent-foreground": "hsl(var(--sidebar-accent-foreground))",
          border: "hsl(var(--sidebar-border))",
          ring: "hsl(var(--sidebar-ring))",
        },

        // --- js 파일에서 가져온 색상 (추가) ---
        "light-bg-start": "#fffbf2",
        "light-bg-end": "#fff3d7",
        "light-bg": "#FFF4D4",
        "text-dark": "#212121",
        "text-muted": "#D48F5F",
        "text-placeholder": "#9C9A9A",
        "text-light": "#FFFFFF",
        "text-secondary": "#646161",
        "text-accent": "#D48F5F",
        "brand-brown": "#965D38",
        "nav-active-orange": "#DCA67B",
        "border-color": "#DCA67B",
        "border-muted": "#666666",
        "border-neutral": "#9C9A9A",
        "button-primary-bg": "#FF8637",
        "button-primary-hover": "#FFB652",
        "button-secondary-bg": "#DCA67B",
        "button-secondary-hover": "#BB8866",
        "image-placeholder-bg": "#fff4d4",
        "preview-placeholder-bg": "#eff3fd",
        "toggle-bg": "#ffb752",
        "toggle-inactive-bg": "#e5e7eb",
      },

      width: {
        18: "4.5rem", // 기존 ts 파일 내용
      },
      height: {
        18: "4.5rem", // 기존 ts 파일 내용
      },

      borderRadius: {
        lg: "var(--radius)", // 기존 ts 파일 내용
        md: "calc(var(--radius) - 2px)", // 기존 ts 파일 내용
        sm: "calc(var(--radius) - 4px)", // 기존 ts 파일 내용
      },

      // js 파일에서 가져온 boxShadow 추가
      boxShadow: {
        "md-custom":
          "0px 8px 12px -6px rgba(0, 0, 0, 0.16), 0px 12px 16px 0px rgba(0, 0, 0, 0.12), 0px 1px 32px 0px rgba(0, 0, 0, 0.08)",
      },

      // 기존 ts 파일의 keyframes 및 animation (유지)
      keyframes: {
        "accordion-down": {
          from: {
            height: "0",
          },
          to: {
            height: "var(--radix-accordion-content-height)",
          },
        },
        "accordion-up": {
          from: {
            height: "var(--radix-accordion-content-height)",
          },
          to: {
            height: "0",
          },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
      },
    },
  },
  // 기존 ts 파일의 plugins (유지)
  plugins: [require("tailwindcss-animate")],
} satisfies Config;