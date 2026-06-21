-- ============================================================
-- V24 : User report table + USER_REPORTED notification type
-- ============================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'notification_type'
          AND e.enumlabel = 'USER_REPORTED'
    ) THEN
        ALTER TYPE notification_type ADD VALUE 'USER_REPORTED';
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS tbl_user_report (
    id               BIGSERIAL PRIMARY KEY,
    reporter_id      BIGINT NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    reported_user_id BIGINT NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    reason           VARCHAR(100) NOT NULL,
    description      TEXT,
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    deleted_at       TIMESTAMP WITHOUT TIME ZONE,
    deleted_by       BIGINT REFERENCES tbl_user(id) ON DELETE SET NULL,
    CONSTRAINT uq_user_report_reporter_reported UNIQUE (reporter_id, reported_user_id),
    CONSTRAINT chk_user_report_not_self CHECK (reporter_id <> reported_user_id)
);

CREATE INDEX IF NOT EXISTS idx_user_report_reported_user_status
    ON tbl_user_report(reported_user_id, status)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_user_report_reporter_id
    ON tbl_user_report(reporter_id)
    WHERE deleted_at IS NULL;
