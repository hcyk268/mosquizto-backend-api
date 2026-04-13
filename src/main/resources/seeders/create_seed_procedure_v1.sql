CREATE OR REPLACE PROCEDURE seed_user_collections_v1(p_user_id INT)
    LANGUAGE plpgsql
AS $$
DECLARE
    v_db_collection_id INT;
    v_oop_collection_id INT;
    v_os_collection_id INT;
BEGIN
    -- =========================
    -- 1. COLLECTION: CƠ SỞ DỮ LIỆU
    -- =========================
    INSERT INTO tbl_collection (title, description, visibility, user_id, created_at, updated_at)
    VALUES ('Cơ sở dữ liệu', 'Các khái niệm SQL', true, p_user_id, NOW(), NOW())
    RETURNING id INTO v_db_collection_id;

    INSERT INTO tbl_collection_item (term, definition, order_index, collection_id, created_at, updated_at)
    VALUES
        ('RDBMS', 'Hệ quản trị CSDL quan hệ', 1, v_db_collection_id, NOW(), NOW()),
        ('Primary Key', 'Khóa chính', 2, v_db_collection_id, NOW(), NOW()),
        ('Foreign Key', 'Khóa ngoại', 3, v_db_collection_id, NOW(), NOW()),
        ('Index', 'Tăng tốc truy vấn', 4, v_db_collection_id, NOW(), NOW()),
        ('JOIN', 'Kết hợp bảng', 5, v_db_collection_id, NOW(), NOW()),
        ('INNER JOIN', 'Lấy dữ liệu khớp', 6, v_db_collection_id, NOW(), NOW()),
        ('LEFT JOIN', 'Lấy tất cả bên trái', 7, v_db_collection_id, NOW(), NOW()),
        ('RIGHT JOIN', 'Lấy tất cả bên phải', 8, v_db_collection_id, NOW(), NOW()),
        ('GROUP BY', 'Nhóm dữ liệu', 9, v_db_collection_id, NOW(), NOW()),
        ('HAVING', 'Lọc nhóm', 10, v_db_collection_id, NOW(), NOW()),
        ('WHERE', 'Điều kiện lọc', 11, v_db_collection_id, NOW(), NOW()),
        ('ORDER BY', 'Sắp xếp', 12, v_db_collection_id, NOW(), NOW()),
        ('Normalization', 'Chuẩn hóa dữ liệu', 13, v_db_collection_id, NOW(), NOW()),
        ('Denormalization', 'Phi chuẩn hóa', 14, v_db_collection_id, NOW(), NOW()),
        ('Transaction', 'Giao dịch', 15, v_db_collection_id, NOW(), NOW()),
        ('ACID', 'Tính chất giao dịch', 16, v_db_collection_id, NOW(), NOW()),
        ('View', 'Bảng ảo', 17, v_db_collection_id, NOW(), NOW()),
        ('Stored Procedure', 'Thủ tục lưu trữ', 18, v_db_collection_id, NOW(), NOW()),
        ('Trigger', 'Kích hoạt tự động', 19, v_db_collection_id, NOW(), NOW()),
        ('Schema', 'Cấu trúc DB', 20, v_db_collection_id, NOW(), NOW());

    -- =========================
    -- 2. COLLECTION: OOP
    -- =========================
    INSERT INTO tbl_collection (title, description, visibility, user_id, created_at, updated_at)
    VALUES ('Lập trình hướng đối tượng', 'Nguyên lý OOP', true, p_user_id, NOW(), NOW())
    RETURNING id INTO v_oop_collection_id;

    INSERT INTO tbl_collection_item (term, definition, order_index, collection_id, created_at, updated_at)
    VALUES
        ('Class', 'Khuôn mẫu', 1, v_oop_collection_id, NOW(), NOW()),
        ('Object', 'Đối tượng', 2, v_oop_collection_id, NOW(), NOW()),
        ('Encapsulation', 'Đóng gói', 3, v_oop_collection_id, NOW(), NOW()),
        ('Inheritance', 'Kế thừa', 4, v_oop_collection_id, NOW(), NOW()),
        ('Polymorphism', 'Đa hình', 5, v_oop_collection_id, NOW(), NOW()),
        ('Abstraction', 'Trừu tượng', 6, v_oop_collection_id, NOW(), NOW()),
        ('Interface', 'Giao diện', 7, v_oop_collection_id, NOW(), NOW()),
        ('Abstract Class', 'Lớp trừu tượng', 8, v_oop_collection_id, NOW(), NOW()),
        ('Constructor', 'Hàm khởi tạo', 9, v_oop_collection_id, NOW(), NOW()),
        ('Destructor', 'Hàm hủy', 10, v_oop_collection_id, NOW(), NOW()),
        ('Method', 'Phương thức', 11, v_oop_collection_id, NOW(), NOW()),
        ('Attribute', 'Thuộc tính', 12, v_oop_collection_id, NOW(), NOW()),
        ('Overloading', 'Nạp chồng', 13, v_oop_collection_id, NOW(), NOW()),
        ('Overriding', 'Ghi đè', 14, v_oop_collection_id, NOW(), NOW()),
        ('Access Modifier', 'Phạm vi truy cập', 15, v_oop_collection_id, NOW(), NOW()),
        ('Singleton', 'Mẫu đơn thể', 16, v_oop_collection_id, NOW(), NOW()),
        ('Factory Pattern', 'Mẫu factory', 17, v_oop_collection_id, NOW(), NOW()),
        ('Dependency Injection', 'Tiêm phụ thuộc', 18, v_oop_collection_id, NOW(), NOW()),
        ('Composition', 'Kết hợp', 19, v_oop_collection_id, NOW(), NOW()),
        ('Aggregation', 'Tập hợp', 20, v_oop_collection_id, NOW(), NOW());

    -- =========================
    -- 3. COLLECTION: HỆ ĐIỀU HÀNH
    -- =========================
    INSERT INTO tbl_collection (title, description, visibility, user_id, created_at, updated_at)
    VALUES ('Hệ điều hành', 'Kiến thức OS', true, p_user_id, NOW(), NOW())
    RETURNING id INTO v_os_collection_id;

    INSERT INTO tbl_collection_item (term, definition, order_index, collection_id, created_at, updated_at)
    VALUES
        ('Process', 'Tiến trình', 1, v_os_collection_id, NOW(), NOW()),
        ('Thread', 'Luồng', 2, v_os_collection_id, NOW(), NOW()),
        ('CPU Scheduling', 'Lập lịch CPU', 3, v_os_collection_id, NOW(), NOW()),
        ('Deadlock', 'Tắc nghẽn', 4, v_os_collection_id, NOW(), NOW()),
        ('Starvation', 'Đói tài nguyên', 5, v_os_collection_id, NOW(), NOW()),
        ('Paging', 'Phân trang', 6, v_os_collection_id, NOW(), NOW()),
        ('Segmentation', 'Phân đoạn', 7, v_os_collection_id, NOW(), NOW()),
        ('Virtual Memory', 'Bộ nhớ ảo', 8, v_os_collection_id, NOW(), NOW()),
        ('Context Switch', 'Chuyển ngữ cảnh', 9, v_os_collection_id, NOW(), NOW()),
        ('Kernel', 'Nhân hệ điều hành', 10, v_os_collection_id, NOW(), NOW()),
        ('User Mode', 'Chế độ người dùng', 11, v_os_collection_id, NOW(), NOW()),
        ('Kernel Mode', 'Chế độ kernel', 12, v_os_collection_id, NOW(), NOW()),
        ('Semaphore', 'Cơ chế đồng bộ', 13, v_os_collection_id, NOW(), NOW()),
        ('Mutex', 'Khóa loại trừ', 14, v_os_collection_id, NOW(), NOW()),
        ('File System', 'Hệ thống tệp', 15, v_os_collection_id, NOW(), NOW()),
        ('I/O Management', 'Quản lý vào/ra', 16, v_os_collection_id, NOW(), NOW()),
        ('Interrupt', 'Ngắt', 17, v_os_collection_id, NOW(), NOW()),
        ('System Call', 'Lời gọi hệ thống', 18, v_os_collection_id, NOW(), NOW()),
        ('Bootloader', 'Trình khởi động', 19, v_os_collection_id, NOW(), NOW()),
        ('Shell', 'Giao diện dòng lệnh', 20, v_os_collection_id, NOW(), NOW());

END;
$$;;