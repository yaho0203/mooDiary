@echo off
chcp 65001 >nul
echo ========================================
echo Gradle Clean and Build
echo ========================================
echo.

echo [0/5] Fixing JAVA_HOME...
REM 잘못된 JAVA_HOME 제거 - PATH에서 Java 17 찾기
set JAVA_HOME=
for /f "tokens=*" %%i in ('where java 2^>nul') do (
    set JAVA_PATH=%%i
    goto :found_java
)
:found_java
if defined JAVA_PATH (
    REM java.exe 경로에서 JAVA_HOME 추출 (bin\java.exe 제거)
    for %%j in ("%JAVA_PATH%") do set JAVA_HOME=%%~dpj..
    set JAVA_HOME=%JAVA_HOME:\bin\=%
    set JAVA_HOME=%JAVA_HOME:\bin=%
    echo Found Java at: %JAVA_HOME%
) else (
    echo WARNING: Java not found in PATH, using system default
)

echo [1/5] Stopping Gradle daemon...
call gradlew.bat --stop 2>nul

echo [2/5] Removing build directories...
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle
if exist bin rmdir /s /q bin
if exist out rmdir /s /q out

echo [3/5] Cleaning Gradle cache...
if exist "%USERPROFILE%\.gradle\caches" rmdir /s /q "%USERPROFILE%\.gradle\caches"
if exist "%USERPROFILE%\.gradle\daemon" rmdir /s /q "%USERPROFILE%\.gradle\daemon"
if exist "%USERPROFILE%\.gradle\wrapper\dists\gradle-8.14.3-bin" rmdir /s /q "%USERPROFILE%\.gradle\wrapper\dists\gradle-8.14.3-bin"

echo [4/5] Starting build with detailed error output...
echo Using JAVA_HOME: %JAVA_HOME%
echo.
call gradlew.bat clean build --no-daemon --no-build-cache --stacktrace --info 2>&1 | findstr /V "Picked up JAVA_TOOL_OPTIONS"

echo.
echo ========================================
if %ERRORLEVEL% EQU 0 (
    echo Build SUCCESS!
) else (
    echo Build FAILED! Error code: %ERRORLEVEL%
)
echo ========================================
pause