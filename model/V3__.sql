
CREATE TABLE tbl_collection
(
    id          INTEGER NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    tile        VARCHAR(255),
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