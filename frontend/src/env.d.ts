/// <reference types="vite/client" />

// 환경 변수 타입 선언
// Vite의 `import.meta.env`에 접근할 때 TypeScript 오류를 방지합니다.
interface ImportMetaEnv {
  readonly VITE_API_URL?: string;
  // 필요한 다른 VITE_* 환경변수를 여기에 추가하세요.
  readonly [key: string]: string | undefined;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

export {};
