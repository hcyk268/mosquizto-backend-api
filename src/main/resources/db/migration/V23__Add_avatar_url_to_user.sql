-- ============================================================
-- V23 : Add avatar_url to tbl_user
-- ============================================================

ALTER TABLE tbl_user
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(512);
