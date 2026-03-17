-- ============================================================
-- V1 : Create initial database schema
-- Project  : Mosquizto Backend API
-- Database : PostgreSQL
-- ============================================================

-- ----------------------------
-- ENUM : user_status
-- ----------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_status') THEN
        CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE');
    END IF;
END
$$;

-- ----------------------------
-- TABLE : tbl_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_role (
    id      SERIAL      PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
);

-- ----------------------------
-- TABLE : tbl_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_user (
    id          BIGSERIAL       PRIMARY KEY,
    full_name   VARCHAR(150),
    email       VARCHAR(255)    NOT NULL UNIQUE,
    username    VARCHAR(100)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    status      user_status     NOT NULL DEFAULT 'INACTIVE',
    verify_code VARCHAR(10),
    role_id     INT             REFERENCES tbl_role(id),
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

-- ----------------------------
-- TABLE : tbl_token
-- ----------------------------
CREATE TABLE IF NOT EXISTS tbl_token (
    id            BIGSERIAL       PRIMARY KEY,
    username      VARCHAR(100)    NOT NULL UNIQUE,
    access_token  TEXT,
    refresh_token TEXT,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);

-- ----------------------------
-- INDEX
-- ----------------------------
CREATE INDEX IF NOT EXISTS idx_user_email    ON tbl_user(email);
CREATE INDEX IF NOT EXISTS idx_user_username ON tbl_user(username);
CREATE INDEX IF NOT EXISTS idx_token_username ON tbl_token(username);
