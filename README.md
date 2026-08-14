# Employee Management System

Mini project quản lý nhân viên được xây dựng để học Spring Boot theo 10 module, từ khởi tạo ứng dụng đến REST API, JPA, Thymeleaf, Security và Reporting.

Dự án hiện đã hoàn thành toàn bộ Module 1–10. Ứng dụng cung cấp cả giao diện web render phía server và REST API có xác thực/phân quyền.

## Chức năng chính

- Quản lý nhân viên: xem danh sách, tìm kiếm, thêm, sửa và xóa qua REST API.
- Tìm kiếm nhân viên theo tên và phòng ban.
- Quản lý quan hệ giữa `Employee` và `Department` bằng Spring Data JPA.
- Validation request và xử lý lỗi tập trung bằng `@ControllerAdvice`.
- Giao diện Thymeleaf cho đăng nhập, đăng ký, danh sách và thêm nhân viên.
- Xác thực bằng Form Login, HTTP Basic hoặc JWT.
- Phân quyền:
  - `USER`: xem danh sách và báo cáo.
  - `ADMIN`: có toàn quyền CRUD nhân viên và xem các Actuator endpoint nâng cao.
- Báo cáo tổng số nhân viên và số lượng theo từng phòng ban.
- Caching báo cáo tổng số nhân viên bằng Caffeine trong 1 phút.
- Actuator, logging theo profile và scheduled task ghi `System running` mỗi 30 giây.

## Công nghệ

- Java 17
- Spring Boot 3.5.16
- Spring MVC và REST API
- Spring Data JPA / Hibernate
- Bean Validation
- Thymeleaf và Thymeleaf Spring Security
- Spring Security, OAuth2 Resource Server và JWT HS256
- Spring Boot Actuator
- Spring Cache và Caffeine
- H2 cho môi trường phát triển
- PostgreSQL cho môi trường production
- Maven Wrapper
- JUnit 5, MockMvc, AssertJ và Spring Security Test

## Kiến trúc

```text
HTTP Request
     │
     ▼
Controller ─────► DTO / Validation
     │
     ▼
Service ────────► Business rules, transaction, cache
     │
     ▼
Repository ─────► Spring Data JPA / @Query
     │
     ▼
H2 hoặc PostgreSQL
```

Các package chính:

```text
src/main/java/com/example/employeemanagement
├── config       # Bean, cache, scheduling, security và JWT
├── controller   # REST controller và MVC controller
├── dto          # Request/response objects
├── exception    # Exception nghiệp vụ và global error handler
├── model        # JPA entities và Role
├── repository   # Spring Data repositories và query báo cáo
├── scheduler    # Scheduled task
├── service      # Nghiệp vụ, transaction và mapping DTO
└── util         # Tiện ích sinh/format mã nhân viên
```

## Chạy nhanh với profile `dev`

### Yêu cầu

- JDK 17 trở lên.
- Không cần cài Maven vì dự án đã có Maven Wrapper.
- Docker chỉ cần thiết nếu muốn chạy PostgreSQL.

Kiểm tra Java:

```powershell
java -version
```

Chạy ứng dụng trên Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Trên Linux/macOS:

```bash
./mvnw spring-boot:run
```

Mặc định ứng dụng chạy tại `http://localhost:8080` với profile `dev` và H2 in-memory. Database được tạo lại sau mỗi lần khởi động ứng dụng.

### Tài khoản phát triển

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin123!` | `ADMIN` |
| `user` | `User123!` | `USER` |

Các tài khoản này chỉ được tự động tạo trong profile `dev`. Có thể thay mật khẩu bằng biến môi trường `DEV_ADMIN_PASSWORD` và `DEV_USER_PASSWORD`.

### Các trang web

| URL | Quyền | Mô tả |
|---|---|---|
| `/login` | Public | Đăng nhập bằng form |
| `/register` | Public | Đăng ký tài khoản mới với role `USER` |
| `/employees/list` | `USER`, `ADMIN` | Danh sách và tìm kiếm nhân viên |
| `/employees/add` | `ADMIN` | Form thêm nhân viên |
| `/employees/statistics` | `USER`, `ADMIN` | Thống kê nhân viên |
| `/actuator/health` | Public | Trạng thái ứng dụng |
| `/actuator/info` | Public | Thông tin ứng dụng |
| `/h2-console` | Dev only | H2 database console |

Thông tin kết nối H2:

```text
JDBC URL: jdbc:h2:mem:employee_management
User: sa
Password: để trống
```

## REST API

Các API `GET` cho phép `USER` hoặc `ADMIN`. Các API thay đổi dữ liệu yêu cầu `ADMIN`.

| Method | Endpoint | Quyền | Chức năng |
|---|---|---|---|
| `POST` | `/api/auth/token` | Public | Đăng nhập và nhận JWT |
| `GET` | `/api/hello` | `USER`, `ADMIN` | Kiểm tra ứng dụng |
| `GET` | `/api/module2/employee-preview` | `USER`, `ADMIN` | Demo custom bean và IoC |
| `GET` | `/api/employees` | `USER`, `ADMIN` | Danh sách/tìm kiếm nhân viên |
| `GET` | `/api/employees/{id}` | `USER`, `ADMIN` | Chi tiết nhân viên |
| `POST` | `/api/employees` | `ADMIN` | Thêm nhân viên |
| `PUT` | `/api/employees/{id}` | `ADMIN` | Cập nhật nhân viên |
| `DELETE` | `/api/employees/{id}` | `ADMIN` | Xóa nhân viên |
| `GET` | `/api/reports/employees/total` | `USER`, `ADMIN` | Tổng số nhân viên |
| `GET` | `/api/reports/employees/by-department` | `USER`, `ADMIN` | Số nhân viên theo phòng ban |

API danh sách hỗ trợ hai query parameter tùy chọn:

```text
GET /api/employees?name=an&departmentId=1
```

Body dùng khi thêm hoặc cập nhật nhân viên:

```json
{
  "name": "Nguyen Van An",
  "email": "an@example.com",
  "departmentId": 1
}
```

### Gọi API bằng HTTP Basic

Đọc danh sách bằng tài khoản `USER`:

```powershell
curl.exe -u "user:User123!" `
  http://localhost:8080/api/employees
```

Thêm nhân viên bằng tài khoản `ADMIN`:

```powershell
curl.exe -u "admin:Admin123!" `
  -H "Content-Type: application/json" `
  -d '{"name":"Nguyen Van An","email":"an@example.com","departmentId":1}' `
  http://localhost:8080/api/employees
```

### Gọi API bằng JWT

Nhận access token:

```powershell
$tokenResponse = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/auth/token" `
  -ContentType "application/json" `
  -Body '{"username":"user","password":"User123!"}'
```

Sử dụng token:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/employees" `
  -Headers @{ Authorization = "Bearer $($tokenResponse.accessToken)" }
```

Access token mặc định có thời hạn 15 phút.

## Profiles và database

### Profile `dev`

- Là profile mặc định.
- Sử dụng H2 in-memory ở chế độ tương thích PostgreSQL.
- Hibernate dùng `ddl-auto=create-drop`.
- Seed ba phòng ban từ `data.sql`.
- Bật H2 Console và log SQL ở mức `DEBUG`.
- Tự động tạo tài khoản `admin` và `user`.

### Profile `prod`

- Sử dụng PostgreSQL.
- Tắt H2 Console và SQL debug log.
- Hibernate dùng `ddl-auto=validate`, không tự tạo hoặc sửa schema.
- Bắt buộc cung cấp `DB_PASSWORD` và `JWT_SECRET`.
- `JWT_SECRET` phải có ít nhất 32 byte.

Khởi động PostgreSQL:

```powershell
docker compose up -d postgres
```

`compose.yml` tạo database với password mặc định là `postgres`. Trước khi chạy profile `prod`, cần tạo schema tương ứng vì dự án hiện chưa tích hợp Flyway/Liquibase và `ddl-auto=validate` chỉ kiểm tra schema.

Ví dụ cấu hình môi trường sau khi đã có schema:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="employee_management"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="replace-with-a-secret-containing-at-least-32-bytes"

.\mvnw.cmd spring-boot:run
```

Không commit password hoặc JWT secret thật vào repository.

## Chạy test

```powershell
.\mvnw.cmd test
```

Test suite hiện có 35 test, bao phủ:

- Spring context và custom bean wiring.
- REST CRUD, validation và global exception handling.
- MVC controller và Thymeleaf.
- Logging, profiles, Actuator, scheduling và cache.
- Authentication bằng HTTP Basic, Form Login và JWT.
- Phân quyền `USER`/`ADMIN`.
- Query và trang thống kê nhân viên.

## Lộ trình 10 module

| Module | Nội dung | Kết quả trong dự án |
|---|---|---|
| 1 | Getting Started | Cấu trúc Spring Boot và `/api/hello` |
| 2 | Custom Bean & IoC | Constructor injection, `UtilityService`, custom bean |
| 3 | REST API cơ bản | Controller và API nhân viên |
| 4 | Spring Data JPA | Entity, repository, quan hệ Employee–Department và CRUD |
| 5 | Validation & Exception | Bean Validation và global error response |
| 6 | MVC + Thymeleaf | Danh sách, tìm kiếm và form thêm nhân viên |
| 7 | Logging & Profiles | SLF4J/Logback và cấu hình dev/prod |
| 8 | Advanced Spring Boot | Actuator, scheduling và Caffeine cache |
| 9 | Spring Security | Form Login, HTTP Basic, JWT và role-based authorization |
| 10 | Reporting & Analytics | Tổng số và thống kê nhân viên theo phòng ban |

## Hướng phát triển tiếp theo

- Tích hợp Flyway hoặc Liquibase để quản lý schema PostgreSQL.
- Thêm phân trang và sắp xếp cho danh sách nhân viên.
- Thêm ngày vào làm để thống kê xu hướng theo tháng/quý.
- Bổ sung chức năng sửa/xóa trên giao diện Thymeleaf.
- Tạo OpenAPI/Swagger documentation.
- Dùng Testcontainers để kiểm thử trực tiếp với PostgreSQL.
