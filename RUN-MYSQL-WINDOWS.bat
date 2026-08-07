@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo Starting MySQL container...
docker compose up -d
if errorlevel 1 (
  echo Docker/MySQL could not be started.
  pause
  exit /b 1
)
echo Starting Eventora with the mysql profile...
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
pause
