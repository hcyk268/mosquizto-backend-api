-- ============================================================
-- V11 : Add answer mode to study session detail
-- Purpose: Preserve the direction used for each answer
-- ============================================================

ALTER TABLE tbl_study_session_detail
    ADD COLUMN IF NOT EXISTS answer_mode BOOLEAN NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN tbl_study_session_detail.answer_mode IS
    'true = term to definition, false = definition to term';
