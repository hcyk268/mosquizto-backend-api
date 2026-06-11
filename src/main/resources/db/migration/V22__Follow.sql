-- ============================================================
-- V22 : Create follow table
-- ============================================================

CREATE TABLE IF NOT EXISTS tbl_follow(
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL REFERENCES tbl_user(id),
    following_id BIGINT NOT NULL REFERENCES tbl_user(id),
    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by BIGINT REFERENCES tbl_user(id),
    CONSTRAINT uq_follower_following UNIQUE (follower_id, following_id),
    CONSTRAINT chk_follow_not_self CHECK (follower_id <> following_id)
);

CREATE INDEX IF NOT EXISTS idx_follow_follower_active
    ON tbl_follow(follower_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_follow_following_active
    ON tbl_follow(following_id)
    WHERE deleted_at IS NULL;
