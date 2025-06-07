# Script để khởi động lại Kong services
Write-Host "Dừng tất cả containers..." -ForegroundColor Yellow

# Dừng tất cả containers trước
docker-compose down

Write-Host "Dừng và xóa các Kong containers cũ (bao gồm cả containers thừa)..." -ForegroundColor Yellow

# Dừng và xóa TẤT CẢ containers Kong (kể cả thừa)
docker stop $(docker ps -aq --filter "name=kong") 2>$null
docker rm $(docker ps -aq --filter "name=kong") 2>$null

Write-Host "Xóa Kong volumes cũ (QUAN TRỌNG: Xóa dữ liệu PostgreSQL 16 cũ)..." -ForegroundColor Red
docker volume rm config_kong_db_data 2>$null
docker volume prune -f

Write-Host "Khởi động lại Kong services..." -ForegroundColor Green

# Khởi động Kong database trước
docker-compose up -d kong-db

Write-Host "Đợi Kong database khởi động..." -ForegroundColor Cyan
Start-Sleep -Seconds 30

# Chạy migrations
docker-compose up kong-migrations

Write-Host "Đợi migrations hoàn thành..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# Khởi động Kong
docker-compose up -d kong

Write-Host "Kong đã được khởi động!" -ForegroundColor Green
Write-Host "Kong Admin API: http://localhost:8001" -ForegroundColor White
Write-Host "Kong Manager UI: http://localhost:8002/manager" -ForegroundColor White
Write-Host "Kong Proxy: http://localhost:8000" -ForegroundColor White
