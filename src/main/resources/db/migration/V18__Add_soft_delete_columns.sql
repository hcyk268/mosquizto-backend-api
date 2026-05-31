-- ============================================================
-- V18 : Add soft-delete metadata columns
-- ============================================================

ALTER TABLE tbl_user
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_collection
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_collection_item
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_study_session
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_study_session_detail
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_user_collection
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_course
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_folder
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_user_course
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_course_collection
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_folder_collection
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_user_folder
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_collection_report
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

ALTER TABLE tbl_user_collection_item_star
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES tbl_user(id);

CREATE INDEX IF NOT EXISTS idx_user_active
    ON tbl_user(id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_collection_active
    ON tbl_collection(id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_collection_item_active_by_collection
    ON tbl_collection_item(collection_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_folder_active_by_user
    ON tbl_folder(user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_course_active
    ON tbl_course(id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_study_session_active_by_user
    ON tbl_study_session(user_id)
    WHERE deleted_at IS NULL;
