ALTER TABLE tbl_user_collection_item_progress
DROP
CONSTRAINT tbl_user_collection_item_progress_collection_item_id_fkey;

ALTER TABLE tbl_user_collection_item_progress
DROP
CONSTRAINT tbl_user_collection_item_progress_user_id_fkey;

ALTER TABLE tbl_study_session_detail
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

DROP TABLE tbl_user_collection_item_progress CASCADE;

ALTER TABLE tbl_token
ALTER
COLUMN access_token TYPE VARCHAR(255) USING (access_token::VARCHAR(255));

ALTER TABLE tbl_user
    ALTER COLUMN email DROP NOT NULL;

ALTER TABLE tbl_role
ALTER
COLUMN name TYPE VARCHAR(255) USING (name::VARCHAR(255));

ALTER TABLE tbl_role
    ALTER COLUMN name DROP NOT NULL;

ALTER TABLE tbl_user
    ALTER COLUMN password DROP NOT NULL;

ALTER TABLE tbl_token
ALTER
COLUMN refresh_token TYPE VARCHAR(255) USING (refresh_token::VARCHAR(255));

ALTER TABLE tbl_user_collection
    ALTER COLUMN role DROP NOT NULL;

ALTER TABLE tbl_user
    ALTER COLUMN status DROP NOT NULL;

ALTER TABLE tbl_token
ALTER
COLUMN username TYPE VARCHAR(255) USING (username::VARCHAR(255));

ALTER TABLE tbl_token
    ALTER COLUMN username DROP NOT NULL;

ALTER TABLE tbl_user
    ALTER COLUMN username DROP NOT NULL;