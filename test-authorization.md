# Test Authorization cho Product-Service

## Chuẩn bị

1. **Khởi động các services theo thứ tự:**
   ```bash
   # 1. Service Registry
   cd Service-Registry
   ./gradlew bootRun
   
   # 2. Auth Service  
   cd Auth-Service
   ./gradlew bootRun
   
   # 3. Gateway Service
   cd Gateway-Service
   ./gradlew bootRun
   
   # 4. Product Service
   cd Product-Service
   ./gradlew bootRun
   ```

2. **Kiểm tra services đã chạy:**
   - Service Registry: http://localhost:8761
   - Auth Service: http://localhost:8084
   - Gateway Service: http://localhost:9091
   - Product Service: http://localhost:8099

## Test Cases

### 1. Test Authentication Flow

#### 1.1 Login với GitHub OAuth
```bash
# Mở browser và truy cập:
http://localhost:9091/oauth2/authorization/github

# Sau khi login thành công, lấy JWT token từ redirect URL
```

#### 1.2 Test token validation
```bash
curl -X POST http://localhost:8084/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{"token":"YOUR_JWT_TOKEN"}'
```

### 2. Test Product API qua Gateway

#### 2.1 Test GET product (cần authentication)
```bash
# Không có token - expect 401
curl -X GET http://localhost:9091/api/product/1

# Có token hợp lệ - expect 200 hoặc 404
curl -X GET http://localhost:9091/api/product/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 2.2 Test CREATE product (cần ADMIN role)
```bash
# User role - expect 403
curl -X POST http://localhost:9091/api/product \
  -H "Authorization: Bearer USER_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "price": 100.00,
    "description": "Test Description",
    "categoryId": 1,
    "brandId": 1
  }'

# Admin role - expect 201
curl -X POST http://localhost:9091/api/product \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "price": 100.00,
    "description": "Test Description", 
    "categoryId": 1,
    "brandId": 1
  }'
```

### 3. Test Direct Access (Should Fail)

#### 3.1 Bypass Gateway - expect no user headers
```bash
# Direct call to Product Service - expect authentication failure
curl -X GET http://localhost:8099/api/v1/products/1
```

## Expected Results

### ✅ Success Cases:
- Gateway validates token successfully
- User headers are added to request
- Product Service receives user info
- Authorization checks pass for correct roles

### ❌ Failure Cases:
- No token → 401 Unauthorized
- Invalid token → 401 Unauthorized  
- Wrong role → 403 Forbidden
- Direct access → No user context

## Debug Commands

### Check Gateway logs:
```bash
# In Gateway-Service directory
tail -f logs/application.log
```

### Check Product Service logs:
```bash
# In Product-Service directory  
tail -f logs/application.log
```

### Verify JWT token payload:
```bash
# Decode JWT token (use online JWT decoder or jq)
echo "YOUR_JWT_TOKEN" | cut -d. -f2 | base64 -d | jq
```

## Manual Testing với Postman

1. **Import collection**: Tạo Postman collection với các endpoints
2. **Set environment variables**: 
   - `gateway_url`: http://localhost:9091
   - `jwt_token`: {{token_from_login}}
3. **Test scenarios**: Chạy từng test case theo thứ tự

## Kết quả mong đợi

Sau khi hoàn thành, hệ thống sẽ có:
- ✅ Centralized authentication tại Gateway
- ✅ Distributed authorization tại từng service
- ✅ Role-based access control
- ✅ Secure communication giữa services
- ✅ Proper error handling và logging
