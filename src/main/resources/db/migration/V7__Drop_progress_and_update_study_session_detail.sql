-- ============================================================
-- V7 : Drop tbl_user_collection_item_progress
--       + Add updated_at to tbl_study_session_detail
-- ============================================================

-- ----------------------------
-- DROP INDEXES
-- ----------------------------
DROP INDEX IF EXISTS idx_user_coll_item_progress_uid;
DROP INDEX IF EXISTS idx_user_coll_item_progress_ciid;

-- ----------------------------
-- DROP TABLE
-- ----------------------------
DROP TABLE IF EXISTS tbl_user_collection_item_progress;

-- ----------------------------
-- DROP ENUM
-- ----------------------------
DROP TYPE IF EXISTS progress_status;

-- ----------------------------
-- ADD updated_at to tbl_study_session_detail
-- ----------------------------
ALTER TABLE tbl_study_session_detail
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
