@echo off
chcp 65001 >nul
echo ========================================
echo VS Code Java 확장 캐시 정리
echo ========================================
echo.

echo [1/4] VS Code Java Language Server 워크스페이스 정리 중...
if exist "%USERPROFILE%\.vscode\extensions\redhat.java-*\workspace" (
    for /d %%i in ("%USERPROFILE%\.vscode\extensions\redhat.java-*\workspace") do (
        echo Deleting: %%i
        rmdir /s /q "%%i" 2>nul
    )
)

echo [2/4] Java 확장 프로그램 캐시 정리 중...
if exist "%APPDATA%\Code\User\workspaceStorage" (
    for /d %%i in ("%APPDATA%\Code\User\workspaceStorage\*") do (
        if exist "%%i\redhat.java\jdt_ws" (
            echo Deleting JDT workspace: %%i\redhat.java\jdt_ws
            rmdir /s /q "%%i\redhat.java\jdt_ws" 2>nul
        )
    )
)

echo [3/4] Java 확장 프로그램 설정 파일 확인 중...
if exist "%APPDATA%\Code\User\settings.json" (
    echo Settings file exists: %APPDATA%\Code\User\settings.json
)

echo [4/4] 완료!
echo.
echo 다음 단계:
echo 1. VS Code를 완전히 종료하세요
echo 2. VS Code를 다시 시작하세요
echo 3. Ctrl+Shift+P를 누르고 "Java: Clean Java Language Server Workspace" 실행
echo 4. VS Code를 다시 시작하세요
echo.
pause

