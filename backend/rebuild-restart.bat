@echo off
chcp 65001 >nul
echo ========================================
echo 백엔드 완전 재빌드 및 재시작
echo ========================================
echo.

echo [1/4] Java 17 강제 설정...
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME: %JAVA_HOME%

echo [2/4] Gradle 데몬 중지...
call gradlew.bat --stop 2>nul

echo [3/4] 빌드 디렉토리 삭제...
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle

echo [4/4] 재빌드 및 실행...
call gradlew.bat --no-daemon clean build bootRun

pause

