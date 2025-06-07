# Script dọn dẹp Kong hoàn toàn
Write-Host "=== DỌNP DẸP KONG HOÀN TOÀN ===" -ForegroundColor Red

Write-Host "1. Dừng tất cả containers..." -ForegroundColor Yellow
docker-compose down --remove-orphans

Write-Host "2. Xóa TẤT CẢ containers Kong (kể cả thừa)..." -ForegroundColor Yellow
$kongContainers = docker ps -aq --filter "name=kong"
if ($kongContainers) {
    docker stop $kongContainers 2>$null
    docker rm $kongContainers 2>$null
    Write-Host "Đã xóa containers: $($kongContainers -join ', ')" -ForegroundColor Green
} else {
    Write-Host "Không có Kong containers nào" -ForegroundColor Green
}

Write-Host "3. Xóa Kong volumes (dữ liệu PostgreSQL cũ)..." -ForegroundColor Yellow
docker volume rm config_kong_db_data 2>$null
docker volume ls | Select-String "kong" | ForEach-Object {
    $volumeName = ($_ -split '\s+')[1]
    docker volume rm $volumeName 2>$null
    Write-Host "Đã xóa volume: $volumeName" -ForegroundColor Green
}

Write-Host "4. Dọn dẹp images không sử dụng..." -ForegroundColor Yellow
docker image prune -f

Write-Host "5. Dọn dẹp networks..." -ForegroundColor Yellow
docker network prune -f

Write-Host "=== HOÀN THÀNH DỌNP DẸP ===" -ForegroundColor Green
Write-Host "Bây giờ có thể chạy: .\restart-kong.ps1" -ForegroundColor Cyan
