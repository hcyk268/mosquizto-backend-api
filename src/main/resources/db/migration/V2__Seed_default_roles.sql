-- ============================================================
-- V2 : Seed default roles
-- ============================================================

INSERT INTO tbl_role (id, name)
VALUES
    (1, 'ADMIN'),
    (2, 'USER')
ON CONFLICT (name) DO NOTHING;
