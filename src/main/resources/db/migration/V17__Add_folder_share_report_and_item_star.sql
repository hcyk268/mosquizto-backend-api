-- ============================================================
-- V17 : Folder sharing, collection reports, and starred items
-- ============================================================

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'folder_role') THEN
        CREATE TYPE folder_role AS ENUM ('OWNER', 'EDITOR', 'VIEWER');
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS tbl_user_folder (
    user_id       BIGINT NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    folder_id     BIGINT NOT NULL REFERENCES tbl_folder(id) ON DELETE CASCADE,
    role          folder_role NOT NULL DEFAULT 'VIEWER',
    access_status access_status DEFAULT 'ENABLE',
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY (user_id, folder_id)
);

INSERT INTO tbl_user_folder (user_id, folder_id, role, access_status, created_at, updated_at)
SELECT f.user_id, f.id, 'OWNER', 'ENABLE', COALESCE(f.created_at, NOW()), COALESCE(f.updated_at, NOW())
FROM tbl_folder f
WHERE f.user_id IS NOT NULL
ON CONFLICT (user_id, folder_id) DO NOTHING;

CREATE TABLE IF NOT EXISTS tbl_collection_report (
    id            BIGSERIAL PRIMARY KEY,
    collection_id INTEGER NOT NULL REFERENCES tbl_collection(id) ON DELETE CASCADE,
    reporter_id   BIGINT NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    reason        VARCHAR(100) NOT NULL,
    description   TEXT,
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS tbl_user_collection_item_star (
    user_id            BIGINT NOT NULL REFERENCES tbl_user(id) ON DELETE CASCADE,
    collection_item_id INTEGER NOT NULL REFERENCES tbl_collection_item(id) ON DELETE CASCADE,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY (user_id, collection_item_id)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_collection_report_reporter') THEN
        ALTER TABLE tbl_collection_report
            ADD CONSTRAINT uk_collection_report_reporter UNIQUE (collection_id, reporter_id);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_user_folder_user_id
    ON tbl_user_folder(user_id);
CREATE INDEX IF NOT EXISTS idx_user_folder_folder_id
    ON tbl_user_folder(folder_id);

CREATE INDEX IF NOT EXISTS idx_collection_report_collection_id
    ON tbl_collection_report(collection_id);
CREATE INDEX IF NOT EXISTS idx_collection_report_reporter_id
    ON tbl_collection_report(reporter_id);

CREATE INDEX IF NOT EXISTS idx_item_star_user_id
    ON tbl_user_collection_item_star(user_id);
CREATE INDEX IF NOT EXISTS idx_item_star_item_id
    ON tbl_user_collection_item_star(collection_item_id);
