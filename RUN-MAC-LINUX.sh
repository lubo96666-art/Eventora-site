#!/usr/bin/env bash
cd "$(dirname "$0")"
echo "Eventora - startirane"
echo "Open: http://localhost:8080/visualization"
./mvnw spring-boot:run
