-- ============================================================
-- V2 : Seed default roles
-- ============================================================

INSERT INTO tbl_role (id, name)
VALUES
    (1, 'ADMIN'),
    (2, 'USER')
ON CONFLICT (name) DO NOTHING;

SELECT setval(pg_get_serial_sequence('tbl_role', 'id'), coalesce(max(id), 0) + 1, false) FROM tbl_role;
