@echo off
echo  Starting mooDiary Development Environment...

REM 백엔드 시작
echo  Starting Backend Server...
cd backend
start "Backend Server" cmd /k "gradlew.bat bootRun"

REM 프론트엔드 시작
echo  Starting Frontend Server...
cd ../frontend
start "Frontend Server" cmd /k "npm start"

echo  Development servers started!
echo Backend: http://localhost:8080
echo Frontend: http://localhost:3000

pause
