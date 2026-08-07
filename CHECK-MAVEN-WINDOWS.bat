@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo ========================================
echo   Eventora - Maven dependency check
echo ========================================
echo.
echo This downloads the Spring and Jakarta dependencies and compiles the project.
echo.
call mvnw.cmd -U -DskipTests clean compile
if errorlevel 1 (
    echo.
    echo Maven check failed. Check the internet connection and Java 17 installation.
    pause
    exit /b 1
)
echo.
echo BUILD SUCCESS - Jakarta Servlet and all Maven dependencies are available.
echo In IntelliJ click Maven - Reload All Maven Projects, then run EventoraApplication.
pause
