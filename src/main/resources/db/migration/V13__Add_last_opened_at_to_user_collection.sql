ALTER TABLE tbl_user_collection 
ADD COLUMN last_opened_at TIMESTAMP;

ALTER TABLE tbl_study_session_detail
ALTER COLUMN response_time_ms TYPE FLOAT8;