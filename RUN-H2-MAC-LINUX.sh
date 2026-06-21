#!/usr/bin/env bash
echo "Starting Eventora with embedded H2 database..."
echo "Open: http://localhost:8080/visualization"
mvn spring-boot:run -Dspring-boot.run.profiles=h2
