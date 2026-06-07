-- ============================================================
-- V21 : Create notification table
-- Purpose: Persist user notifications for offline delivery
--          (flush to WebSocket on reconnect)
-- ============================================================

-- ----------------------------
-- ENUM : notification_type
-- ----------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'notification_type') THEN
CREATE TYPE notification_type AS ENUM (
            'COLLECTION_SHARED',
            'COLLECTION_REPORTED',
            'JOIN_REQUEST_RECEIVED',
            'JOIN_REQUEST_APPROVED',
            'JOIN_REQUEST_DENIED',
            'HAS_FOLLOWER',
            'COLLECTION_CREATED'
        );
END IF;
END
$$;

-- ----------------------------
-- TABLE : tbl_notification
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_notification (
                                                id            BIGSERIAL        PRIMARY KEY,
                                                recipient_id  BIGINT           NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    type          notification_type NOT NULL,
    message       TEXT             NOT NULL,
    is_read       BOOLEAN          NOT NULL DEFAULT FALSE,
    read_at       TIMESTAMP WITHOUT TIME ZONE,
    reference_id  BIGINT,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    deleted_by    BIGINT           REFERENCES tbl_user(id) ON DELETE SET NULL
    );

-- ----------------------------
-- INDEXES
-- ----------------------------
-- Query chính: lấy thông báo chưa đọc của 1 user khi reconnect
CREATE INDEX IF NOT EXISTS idx_notification_recipient_unread
    ON tbl_notification(recipient_id, is_read)
    WHERE deleted_at IS NULL;

-- Query phụ: phân trang lịch sử thông báo theo thời gian
CREATE INDEX IF NOT EXISTS idx_notification_recipient_created
    ON tbl_notification(recipient_id, created_at DESC)
    WHERE deleted_at IS NULL;

COMMENT ON TABLE tbl_notification IS
    'Persists notifications for all users. Unread rows are flushed via WebSocket on reconnect.';

COMMENT ON COLUMN tbl_notification.reference_id IS
    'Optional FK-like pointer to the related entity (collectionId, reportId, ...). Not a hard FK to keep the table generic.';

COMMENT ON COLUMN tbl_notification.type IS
    'Discriminator for frontend routing and icon rendering.';
