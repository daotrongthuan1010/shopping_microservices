#!/bin/bash

# Script để khởi động lại Kong services
echo "Dừng tất cả containers..."

# Dừng tất cả containers trước
docker-compose down

echo "Dừng và xóa các Kong containers cũ (bao gồm cả containers thừa)..."

# Dừng và xóa TẤT CẢ containers Kong (kể cả thừa)
docker stop $(docker ps -aq --filter "name=kong") 2>/dev/null
docker rm $(docker ps -aq --filter "name=kong") 2>/dev/null

echo "Xóa Kong volumes cũ (QUAN TRỌNG: Xóa dữ liệu PostgreSQL 16 cũ)..."
docker volume rm config_kong_db_data 2>/dev/null
docker volume prune -f

echo "Khởi động lại Kong services..."

# Khởi động Kong database trước
docker-compose up -d kong-db

echo "Đợi Kong database khởi động..."
sleep 30

# Chạy migrations
docker-compose up kong-migrations

echo "Đợi migrations hoàn thành..."
sleep 10

# Khởi động Kong
docker-compose up -d kong

echo "Kong đã được khởi động!"
echo "Kong Admin API: http://localhost:8001"
echo "Kong Manager UI: http://localhost:8002/manager"
echo "Kong Proxy: http://localhost:8000"
