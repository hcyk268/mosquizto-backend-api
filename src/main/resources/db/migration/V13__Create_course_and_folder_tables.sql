-- ============================================================
-- V13 : Create course and folder tables
-- Purpose: Persist Course, UserCourse, CourseCollection, Folder,
--          and FolderCollection entities via Flyway-managed DDL.
-- ============================================================

-- ----------------------------
-- ENUMS
-- ----------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'course_role') THEN
        CREATE TYPE course_role AS ENUM ('TEACHER', 'STUDENT');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'access_status') THEN
        CREATE TYPE access_status AS ENUM ('ENABLE', 'PENDING', 'DENIED');
    END IF;
END
$$;


-- Align existing AccessStatus column with the PostgreSQL enum used by entities.
ALTER TABLE tbl_user_collection
    ALTER COLUMN access_status DROP DEFAULT,
    ALTER COLUMN access_status TYPE access_status USING (
        CASE
            WHEN access_status IS NULL THEN NULL
            WHEN UPPER(access_status) = 'DISABLE' THEN 'DENIED'
            ELSE UPPER(access_status)
        END::access_status
    ),
    ALTER COLUMN access_status SET DEFAULT 'ENABLE';

-- ----------------------------
-- TABLE : tbl_course
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_course (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(255),
    description   TEXT,
    visibility    BOOLEAN,
    thumbnail_url VARCHAR(255),
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE
);

-- ----------------------------
-- TABLE : tbl_folder
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_folder (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    user_id     BIGINT REFERENCES tbl_user(id),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE
);

-- ----------------------------
-- TABLE : tbl_user_course
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_user_course (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES tbl_user(id),
    course_id     BIGINT NOT NULL REFERENCES tbl_course(id),
    role          course_role NOT NULL DEFAULT 'STUDENT',
    joined_at     TIMESTAMP WITHOUT TIME ZONE,
    access_status access_status DEFAULT 'ENABLE',
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE
);

-- ----------------------------
-- TABLE : tbl_course_collection
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_course_collection (
    id            BIGSERIAL PRIMARY KEY,
    course_id     BIGINT NOT NULL REFERENCES tbl_course(id),
    collection_id INTEGER NOT NULL REFERENCES tbl_collection(id),
    order_index   INTEGER,
    access_status access_status DEFAULT 'ENABLE',
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE
);

-- ----------------------------
-- TABLE : tbl_folder_collection
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_folder_collection (
    id            BIGSERIAL PRIMARY KEY,
    folder_id     BIGINT NOT NULL REFERENCES tbl_folder(id),
    collection_id INTEGER NOT NULL REFERENCES tbl_collection(id),
    order_index   INTEGER,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE
);

-- ----------------------------
-- UNIQUE CONSTRAINTS
-- ----------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_user_course') THEN
        ALTER TABLE tbl_user_course
            ADD CONSTRAINT uk_user_course UNIQUE (user_id, course_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_course_collection') THEN
        ALTER TABLE tbl_course_collection
            ADD CONSTRAINT uk_course_collection UNIQUE (course_id, collection_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_folder_collection') THEN
        ALTER TABLE tbl_folder_collection
            ADD CONSTRAINT uk_folder_collection UNIQUE (folder_id, collection_id);
    END IF;
END
$$;

-- ----------------------------
-- INDEXES
-- ----------------------------
CREATE INDEX IF NOT EXISTS idx_folder_user_id
    ON tbl_folder(user_id);

CREATE INDEX IF NOT EXISTS idx_user_course_user_id
    ON tbl_user_course(user_id);
CREATE INDEX IF NOT EXISTS idx_user_course_course_id
    ON tbl_user_course(course_id);

CREATE INDEX IF NOT EXISTS idx_course_collection_course_id
    ON tbl_course_collection(course_id);
CREATE INDEX IF NOT EXISTS idx_course_collection_collection_id
    ON tbl_course_collection(collection_id);

CREATE INDEX IF NOT EXISTS idx_folder_collection_folder_id
    ON tbl_folder_collection(folder_id);
CREATE INDEX IF NOT EXISTS idx_folder_collection_collection_id
    ON tbl_folder_collection(collection_id);

