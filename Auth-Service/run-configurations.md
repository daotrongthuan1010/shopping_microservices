# IntelliJ IDEA Run Configurations

## Cách chạy Auth Service trong IntelliJ IDEA

### 1. Import Project
1. Mở IntelliJ IDEA
2. File > Open > Chọn thư mục `Auth-Service`
3. Chọn "Import Gradle project"
4. Đợi Gradle sync hoàn thành

### 2. Cấu hình Environment Variables
Trong IntelliJ IDEA:
1. Run > Edit Configurations
2. Tạo new Application configuration
3. Main class: `microservices.authservice.authservice.AuthServiceApplication`
4. Environment variables:
   ```
   GITHUB_CLIENT_ID=Ov23l1PpstZ09LdPgcvV
   GITHUB_CLIENT_SECRET=your-github-client-secret-here
   JWT_SECRET=myVerySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong
   FRONTEND_URL=http://localhost:3000
   ```

### 3. Cấu hình Database
Đảm bảo MySQL đang chạy và có database `auth_service_db`:

```sql
CREATE DATABASE auth_service_db;
```

### 4. Chạy với Profile

**Option 1: Local Profile (Khuyến nghị cho development)**
- VM options: `-Dspring.profiles.active=local`
- Hoặc Program arguments: `--spring.profiles.active=local`
- Sử dụng H2 in-memory database (không cần MySQL)
- Disable Eureka và Config Server

**Option 2: Dev Profile (Cần MySQL)**
- VM options: `-Dspring.profiles.active=dev`
- Cần MySQL database và có thể cần Eureka Server

**Option 3: Default Profile**
- Không set profile, sử dụng application.yml mặc định
- Cần tất cả services (MySQL, Eureka, Config Server)

### 5. GitHub OAuth Setup
Trong GitHub OAuth App settings, thêm Authorization callback URL:
```
http://localhost:8084/oauth2/callback/github
```

### 6. Test Endpoints
Sau khi service chạy thành công:

- Health check: `GET http://localhost:8084/api/auth/health`
- OAuth login: `GET http://localhost:8084/oauth2/authorize/github`
- Eureka: Service sẽ register với Eureka tại `http://localhost:8761`

### 7. Troubleshooting
- Nếu gặp lỗi JAVA_HOME: Đảm bảo Project SDK được set đúng (File > Project Structure > Project > Project SDK)
- Nếu gặp lỗi database: Kiểm tra MySQL connection và database đã tạo
- Nếu gặp lỗi OAuth: Kiểm tra GitHub Client ID/Secret và callback URL
