# Product Service - Authorization Implementation

## Tổng quan

Product-Service đã được cấu hình để nhận thông tin phân quyền từ Gateway-Service thông qua HTTP headers. Đây là một kiến trúc phân quyền tập trung, nơi Gateway xử lý authentication và truyền thông tin user xuống các microservices.

## Kiến trúc phân quyền

### 1. Gateway-Service (Authentication Layer)
- **AuthenticationFilter**: Validate JWT token với Auth-Service
- **Truyền thông tin user**: Thêm headers `X-User-Id`, `X-User-Email`, `X-User-Role` vào request
- **Public endpoints**: Bỏ qua authentication cho `/api/auth/**`, `/oauth2/**`, `/actuator/**`

### 2. Product-Service (Authorization Layer)
- **GatewayAuthenticationFilter**: Đọc user info từ headers và tạo SecurityContext
- **SecurityConfig**: Cấu hình Spring Security với custom filter
- **Manual Authorization**: Kiểm tra role trong từng controller method

## Cấu hình Security

### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // Cấu hình SecurityFilterChain với GatewayAuthenticationFilter
}
```

### GatewayAuthenticationFilter.java
- Đọc headers: `X-User-Id`, `X-User-Email`, `X-User-Role`
- Tạo `UserPrincipal` object
- Set vào `SecurityContextHolder`

### SecurityUtils.java
- Utility class để lấy thông tin user hiện tại
- Methods: `getCurrentUser()`, `isAdmin()`, `isUser()`

## Phân quyền theo Role

### ADMIN Role
- ✅ Tạo sản phẩm (`POST /api/v1/products`)
- ✅ Cập nhật sản phẩm (`PUT /api/v1/products/{id}`)
- ✅ Xóa sản phẩm (`DELETE /api/v1/products/{id}`)
- ✅ Tăng số lượng (`PATCH /api/v1/products/{id}/increase-quantity`)
- ✅ Giảm số lượng (`PATCH /api/v1/products/{id}/reduce-quantity`)
- ✅ Cập nhật trạng thái (`PATCH /api/v1/products/{id}/status`)
- ✅ Xem sản phẩm (`GET /api/v1/products/**`)

### USER Role
- ❌ Tạo sản phẩm
- ❌ Cập nhật sản phẩm
- ❌ Xóa sản phẩm
- ❌ Tăng số lượng
- ✅ Giảm số lượng (cho việc đặt hàng)
- ❌ Cập nhật trạng thái
- ✅ Xem sản phẩm

## Flow hoạt động

1. **Client** gửi request với JWT token trong Authorization header
2. **Gateway** validate token với Auth-Service
3. **Gateway** thêm user info vào headers và forward request
4. **Product-Service** đọc headers và tạo SecurityContext
5. **Controller** kiểm tra role và thực hiện business logic

## Testing

### Test với Postman/curl

```bash
# 1. Login để lấy JWT token
curl -X POST http://localhost:9091/api/auth/login

# 2. Sử dụng token để gọi Product API
curl -X GET http://localhost:9091/api/product/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 3. Test tạo sản phẩm (cần ADMIN role)
curl -X POST http://localhost:9091/api/product \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","price":100}'
```

## Lưu ý quan trọng

1. **Security Headers**: Product-Service chỉ tin tưởng requests từ Gateway
2. **No Direct Access**: Không nên expose Product-Service trực tiếp ra ngoài
3. **Role Mapping**: Đảm bảo role trong JWT khớp với role check trong code
4. **Error Handling**: Trả về HTTP 403 khi không có quyền

## Troubleshooting

### Lỗi thường gặp:
- **403 Forbidden**: User không có role phù hợp
- **401 Unauthorized**: Token không hợp lệ hoặc hết hạn
- **Missing Headers**: Request không đi qua Gateway

### Debug:
- Check logs của Gateway-Service và Product-Service
- Verify JWT token payload
- Kiểm tra headers trong request
