// 유효성 검사 유틸리티 함수

export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

export const isValidPassword = (password: string): boolean => {
  // 최소 8자, 하나 이상의 문자와 숫자
  return password.length >= 8;
};

export const validateLoginForm = (
  email: string,
  password: string
): { isValid: boolean; error?: string } => {
  if (!email || !password) {
    return { isValid: false, error: "이메일과 비밀번호를 모두 입력해주세요." };
  }

  if (!isValidEmail(email)) {
    return { isValid: false, error: "유효한 이메일 주소를 입력해주세요." };
  }

  if (!isValidPassword(password)) {
    return { isValid: false, error: "비밀번호는 최소 8자 이상이어야 합니다." };
  }

  return { isValid: true };
};

