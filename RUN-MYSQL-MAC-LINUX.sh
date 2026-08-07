#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
