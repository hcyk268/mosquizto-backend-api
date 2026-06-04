-- Thêm cột invited_by
ALTER TABLE tbl_user_collection ADD COLUMN invited_by BIGINT;

-- Thêm khóa ngoại trỏ về tbl_user
ALTER TABLE tbl_user_collection
    ADD CONSTRAINT tbl_user_collection_invited_by_fkey
        FOREIGN KEY (invited_by) REFERENCES tbl_user(id) ON DELETE SET NULL;