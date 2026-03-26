-- ============================================================
-- V6 : Create study session and session detail tables
-- Purpose: Track individual game/study sessions and results
-- ============================================================

-- ----------------------------
-- TABLE : tbl_study_session
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_study_session (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES tbl_user(id),
    collection_id   INT          NOT NULL REFERENCES tbl_collection(id),
    total_score     INT          DEFAULT 0,
    total_correct   INT          DEFAULT 0,
    total_wrong     INT          DEFAULT 0,
    started_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    );

-- ----------------------------
-- TABLE : tbl_study_session_detail
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_study_session_detail (
                                                        id                 BIGSERIAL    PRIMARY KEY,
                                                        session_id         BIGINT       NOT NULL REFERENCES tbl_study_session(id) ON DELETE CASCADE,
    collection_item_id INT          NOT NULL REFERENCES tbl_collection_item(id),
    is_correct         BOOLEAN      NOT NULL,
    response_time_ms   INT,
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    );

COMMENT ON COLUMN tbl_study_session_detail.response_time_ms IS 'Time taken to answer in milliseconds';

-- ----------------------------
-- INDEXES
-- ----------------------------
CREATE INDEX IF NOT EXISTS idx_session_user_id       ON tbl_study_session(user_id);
CREATE INDEX IF NOT EXISTS idx_session_collection_id ON tbl_study_session(collection_id);
CREATE INDEX IF NOT EXISTS idx_session_detail_parent ON tbl_study_session_detail(session_id);