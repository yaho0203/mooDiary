@echo off
chcp 65001 >nul
echo ========================================
echo Java PATH 수정 스크립트
echo ========================================
echo.
echo 이 스크립트는 시스템 PATH에서 Java 25를 제거하고 Java 17을 맨 앞에 추가합니다.
echo 관리자 권한이 필요합니다.
echo.
pause

echo [1/3] 현재 PATH 확인 중...
setx PATH "%PATH%" /M >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: 관리자 권한이 필요합니다!
    echo 이 스크립트를 우클릭하고 "관리자 권한으로 실행"을 선택하세요.
    pause
    exit /b 1
)

echo [2/3] Java 17 경로를 PATH 맨 앞에 추가 중...
for /f "tokens=2*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH 2^>nul') do set "CURRENT_PATH=%%B"

REM Java 17 경로가 이미 있으면 제거
set "CURRENT_PATH=%CURRENT_PATH:C:\Program Files\Java\jdk-17\bin;=%"
set "CURRENT_PATH=%CURRENT_PATH:;C:\Program Files\Java\jdk-17\bin=%"

REM Java 25 경로 제거 (일반적인 경로들)
set "CURRENT_PATH=%CURRENT_PATH:C:\Program Files\Java\jdk-25\bin;=%"
set "CURRENT_PATH=%CURRENT_PATH:;C:\Program Files\Java\jdk-25\bin=%"
set "CURRENT_PATH=%CURRENT_PATH:C:\Program Files\Eclipse Adoptium\jdk-25%\bin;=%"
set "CURRENT_PATH=%CURRENT_PATH:;C:\Program Files\Eclipse Adoptium\jdk-25%\bin=%"

REM Java 17을 맨 앞에 추가
set "NEW_PATH=C:\Program Files\Java\jdk-17\bin;%CURRENT_PATH%"

echo [3/3] PATH 업데이트 중...
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PATH /t REG_EXPAND_SZ /d "%NEW_PATH%" /f >nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 성공! PATH가 업데이트되었습니다.
    echo ========================================
    echo.
    echo 변경사항을 적용하려면:
    echo 1. 모든 터미널/VS Code를 종료하세요
    echo 2. VS Code를 다시 시작하세요
    echo 3. 새 터미널에서 "java -version"을 실행해 확인하세요
    echo.
) else (
    echo.
    echo ERROR: PATH 업데이트 실패!
    echo 관리자 권한으로 실행했는지 확인하세요.
    echo.
)

pause

