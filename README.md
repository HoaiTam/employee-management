# Employee Management System

Mini project học Spring Boot theo 10 module. Bộ khung hiện tại hoàn thành phần setup và bài kiểm tra `/api/hello`; nghiệp vụ Employee Management sẽ được phát triển tuần tự để mỗi module vẫn có giá trị học tập.

## Công nghệ và quyết định khởi tạo

- Java 17 và Maven.
- Spring Boot 3.5.16.
- Spring MVC/REST, Spring Data JPA, Bean Validation, Thymeleaf.
- Actuator và Spring Cache cho module nâng cao.
- H2 in-memory ở profile `dev`, PostgreSQL ở profile `prod`.
- Spring Security chưa được thêm vào build; sẽ thêm ở Module 9 để tránh cơ chế đăng nhập mặc định che khuất các bài học trước đó.

## Chạy dự án

Yêu cầu JDK 17+. Nếu IDE đã cấu hình Maven, chạy class `EmployeeManagementApplication` hoặc dùng terminal:

```powershell
./mvnw.cmd spring-boot:run
```

Mặc định ứng dụng dùng profile `dev` và H2, không cần cài database. Kiểm tra:

- `GET http://localhost:8080/api/hello`
- `GET http://localhost:8080/actuator/health`
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:employee_management`
  - User: `sa`
  - Password: để trống

Chạy test:

```powershell
./mvnw.cmd test
```

## PostgreSQL (dùng từ Module 4)

Khởi động database tùy chọn:

```powershell
docker compose up -d postgres
```

Sau đó đặt các biến môi trường tương ứng `.env.example` và chạy profile `prod`. Profile này dùng `ddl-auto=validate`, nên chỉ dùng sau khi schema đã được tạo trong Module 4.

## Lộ trình triển khai

1. Getting Started: project structure, auto-configuration, `/api/hello`.
2. Bean & IoC: `UtilityService`, custom `@Bean`, constructor injection.
3. REST API: Employee in-memory và các endpoint cơ bản.
4. Data JPA: Employee, Department, repository, CRUD và search.
5. Validation & Exception Handling.
6. MVC + Thymeleaf.
7. Logging & Profiles.
8. Actuator, scheduling và caching.
9. Spring Security, role ADMIN/USER và JWT.
10. Reporting & Analytics.

Không tạo trước `entity`, `repository`, `service`, DTO hay security config: chúng sẽ xuất hiện đúng lúc trong từng module, kèm giải thích về tư duy thiết kế và công nghệ.
