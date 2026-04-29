-- ============================================================
-- V12 : Drop unused PostgreSQL token table
-- Purpose: token persistence now uses RedisToken in Redis, not tbl_token
-- ============================================================

DROP TABLE IF EXISTS tbl_token;
