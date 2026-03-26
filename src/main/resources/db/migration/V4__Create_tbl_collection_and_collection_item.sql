-- ============================================================
-- V4 : Create tbl_collection & tbl_collection_item
-- Project  : Mosquizto Backend API
-- Database : PostgreSQL
-- ============================================================

-- ----------------------------
-- TABLE : tbl_collection
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_collection (
    id          SERIAL                      PRIMARY KEY,
    title       VARCHAR(255),
    description TEXT,
    visibility  BOOLEAN                     DEFAULT true,
    user_id     BIGINT                      REFERENCES tbl_user(id),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE
);

-- ----------------------------
-- TABLE : tbl_collection_item
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_collection_item (
    id            SERIAL                      PRIMARY KEY,
    term          VARCHAR(255),
    definition    TEXT,
    image_url     VARCHAR(255),
    order_index   INTEGER,
    collection_id INTEGER                     REFERENCES tbl_collection(id),
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE
);
-- ----------------------------
-- INDEX
-- ----------------------------
CREATE INDEX IF NOT EXISTS idx_collection_user_id       ON tbl_collection(user_id);
CREATE INDEX IF NOT EXISTS idx_collection_item_coll_id  ON tbl_collection_item(collection_id);
