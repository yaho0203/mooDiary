# 빌드 문제 해결 가이드

## 문제: IDE에서 빌드할 때마다 Gradle 손상

### 해결 방법 1: IDE 자동 빌드 끄기 (권장)

**NetBeans:**
1. Tools → Options → Build → Compile
2. "Compile on Save" 체크 해제
3. "Run on Save" 체크 해제

**IntelliJ IDEA:**
1. File → Settings → Build, Execution, Deployment → Compiler
2. "Build project automatically" 체크 해제

**Eclipse:**
1. Project → Build Automatically 체크 해제

### 해결 방법 2: 터미널에서만 빌드

IDE에서 빌드하지 말고, 항상 터미널에서 빌드하세요:

```bash
# PowerShell
cd backend
.\clean-build.ps1

# 또는 CMD
cd backend
clean-build.bat
```

### 해결 방법 3: 수동 빌드

```bash
cd backend
gradlew.bat clean build --no-daemon
```

## 주의사항

- **절대 IDE에서 자동 빌드와 터미널 빌드를 동시에 실행하지 마세요**
- 빌드 전에 항상 `clean-build.bat` 또는 `clean-build.ps1` 실행
- IDE를 재시작한 후에도 자동 빌드가 꺼져있는지 확인

