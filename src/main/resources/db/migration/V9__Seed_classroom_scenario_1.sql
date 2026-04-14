-- ============================================================
-- V9 : Seed classroom scenario
-- Scenario : Class "Tiếng Anh Lớp 10A" managed by teacher "co_b"
--            + 10 students (6 ENABLE, 4 DISABLE)
--            + 1 class leader (EDITOR), 5 remaining viewers (VIEWER)
-- Password  : $2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy (shared)
-- ============================================================

-- ----------------------------
-- USERS
-- Teacher: co_b (ADMIN role = 1)
-- Students: student_01 .. student_10 (USER role = 2)
-- ----------------------------
INSERT INTO tbl_user (full_name, email, username, password, status, role_id, created_at, updated_at)
VALUES
    -- Teacher / collection owner
    ('Cô Bình',         'co.binh@school.edu.vn',        'co_b',         '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 1, NOW(), NOW()),

    -- Students
    ('Nguyễn Văn An',   'van.an@student.edu.vn',        'student_01',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Trần Thị Bảo',    'thi.bao@student.edu.vn',       'student_02',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Lê Minh Cường',   'minh.cuong@student.edu.vn',    'student_03',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Phạm Ngọc Dung',  'ngoc.dung@student.edu.vn',     'student_04',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Hoàng Anh Đức',   'anh.duc@student.edu.vn',       'student_05',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Vũ Thị Hoa',      'thi.hoa@student.edu.vn',       'student_06',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    -- students below are DISABLE (chưa được duyệt / bị khoá)
    ('Đặng Quốc Hùng',  'quoc.hung@student.edu.vn',     'student_07',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Bùi Thị Kim',     'thi.kim@student.edu.vn',       'student_08',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Ngô Thanh Long',  'thanh.long@student.edu.vn',    'student_09',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW()),
    ('Đinh Thị Mai',    'thi.mai@student.edu.vn',       'student_10',   '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 2, NOW(), NOW());

-- ----------------------------
-- COLLECTION
-- Owner: co_b
-- ----------------------------
INSERT INTO tbl_collection (title, description, visibility, user_id, count, created_at, updated_at)
VALUES (
           'Tiếng Anh Lớp 10A – Unit 1',
           'Từ vựng và cụm từ Unit 1: Family Life – dành cho học sinh lớp 10A.',
           true,
           (SELECT id FROM tbl_user WHERE username = 'co_b'),
           10,
           NOW(), NOW()
       );

-- ----------------------------
-- COLLECTION ITEMS (10 từ vựng)
-- ----------------------------
INSERT INTO tbl_collection_item (term, definition, image_url, order_index, collection_id, created_at, updated_at)
VALUES
    ('breadwinner',     'the member of a family who earns the money that the family needs',   NULL, 1,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('chore',           'a task that you do regularly at home, e.g. cleaning or cooking',     NULL, 2,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('supportive',      'giving help or encouragement',                                        NULL, 3,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('hospitable',      'friendly and welcoming to guests or strangers',                       NULL, 4,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('filial piety',    'respect and care shown toward one''s parents and ancestors',          NULL, 5,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('nurture',         'to care for and encourage the growth of someone',                     NULL, 6,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('sacrifice',       'to give up something important for the sake of others',               NULL, 7,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('dependant',       'a person who relies on another for financial support',                NULL, 8,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('extended family', 'a family unit that includes grandparents, aunts, uncles, etc.',       NULL, 9,  (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW()),
    ('household',       'all the people who live together in one home',                        NULL, 10, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), NOW(), NOW());

-- ----------------------------
-- USER_COLLECTION memberships
--
-- co_b        → OWNER,  ENABLE
-- student_01  → EDITOR, ENABLE  (lớp trưởng)
-- student_02  → VIEWER, ENABLE
-- student_03  → VIEWER, ENABLE
-- student_04  → VIEWER, ENABLE
-- student_05  → VIEWER, ENABLE
-- student_06  → VIEWER, ENABLE
-- student_07  → VIEWER, DISABLE
-- student_08  → VIEWER, DISABLE
-- student_09  → VIEWER, DISABLE
-- student_10  → VIEWER, DISABLE
-- ----------------------------
INSERT INTO tbl_user_collection (user_id, collection_id, role, access_status, created_at, updated_at)
VALUES
    ((SELECT id FROM tbl_user WHERE username = 'co_b'),         (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'OWNER',  'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_01'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'EDITOR', 'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_02'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_03'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_04'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_05'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_06'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'ENABLE',  NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_07'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'DISABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_08'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'DISABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_09'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'DISABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'student_10'),   (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 1'), 'VIEWER', 'DISABLE', NOW(), NOW());