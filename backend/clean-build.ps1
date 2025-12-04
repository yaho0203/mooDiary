Write-Host "Cleaning Gradle build directories..." -ForegroundColor Yellow
if (Test-Path "build") { Remove-Item -Path "build" -Recurse -Force }
if (Test-Path ".gradle") { Remove-Item -Path ".gradle" -Recurse -Force }
if (Test-Path "bin") { Remove-Item -Path "bin" -Recurse -Force }
if (Test-Path "out") { Remove-Item -Path "out" -Recurse -Force }
Write-Host "Clean complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Starting Gradle build..." -ForegroundColor Yellow
.\gradlew.bat clean build --no-daemon

