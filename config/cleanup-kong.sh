#!/bin/bash

# Script dọn dẹp Kong hoàn toàn
echo "=== DỌNP DẸP KONG HOÀN TOÀN ==="

echo "1. Dừng tất cả containers..."
docker-compose down --remove-orphans

echo "2. Xóa TẤT CẢ containers Kong (kể cả thừa)..."
kong_containers=$(docker ps -aq --filter "name=kong")
if [ ! -z "$kong_containers" ]; then
    docker stop $kong_containers 2>/dev/null
    docker rm $kong_containers 2>/dev/null
    echo "Đã xóa containers: $kong_containers"
else
    echo "Không có Kong containers nào"
fi

echo "3. Xóa Kong volumes (dữ liệu PostgreSQL cũ)..."
docker volume rm config_kong_db_data 2>/dev/null
docker volume ls | grep kong | awk '{print $2}' | xargs -r docker volume rm 2>/dev/null

echo "4. Dọn dẹp images không sử dụng..."
docker image prune -f

echo "5. Dọn dẹp networks..."
docker network prune -f

echo "=== HOÀN THÀNH DỌNP DẸP ==="
echo "Bây giờ có thể chạy: ./restart-kong.sh"
