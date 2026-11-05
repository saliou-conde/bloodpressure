#!/bin/bash

set -e

echo "🔧 Starte Deployment..."

# ------------------------------
# 1. Backend bauen
# ------------------------------
echo "📦 Baue Backend..."
cd backend
mvn clean package -DskipTests

echo "🐳 Baue Backend-Docker-Image..."
docker build -t sconde87/bloodpressure:latest .

echo "⬆️ Push Backend-Image zu Docker Hub..."
docker push sconde87/bloodpressure:latest

cd ..

# ------------------------------
# 2. Frontend bauen
# ------------------------------
echo "📦 Baue Frontend..."
cd frontend

echo "🐳 Baue Frontend-Docker-Image..."
docker build -t sconde87/bloodpressure-frontend:latest .

echo "⬆️ Push Frontend-Image zu Docker Hub..."
docker push sconde87/bloodpressure-frontend:latest

cd ..

# ------------------------------
# 3. Server aktualisieren
# ------------------------------
echo "🚀 Aktualisiere Server..."

ssh root@139.162.135.188 << 'EOF'
  cd /root/github
  docker compose pull
  docker compose down
  docker compose up -d
EOF

echo "✅ Deployment abgeschlossen!"
