## 🛠️ Hướng dẫn cài đặt & Chạy ứng dụng

### Bước 1: Clone source code

```bash
git clone https://github.com/hcyk268/mosquizto-backend-api.git
cd mosquizto-api
```

### Bước 2: Cấu hình biến môi trường

Dự án cần một file `.env` để cấu hình database, email và JWT token.
Đã có sẵn một file mẫu gốc tên là `.env.example`. Chạy lệnh sau để tạo file `.env`:

**Linux/macOS/Git Bash:**
```bash
cp .env.example .env
```
**Windows (PowerShell):**
```powershell
Copy-Item .env.example .env
```

Mở file `.env` vừa tạo và điền các thông tin thực tế:
- **Database:** Tạo mật khẩu cho DB (`DB_PASSWORD`).
- **Mail SMTP:** Điền email Gmail của bạn và [Tạo App Password](https://myaccount.google.com/apppasswords) của Gmail để điền vào `MAIL_PASSWORD`.
- **JWT Keys:** Thay thế các placeholder bằng chuỗi ngẫu nhiên (hoặc chuỗi Base64).
- **VPS_IP:** Nếu chạy ở máy tính cá nhân (local), cứ để mặc định là `localhost`.

### Bước 3: Khởi động bằng Docker Compose

Mở terminal tại thư mục gốc của project (nơi chứa file `docker-compose.yml`) và chạy:

```bash
docker compose up -d
```



## 🔍 Kiểm tra trạng thái

- **Xem API có chạy không:** Truy cập http://localhost:8080/swagger-ui.html để xem và test toàn bộ tài liệu API.
- **Xem log của ứng dụng:**
  ```bash
  docker logs -f mosquizto_app
  ```
- **Tắt toàn bộ hệ thống:**
  ```bash
  docker compose down
  ```