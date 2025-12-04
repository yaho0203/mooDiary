#!/bin/bash

echo " Starting mooDiary Development Environment..."

# 백엔드 시작
echo " Starting Backend Server..."
cd backend
gradlew.bat bootRun &
BACKEND_PID=$!

# 프론트엔드 시작
echo " tarting Frontend Server..."
cd ../frontend
npm start &
FRONTEND_PID=$!

echo " Development servers started!"
echo "Backend: http://localhost:8080"
echo "Frontend: http://localhost:3000"

# 종료 시그널 처리
trap "echo " Stopping servers...'; kill $BACKEND_PID $FRONTEND_PID; exit" INT

# 서버 상태 모니터링
wait
