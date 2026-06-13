## Huong dan cai dat va chay ung dung

### 1. Clone source code

```bash
git clone https://github.com/hcyk268/mosquizto-backend-api.git
cd mosquizto-api
```

### 2. Tao file `.env`

Du an dung file `.env` cho `docker compose`. File mau da co san tai `.env.example`.

**Linux/macOS/Git Bash**

```bash
cp .env.example .env
```

**Windows (PowerShell)**

```powershell
Copy-Item .env.example .env
```

Cap nhat file `.env` vua tao voi cac nhom bien sau:

- `DB_NAME`, `DB_USER`, `DB_PASSWORD`: thong tin PostgreSQL.
- `APP_MAIL_PROVIDER`: chon `smtp` hoac `sendgrid`.
- `MAIL_ADDRESS`, `MAIL_USERNAME`, `MAIL_PASSWORD`: thong tin SMTP. Nen dung Gmail App Password neu gui qua Gmail.
- `SEND_GRID`: bat buoc khi `APP_MAIL_PROVIDER=sendgrid`.
- `JWT_ACCESS_KEY`, `JWT_REFRESH_KEY`, `JWT_RESET_KEY`: khoa JWT. Co the tao bang `openssl rand -base64 32`.
- `GOOGLE_CLIENT_ID`: client id cho dang nhap Google.
- `MEILI_MASTER_KEY`: khoa cho Meilisearch container.
- `VPS_IP`: host cong khai duoc dung de tao `SERVER_URL`, de `localhost` khi chay local.
- `WS_MESSAGE_TIME_OUT`, `WS_CONNECT_ENDPOINT`: cau hinh WebSocket.

Luu y:

- `docker-compose.yml` tu map cac bien trong `.env` sang cac bien Spring nhu `SPRING_DATASOURCE_URL`, `SPRING_MAIL_USERNAME`, `SERVER_URL`.
- Project hien tai khong tu dong nap file `.env` khi chay bang `mvn spring-boot:run` hoac tu IDE.
- Neu chay app truc tiep khong qua Docker Compose, ban can export thu cong cac bien Spring sau: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_MAIL_ADDRESS`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `SPRING_REDIS_HOST`, `SERVER_URL`, `MEILI_SEARCH_HOST`, `MEILI_MASTER_KEY`, cung voi cac bien JWT, Google va WebSocket.

### 3. Khoi dong bang Docker Compose

Mo terminal tai thu muc goc cua project va chay:

```bash
docker compose up -d --build
```

Lenh nay se khoi dong:

- `postgres`
- `redis`
- `meilisearch`
- `qdrant`
- `app`

### 4. Kiem tra trang thai

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Xem log app:

```bash
docker logs -f mosquizto_app
```

- Dung toan bo he thong:

```bash
docker compose down
```
