-- Bật extension pg_trgm (nếu chưa có)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Tạo GIN Index để  search gần giống nhanh
-- tbl_user và key lay username
CREATE INDEX idx_tbl_user_username_trgm ON tbl_user USING GIN (username gin_trgm_ops);