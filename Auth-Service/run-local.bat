@echo off
echo Starting Auth Service in Simple Mode...
echo Using H2 in-memory database
echo All cloud features disabled
echo Tracing disabled
echo.

set GITHUB_CLIENT_ID=Ov23l1PpstZ09LdPgcvV
set GITHUB_CLIENT_SECRET=your-github-client-secret-here
set JWT_SECRET=myVerySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForSimple

gradlew.bat bootRun --args="--spring.profiles.active=simple"

pause
