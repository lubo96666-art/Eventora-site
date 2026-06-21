@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo ========================================
echo   Eventora - startirane
ECHO ========================================
echo.
echo Otvori sled kato startira: http://localhost:8080/visualization
echo.
call mvn spring-boot:run
pause
