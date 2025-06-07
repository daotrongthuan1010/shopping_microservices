# Hướng dẫn sử dụng Kong

## Vấn đề và giải pháp

### Vấn đề gặp phải:
1. **Konga authentication error**: Konga không tương thích với PostgreSQL mới
2. **Database incompatible**: Kong-db volume chứa dữ liệu PostgreSQL 16, không tương thích với PostgreSQL 11
3. **Multiple Kong containers**: Có nhiều Kong containers đang chạy gây xung đột

### Giải pháp:
- **Loại bỏ Konga** và sử dụng **Kong Manager** (UI built-in)
- **Xóa hoàn toàn Kong volumes cũ** và tạo lại với PostgreSQL 11
- **Dọn dẹp tất cả Kong containers** trước khi khởi động lại
- **Kong database riêng** không ảnh hưởng đến product database (PostgreSQL 16)

## Cách sửa lỗi Kong

### BƯỚC 1: Dọn dẹp hoàn toàn (BẮT BUỘC)

**Windows (PowerShell):**
```powershell
.\cleanup-kong.ps1
```

**Linux/Mac:**
```bash
chmod +x cleanup-kong.sh
./cleanup-kong.sh
```

### BƯỚC 2: Khởi động Kong

**Windows (PowerShell):**
```powershell
.\restart-kong.ps1
```

**Linux/Mac:**
```bash
chmod +x restart-kong.sh
./restart-kong.sh
```

### Hoặc thủ công:
```bash
# Dừng các service Kong cũ
docker-compose stop konga kong kong-migrations kong-db
docker-compose rm -f konga kong kong-migrations kong-db

# Khởi động lại
docker-compose up -d kong-db
# Đợi 30 giây
docker-compose up kong-migrations
# Đợi 10 giây  
docker-compose up -d kong
```

## Truy cập Kong

- **Kong Manager UI**: http://localhost:8002/manager
- **Kong Admin API**: http://localhost:8001
- **Kong Proxy**: http://localhost:8000

## Sử dụng Kong Manager

1. Mở trình duyệt và truy cập: http://localhost:8002/manager
2. Tạo workspace mới hoặc sử dụng default workspace
3. Quản lý Services, Routes, Plugins từ giao diện web

## API Examples

### Tạo Service:
```bash
curl -i -X POST http://localhost:8001/services \
  --data "name=my-service" \
  --data "url=http://httpbin.org"
```

### Tạo Route:
```bash
curl -i -X POST http://localhost:8001/services/my-service/routes \
  --data "hosts[]=example.com"
```

### Test:
```bash
curl -i -X GET http://localhost:8000/ \
  --header "Host: example.com"
```

## Troubleshooting

Nếu vẫn có lỗi, kiểm tra logs:
```bash
docker-compose logs kong
docker-compose logs kong-db
```
