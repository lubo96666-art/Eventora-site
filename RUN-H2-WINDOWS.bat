@echo off
chcp 65001 > nul
echo Starting Eventora with embedded H2 database...
echo Open: http://localhost:8080/visualization
mvn spring-boot:run -Dspring-boot.run.profiles=h2
pause
