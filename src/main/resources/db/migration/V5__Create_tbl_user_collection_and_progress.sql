-- ============================================================
-- V5 : Create tbl_user_collection & tbl_user_collection_item_progress
-- Project  : Mosquizto Backend API
-- Database : PostgreSQL
-- ============================================================

-- ----------------------------
-- ENUM : collection_role
-- ----------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'collection_role') THEN
        CREATE TYPE collection_role AS ENUM ('OWNER', 'EDITOR', 'VIEWER');
    END IF;
END
$$;

-- ----------------------------
-- ENUM : progress_status
-- ----------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'progress_status') THEN
        CREATE TYPE progress_status AS ENUM ('NEW', 'LEARNING', 'MASTERED');
    END IF;
END
$$;

-- ----------------------------
-- TABLE : tbl_user_collection
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_user_collection (
    user_id       BIGINT          NOT NULL REFERENCES tbl_user(id),
        collection_id INTEGER         NOT NULL REFERENCES tbl_collection(id),
    role          collection_role NOT NULL DEFAULT 'VIEWER',
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY (user_id, collection_id)
);

-- ----------------------------
-- TABLE : tbl_user_collection_item_progress
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_user_collection_item_progress (
    user_id            BIGINT          NOT NULL REFERENCES tbl_user(id),
    collection_item_id INTEGER         NOT NULL REFERENCES tbl_collection_item(id),
    status             progress_status NOT NULL DEFAULT 'NEW',
    correct_count      INTEGER         DEFAULT 0,
    wrong_count        INTEGER         DEFAULT 0,
    last_studied_at    TIMESTAMP WITHOUT TIME ZONE,
    next_review_at     TIMESTAMP WITHOUT TIME ZONE,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY (user_id, collection_item_id)
);
-- ----------------------------
-- INDEX
-- ----------------------------
CREATE INDEX IF NOT EXISTS idx_user_collection_user_id       ON tbl_user_collection(user_id);
CREATE INDEX IF NOT EXISTS idx_user_collection_coll_id       ON tbl_user_collection(collection_id);
CREATE INDEX IF NOT EXISTS idx_user_coll_item_progress_uid   ON tbl_user_collection_item_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_coll_item_progress_ciid  ON tbl_user_collection_item_progress(collection_item_id);
