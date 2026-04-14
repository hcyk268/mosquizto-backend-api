-- V8__Add_access_status_and_item_count.sql

-- 1. Thêm cột access_status cho bảng phân quyền
ALTER TABLE tbl_user_collection ADD COLUMN access_status VARCHAR(20) DEFAULT 'ENABLE';

-- Cập nhật các bản ghi cũ thành ENABLE
UPDATE tbl_user_collection SET access_status = 'ENABLE' WHERE access_status IS NULL;

-- 2. Thêm cột count vào bảng tbl_collection
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tbl_collection' AND column_name='count') THEN
ALTER TABLE tbl_collection ADD COLUMN count INTEGER DEFAULT 0;
END IF;
END $$;

-- 3. Cập nhật giá trị count dựa trên số lượng item thực tế đang có trong tbl_collection_item
-- Việc này giúp dữ liệu cũ không bị hiển thị count = 0 trong khi đã có bài học bên trong
UPDATE tbl_collection c
SET count = (
    SELECT COUNT(*)
    FROM tbl_collection_item ci
    WHERE ci.collection_id = c.id
);