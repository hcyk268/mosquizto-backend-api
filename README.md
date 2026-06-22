# Mosquizto Backend API

Backend cho ứng dụng Mosquizto, xây dựng bằng Spring Boot để phục vụ các tính năng học bằng flashcard, quản lý bộ sưu tập, lớp học, thư mục, phiên học, thông báo thời gian thực và gợi ý nội dung.

## 1. Tổng quan

Project này cung cấp API cho các nhóm chức năng chính:

- Xác thực người dùng bằng email/mật khẩu, JWT và Google Sign-In.
- Quản lý người dùng, hồ sơ cá nhân, avatar, theo dõi người dùng khác.
- Quản lý `collection` (bộ flashcard) và `collection item`.
- Quản lý thư mục (`folder`) và khóa học/lớp học (`course`).
- Chia sẻ bộ sưu tập, gửi lời mời tham gia, duyệt yêu cầu tham gia.
- Theo dõi tiến độ học, phiên học (`study session`), streak và achievement.
- Tìm kiếm collection bằng Meilisearch.
- Gợi ý collection bằng vector search với Qdrant + embedding ONNX.
- Gửi email xác thực, quên mật khẩu, thông báo liên quan đến chia sẻ/report.
- Thông báo thời gian thực qua WebSocket/STOMP.
- Ký upload media lên Cloudinary.

## 2. Công nghệ sử dụng

- Java 17
- Spring Boot 3.2.4
- Spring Web, Validation, Security, Data JPA, Redis, WebSocket, Mail, Thymeleaf, AOP
- PostgreSQL
- Flyway Migration
- Redis Cache / Token store / Rate limit support
- Meilisearch cho tìm kiếm
- Qdrant cho vector database
- ONNX Runtime + HuggingFace tokenizer cho embedding
- Cloudinary cho media upload
- Docker và Docker Compose
- Swagger / OpenAPI (`springdoc-openapi`)

## 3. Kiến trúc phụ thuộc

Khi chạy đầy đủ bằng Docker Compose, hệ thống gồm các service sau:

- `app`: ứng dụng Spring Boot
- `postgres`: cơ sở dữ liệu chính
- `redis`: cache, token, rate limit
- `meilisearch`: tìm kiếm collection
- `qdrant`: vector database cho recommendation

Ngoài ra, ứng dụng còn dùng:

- SMTP hoặc SendGrid để gửi email
- Cloudinary để ký upload ảnh/media
- Google OAuth client ID để đăng nhập Google

## 4. Yêu cầu môi trường

- JDK 17
- Maven Wrapper đi kèm project (`mvnw`, `mvnw.cmd`)
- Docker Desktop và Docker Compose nếu chạy bằng container
- Kết nối Internet khi build Docker lần đầu, vì Dockerfile tải model embedding từ Hugging Face

## 5. Cấu hình môi trường

### 5.1. Tạo file `.env`

Từ thư mục gốc project:

```bash
cp .env.example .env
```

PowerShell:

```powershell
Copy-Item .env.example .env
```

### 5.2. Các biến môi trường cần cấu hình

| Biến | Bắt buộc | Mô tả |
| --- | --- | --- |
| `DB_NAME` | Có | Tên database PostgreSQL |
| `DB_USER` | Có | User PostgreSQL |
| `DB_PASSWORD` | Có | Password PostgreSQL |
| `REDIS_HOST` | Có | Host Redis. Khi chạy Docker Compose nên là `redis` |
| `REDIS_PORT` | Có | Port Redis, mặc định `6379` |
| `APP_MAIL_PROVIDER` | Có | `smtp` hoặc `sendgrid` |
| `MAIL_ADDRESS` | Có | Email gửi đi, thường trùng tài khoản SMTP |
| `MAIL_USERNAME` | Có | Username SMTP |
| `MAIL_PASSWORD` | Có | Password SMTP hoặc Gmail App Password |
| `MAIL_FROM` | Nên có | Email hiển thị ở phần người gửi |
| `MAIL_NAME` | Nên có | Tên hiển thị của người gửi, ví dụ `Mosquizto Support` |
| `SEND_GRID` | Khi dùng SendGrid | API key SendGrid |
| `JWT_ACCESS_KEY` | Có | Secret cho access token |
| `JWT_REFRESH_KEY` | Có | Secret cho refresh token |
| `JWT_RESET_KEY` | Có | Secret cho reset password token |
| `GOOGLE_CLIENT_ID` | Có nếu dùng Google login | Client ID từ Google Console |
| `MEILI_MASTER_KEY` | Có | Master key của Meilisearch |
| `MEILI_SEARCH_HOST` | Có | URL Meilisearch, ví dụ `http://meilisearch:7700` hoặc `http://localhost:7700` |
| `VPS_IP` | Có khi chạy Docker | Dùng để tạo `SERVER_URL`, local có thể để `localhost` |
| `SERVER_NAME` | Nên có | Tên server hiển thị trong Swagger/OpenAPI |
| `WS_MESSAGE_TIME_OUT` | Có | Timeout WebSocket message |
| `WS_CONNECT_ENDPOINT` | Có | Endpoint kết nối WebSocket, mặc định `/ws-connection` |
| `CLOUDINARY_NAME` | Có nếu dùng upload media | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Có nếu dùng upload media | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Có nếu dùng upload media | Cloudinary API secret |

### 5.3. Gợi ý sinh secret key

```bash
openssl rand -base64 48
```

Nên tạo riêng cho từng biến:

- `JWT_ACCESS_KEY`
- `JWT_REFRESH_KEY`
- `JWT_RESET_KEY`
- `MEILI_MASTER_KEY`

## 6. Chạy nhanh bằng Docker Compose

Đây là cách nên dùng để khởi động đầy đủ hệ thống vì Dockerfile đã tự tải model AI cần thiết cho recommendation.

### 6.1. Khởi động

```bash
docker compose up -d --build
```

Lệnh trên sẽ:

- Build image cho ứng dụng Spring Boot
- Tải model embedding ONNX và tokenizer trong lúc build
- Khởi động `postgres`, `redis`, `meilisearch`, `qdrant`, `app`
- Tự chạy Flyway migration khi ứng dụng start

### 6.2. Kiểm tra trạng thái

Swagger UI:

- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

OpenAPI JSON:

- [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Xem log ứng dụng:

```bash
docker logs -f mosquizto_app
```

Xem danh sách container:

```bash
docker compose ps
```

### 6.3. Dừng hệ thống

```bash
docker compose down
```

Nếu muốn xóa luôn volume dữ liệu:

```bash
docker compose down -v
```

## 7. Chạy trực tiếp bằng Maven hoặc IDE

Bạn có thể chạy ứng dụng không cần Docker cho service `app`, nhưng phải tự chuẩn bị toàn bộ dependency bên ngoài.

### 7.1. Cần có sẵn các dịch vụ sau

- PostgreSQL
- Redis
- Meilisearch
- Qdrant

### 7.2. Cần export thủ công biến môi trường Spring

Khi chạy trực tiếp, project hiện tại không tự nạp file `.env`. Bạn cần tự set các biến môi trường trước khi chạy.

PowerShell ví dụ:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/mosquiztodb?options=-c%20timezone=Asia/Ho_Chi_Minh"
$env:SPRING_DATASOURCE_USERNAME="your_db_user"
$env:SPRING_DATASOURCE_PASSWORD="your_db_password"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:SPRING_MAIL_ADDRESS="your_email@gmail.com"
$env:SPRING_MAIL_USERNAME="your_email@gmail.com"
$env:SPRING_MAIL_PASSWORD="your_gmail_app_password"
$env:APP_MAIL_PROVIDER="smtp"
$env:SEND_GRID=""
$env:JWT_ACCESS_KEY="your_access_key"
$env:JWT_REFRESH_KEY="your_refresh_key"
$env:JWT_RESET_KEY="your_reset_key"
$env:GOOGLE_CLIENT_ID="your_google_client_id"
$env:SERVER_URL="http://localhost:8080/"
$env:SERVER_NAME="local"
$env:MEILI_SEARCH_HOST="http://localhost:7700"
$env:MEILI_MASTER_KEY="your_meili_master_key"
$env:QDRANT_HOST="localhost"
$env:QDRANT_PORT="6334"
$env:WS_MESSAGE_TIME_OUT="500"
$env:WS_CONNECT_ENDPOINT="/ws-connection"
$env:CLOUDINARY_NAME="your_cloudinary_name"
$env:CLOUDINARY_API_KEY="your_cloudinary_api_key"
$env:CLOUDINARY_API_SECRET="your_cloudinary_api_secret"
```

### 7.3. Tải model embedding thủ công

Đây là bước dễ bị bỏ sót nhất. `EmbeddingServiceImpl` nạp model từ classpath tại:

- `src/main/resources/models/model.onnx`
- `src/main/resources/models/tokenizer.json`

Trong repo hiện tại, các file này không được commit sẵn mà chỉ được tải trong quá trình build Docker. Nếu chạy bằng Maven/IDE, bạn cần tự tạo thư mục và tải file trước.

PowerShell:

```powershell
New-Item -ItemType Directory -Force src\main\resources\models | Out-Null
Invoke-WebRequest "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx" -OutFile "src\main\resources\models\model.onnx"
Invoke-WebRequest "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/tokenizer.json" -OutFile "src\main\resources\models\tokenizer.json"
Invoke-WebRequest "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/tokenizer_config.json" -OutFile "src\main\resources\models\tokenizer_config.json"
```

Nếu bỏ qua bước này, ứng dụng có thể lỗi khi khởi động ở giai đoạn `ApplicationReadyEvent`.

### 7.4. Chạy ứng dụng

```bash
./mvnw spring-boot:run
```

PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## 8. Database migration

Project dùng Flyway với migration nằm tại:

- `src/main/resources/db/migration`

Khi ứng dụng khởi động, Flyway sẽ tự migrate schema PostgreSQL. Trong thư mục migration hiện có:

- Schema khởi tạo ban đầu
- Seed role mặc định
- Seed dữ liệu mô phỏng lớp học/workspace
- Bổ sung bảng cho collection, study session, folder, course, notification, follow, report
- Soft delete, index tìm kiếm và các thay đổi phục vụ recommendation

## 9. Kiểm thử

Chạy test:

```bash
./mvnw test
```

PowerShell:

```powershell
.\mvnw.cmd test
```

Lưu ý:

- Test đang dùng `src/test/resources/application-test.yml`
- Database test dùng H2 in-memory
- Flyway bị tắt trong môi trường test

## 10. Nhóm API chính

Các endpoint không có prefix `/api`; route bắt đầu trực tiếp từ root.

### 10.1. Xác thực

- `/auth/register`
- `/auth/login`
- `/auth/refresh-token`
- `/auth/logout`
- `/auth/forgot-password`
- `/auth/verify-code-forgot-password`
- `/auth/reset-password`
- `/auth/google`

### 10.2. Người dùng

- `/user/profile`
- `/user/update`
- `/user/change-password`
- `/user/avatar`
- `/user/search`
- `/user/profile/{username}`
- `/user/follow/{username}`
- `/user/followers`
- `/user/following`
- `/user/follow/notifications`

### 10.3. Collection và item

- `/collection`
- `/collection/my-list`
- `/collection/public`
- `/collection/search`
- `/collection/recent-opened`
- `/collection/create_index`
- `/collection/item`
- `/collection/item/{id}/star`
- `/collection/item/starred`

### 10.4. Chia sẻ collection

- `/user-collection/share/{collectionId}`
- `/user-collection/join/{collectionId}`
- `/user-collection/members/{collectionId}`
- `/user-collection/invitations`

### 10.5. Folder

- `/folder/create`
- `/folder/{folderId}`
- `/folder/{folderId}/collection/{collectionId}`
- `/folder/{folderId}/share`
- `/folder/{folderId}/members`

### 10.6. Course

- `/course`
- `/course/{courseId}`
- `/course/public`
- `/course/{courseId}/collection/{collectionId}`
- `/course/{courseId}/join`
- `/course/{courseId}/join-requests/pending`
- `/course/{courseId}/members`
- `/course/{courseId}/stats/best-collections-learnt`

### 10.7. Study session

- `/study-session/start`
- `/study-session/{sessionId}/answer`
- `/study-session/{sessionId}/complete`
- `/study-session/{sessionId}/complete-batch`
- `/study-session/history`
- `/study-session/stats/{collectionId}`
- `/study-session/get-jump-back-in`

### 10.8. Notification, report, media, recommendation

- `/notifications`
- `/notifications/unread-count`
- `/reports/collections/{collectionId}`
- `/reports/users/{username}`
- `/media/cloudinary/sign`
- `/recommendation/collections`
- `/recommendation/sync/collections`

Chi tiết request/response và security requirement nên xem trực tiếp trên Swagger UI.

## 11. WebSocket

Project bật STOMP over WebSocket.

- Endpoint kết nối mặc định: `/ws-connection`
- Simple broker prefix: `/topic`, `/queue`
- Application destination prefix: `/{spring.application.name}`

Với cấu hình hiện tại, `spring.application.name` là `Mosquizto-Backend`, nên prefix gửi message vào app sẽ là:

```text
/Mosquizto-Backend
```

Client cần gửi JWT access token trong header `Authorization: Bearer <token>` khi thực hiện `CONNECT`.

## 12. Cấu trúc thư mục chính

```text
.
|-- src/main/java/com/mosquizto/api
|   |-- configuration   # Security, Swagger, Redis, Meilisearch, Qdrant, WebSocket...
|   |-- controller      # REST API controllers
|   |-- service         # Interface nghiệp vụ
|   |-- service/impl    # Implementation nghiệp vụ
|   |-- repository      # Spring Data repositories
|   |-- model           # Entity và model liên quan
|   |-- dto             # Request/Response DTO
|   |-- mapper          # Mapper giữa entity và DTO
|   |-- security        # JWT, auth entry point, Google verifier
|   |-- exception       # Exception và global handler
|   `-- util            # Enum, helper, text matching, centroid...
|-- src/main/resources
|   |-- application.yml
|   |-- db/migration
|   `-- templates       # Email templates
|-- src/test
|-- Dockerfile
|-- docker-compose.yml
|-- .env.example
`-- pom.xml
```

## 13. Một số lưu ý quan trọng

- `README` cũ dùng link Swagger `/swagger-ui.html`; với `springdoc-openapi` hiện tại nên dùng `/swagger-ui/index.html`.
- `.env.example` hiện chưa liệt kê đủ một số biến đang được app sử dụng như `MAIL_FROM`, `MAIL_NAME`, `SERVER_NAME`, `CLOUDINARY_*`. Khi cấu hình thực tế, bạn nên bổ sung các biến này.
- `docker-compose.yml` map `SERVER_URL` theo dạng `http://${VPS_IP}:8080/`, vì vậy khi deploy thật cần đặt `VPS_IP` hoặc domain phù hợp.
- Recommendation phụ thuộc vào Qdrant và model embedding. Nếu chỉ cần test các API cơ bản, nên ưu tiên chạy bằng Docker để tránh lỗi thiếu model.
- Redis trong Compose đã vô hiệu hóa `FLUSHDB` và `FLUSHALL`.

## 14. Xử lý sự cố thường gặp

### 14.1. Ứng dụng lỗi khi start vì thiếu model

Nguyên nhân:

- Chạy bằng Maven/IDE nhưng chưa tải `model.onnx` và `tokenizer.json`

Cách xử lý:

- Tải model vào `src/main/resources/models` như hướng dẫn ở mục 7.3
- Hoặc chạy bằng `docker compose up -d --build`

### 14.2. Không gửi được email qua Gmail

Nguyên nhân thường gặp:

- Dùng mật khẩu Gmail thông thường thay vì App Password

Cách xử lý:

- Bật 2FA cho Gmail
- Tạo App Password tại [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
- Gán App Password vào `MAIL_PASSWORD`

### 14.3. Swagger hiển thị server URL sai

Kiểm tra các biến:

- `VPS_IP`
- `SERVER_URL`
- `SERVER_NAME`

### 14.4. Tìm kiếm hoặc recommendation không hoạt động

Kiểm tra:

- `MEILI_SEARCH_HOST`
- `MEILI_MASTER_KEY`
- `QDRANT_HOST`
- `QDRANT_PORT`
- Container `meilisearch` và `qdrant` đã chạy chưa

Nếu cần đồng bộ lại dữ liệu recommendation:

- Gọi endpoint `POST /recommendation/sync/collections`

## 15. Lệnh hữu ích

Build project:

```bash
./mvnw clean package
```

PowerShell:

```powershell
.\mvnw.cmd clean package
```

Xem log Docker Compose:

```bash
docker compose logs -f app
```

Khởi động lại ứng dụng:

```bash
docker compose restart app
```

## 16. Tài liệu tham khảo nhanh

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Meilisearch local: [http://localhost:7700](http://localhost:7700)
- Qdrant local: [http://localhost:6333](http://localhost:6333)

---

Nếu mục tiêu là chạy project nhanh và ít lỗi nhất, nên bắt đầu bằng `docker compose up -d --build`. Nếu cần debug sâu trong IDE, hãy chuẩn bị đầy đủ service phụ trợ và model embedding trước khi chạy local.
