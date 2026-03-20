CREATE TABLE tbl_collection
(
    id          INTEGER NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    title        VARCHAR(255),
    description TEXT,
    visibility  BOOLEAN,
    user_id     BIGINT,
    CONSTRAINT pk_tbl_collection PRIMARY KEY (id)
);


CREATE TABLE tbl_collection_item
(
    id            INTEGER NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    term          VARCHAR(255),
    definition    TEXT,
    image_url     VARCHAR(255),
    order_index   INTEGER,
    collection_id INTEGER,
    CONSTRAINT pk_tbl_collection_item PRIMARY KEY (id)
);

ALTER TABLE tbl_collection_item
    ADD CONSTRAINT FK_TBL_COLLECTION_ITEM_ON_COLLECTION FOREIGN KEY (collection_id) REFERENCES tbl_collection (id);

ALTER TABLE tbl_collection
    ADD CONSTRAINT FK_TBL_COLLECTION_ON_USER FOREIGN KEY (user_id) REFERENCES tbl_user (id);

-- 1. Thêm dữ liệu vào bảng tbl_collection
INSERT INTO tbl_collection (id, title, description, visibility, user_id)
VALUES
    (1, 'Vocabulary English', 'Common English words', true, 1),
    (2, 'Japanese N5', 'Basic Kanji and Grammar', true, 1)
    ON CONFLICT (id) DO NOTHING;

-- 2. Thêm dữ liệu vào bảng tbl_collection_item
INSERT INTO tbl_collection_item (term, definition, order_index, collection_id)
VALUES
    ('Apple', 'Quả táo', 1, 1),
    ('Banana', 'Quả chuối', 2, 1),
    ('Watashi', 'Tôi', 1, 2)
    ON CONFLICT DO NOTHING;
-- 3. Cập nhật sequence (quan trọng để tránh lỗi ID khi insert tay sau này)
SELECT setval(pg_get_serial_sequence('tbl_collection', 'id'), coalesce(max(id), 0) + 1, false) FROM tbl_collection;
