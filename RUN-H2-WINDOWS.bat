@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo Starting Eventora with embedded H2 database...
echo Open: http://localhost:8080/visualization
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
pause
