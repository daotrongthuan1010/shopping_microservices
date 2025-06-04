# Authentication Service

Microservice xử lý authentication và authorization sử dụng OAuth2 GitHub và JWT tokens.

## Tính năng

- OAuth2 login với GitHub
- JWT token generation và validation
- User management
- Refresh token functionality
- Role-based access control (USER, ADMIN)

## Cài đặt

### 1. Cấu hình GitHub OAuth App

1. Truy cập GitHub Settings > Developer settings > OAuth Apps
2. Chọn OAuth App hiện có hoặc tạo mới với:
   - Application name: `Gateway Login App` (hoặc tên bạn muốn)
   - Homepage URL: `http://localhost:8084`
   - Authorization callback URL: `http://localhost:8084/oauth2/callback/github`
3. Client ID đã có: `Ov23l1PpstZ09LdPgcvV`
4. Copy Client Secret từ phần "Client secrets" (secret bắt đầu bằng `73606890b2...`)

**Quan trọng**: Đảm bảo Authorization callback URL chính xác là:
```
http://localhost:8084/oauth2/callback/github
```

### 2. Cấu hình Environment Variables

Tạo file `.env` hoặc set environment variables:

```bash
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
JWT_SECRET=myVerySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong
FRONTEND_URL=http://localhost:3000
```

### 3. Cấu hình Database

Tạo MySQL database:

```sql
CREATE DATABASE auth_service_db;
```

Hoặc update `application.yml` với database connection của bạn.

### 4. Chạy Service

```bash
./gradlew bootRun
```

Service sẽ chạy trên port 8084.

## API Endpoints

### Authentication
- `GET /oauth2/authorize/github` - Bắt đầu OAuth2 flow với GitHub
- `POST /api/auth/validate` - Validate JWT token
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/me` - Lấy thông tin user hiện tại
- `GET /api/auth/health` - Health check

### Admin Only
- `GET /api/auth/users` - Lấy danh sách tất cả users

## Cách sử dụng

### 1. Frontend Integration

Redirect user đến GitHub OAuth:
```javascript
window.location.href = 'http://localhost:8084/oauth2/authorize/github';
```

### 2. Handle OAuth Callback

Sau khi user authorize, họ sẽ được redirect về frontend với tokens:
```
http://localhost:3000/oauth2/redirect?token=jwt_access_token&refreshToken=jwt_refresh_token
```

### 3. Validate Token

```bash
curl -X POST http://localhost:8084/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{"token": "your_jwt_token"}'
```

### 4. Use Token in Requests

```bash
curl -H "Authorization: Bearer your_jwt_token" \
  http://localhost:8084/api/auth/me
```

## Database Schema

### Users Table
- `id` (BIGINT, PRIMARY KEY)
- `email` (VARCHAR, UNIQUE, NOT NULL)
- `name` (VARCHAR, NOT NULL)
- `github_id` (VARCHAR, UNIQUE)
- `avatar_url` (VARCHAR)
- `github_username` (VARCHAR)
- `role` (ENUM: USER, ADMIN)
- `is_active` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## Security

- JWT tokens có thời hạn 24 giờ
- Refresh tokens có thời hạn 7 ngày
- CORS được cấu hình cho development
- Stateless session management

## Testing

Chạy tests:
```bash
./gradlew test
```

## Troubleshooting

**Lỗi thường gặp và cách sửa:**

- **JAVA_HOME error**: Đảm bảo Project SDK được set đúng (File > Project Structure > Project > Project SDK)
- **Database connection**: Kiểm tra MySQL đang chạy và database `auth_service_db` đã tạo
- **OAuth errors**: Kiểm tra GitHub Client ID/Secret và callback URL chính xác
- **Eureka registration**: Service tự động register với Eureka (không cần `@EnableEurekaClient`)
- **JWT validation**: Đảm bảo JWT secret đủ dài (ít nhất 256 bits)

**Các lỗi đã được sửa:**
- ✅ `ResponseEntity.unauthorized()` → `ResponseEntity.status(HttpStatus.UNAUTHORIZED)`
- ✅ `@EnableEurekaClient` deprecated → Tự động discovery
- ✅ Thêm Global Exception Handler
- ✅ Cải thiện error handling

**IntelliJ IDEA Setup:**
1. Import project as Gradle project
2. Set Project SDK to Java 17
3. Enable annotation processing for Lombok
4. Set environment variables trong Run Configuration
5. Đảm bảo MySQL service đang chạy
