#!/usr/bin/env bash
cd "$(dirname "$0")"
echo "Starting Eventora with embedded H2 database..."
echo "Open: http://localhost:8080/visualization"
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
