-- Thêm cột last_opened_at vào bảng tbl_user_collection
-- Được sửa dụng để theo dỗi quá trình học
ALTER TABLE tbl_user_collection
ADD COLUMN last_opened_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE tbl_study_session_detail
ALTER COLUMN response_time_ms TYPE FLOAT8;