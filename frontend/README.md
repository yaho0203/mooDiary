

## React와 Tailwind의 충돌에 골치 아팠던 나를 위하여
### 1단계 : Vite + React 프로젝트 생성
먼저 Vite로 React 프로젝트 템플릿을 생성합니다.
```
npm create vite@latest my-react-app -- --template react
```
```
> npx
> create-vite my-react-app react

|
o  Use rolldown-vite (Experimental)?:
|  No
|
o  Install with npm and start now?
|  No
|
o  Scaffolding project in C:\Users\catholic\Desktop\my-react-app...
|
—  Done. Now run:

  cd my-react-app
  npm install
  npm run dev
```

### 2단계 : 프로젝트 폴더로 이동
생성된 폴더로 이동합니다.
```
cd my-react-app
```

### 3단계 : React 18 설치 (일반 의존성)
React는 앱 실행에 필요하므로 **일반 의존성**으로 설치합니다. (`-D`를 붙이지 않습니다.)
```
npm install react@18 react-dom@18
```

### 4단계 : 개발용 의존성 (Vite 4 + Tailwind 3) 설치
Tailwind는 개발 중에만 필요하므로 **개발용 의존성**(`-D` 또는 `--save-dev`)으로 설치합니다.
- `vite@4`: React 18과 호환되는 Vite 4버전
- `@vitejs/plugin-react@4`: Vite 4와 호환되는 React 플러그인
- `tailwindcss@3`: Tailwind 3버전
```
npm install -D vite@4 @vitejs/plugin-react@4 tailwindcss@3 postcss autoprefixer
```

### 5단계 : Tailwind CSS 설정 파일 생성
설치된 Tailwind 3 버전에 맞춰 설정 파일을 생성합니다.
```
npx tailwindcss init -p
```
해당 명령어가 오류가 난다면 6단계의 파일을 직접 만들어서 진행.

### 6단계 : `tailwind.config.js` 설정
`tailwind.config.js` 파일을 열고, `content` 배열에 스캔할 파일 경로를 추가합니다.
```JavaScript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", // 이 경로 추가
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### 7단계 : 전역 CSS에 Tailwind 적용
`src/index.css` 파일의 기존 내용을 모두 지우고, 다음 3줄을 추가합니다.
```CSS
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### 8단계 : 작동 테스트 (`App.jsx`)
`src/App.jsx` 파일의 내용을 수정하여 Tailwind가 잘 적용되었는지 테스트합니다.
```JavaScript
function App() {
  return (
    <div className="flex h-screen items-center justify-center bg-gray-100">
      <h1 className="text-3xl font-bold text-emerald-600 underline">
        React(일반) & Tailwind(개발용) 설치 성공! 🤓
      </h1>
    </div>
  )
}

export default App
```

### 9단계 : 개발 파일 붙여넣기
`src`폴더와 `tailwind.config.js`파일에 작성했던 내용이 있다면 붙여넣기</br>
`src\App.jsx`, `src\main.jsx`, `src\components\Header.jsx`, `src\Pages\HomePage.jsx` 등등...

### 10단계 : 개발 서버 실행
프로젝트를 실행합니다.
```
npm run dev
```
