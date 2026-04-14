-- ============================================================
-- V9 : Seed class room scenarios
-- Scenario 1 : Foreign expert "David" with 10 Spring Boot collections (3 Public, 7 Private)
-- Scenario 2 : Add more English collections for "co_b"
-- Password   : $2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy (shared)
-- ============================================================

-- ----------------------------
-- 1. USERS
-- Thêm chuyên gia nước ngoài (Role 1 - ADMIN / Giáo viên)
-- ----------------------------
-- Thêm tài khoản David vào bảng tbl_user
INSERT INTO tbl_user (full_name, email, username, password, status, role_id, created_at, updated_at)
VALUES
    ('David Harrison', 'david.harrison@tech.edu.vn', 'david_sb', '$2a$12$GqJG4FiqdbMyP9FL8sztjuNzY08ZeGyt3lzwNWGaJt3ImkPxinHgy', 'ACTIVE', 1, NOW(), NOW());
-- ----------------------------
-- 2. COLLECTIONS (DAVID - SPRING BOOT)
-- 3 Public, 7 Private (visibility = false)
-- Mỗi collection có count = 5
-- ----------------------------
INSERT INTO tbl_collection (title, description, visibility, user_id, count, created_at, updated_at)
VALUES
    -- 3 Public Collections
    ('Spring Boot Basics', 'Core concepts of Spring Boot framework.', true, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Data JPA', 'Managing relational data in Spring.', true, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Security Fundamentals', 'Authentication and Authorization basics.', true, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    -- 7 Private Collections
    ('Spring Boot REST APIs', 'Advanced RESTful web services design.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Microservices', 'Building microservices with Spring Cloud.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Boot Testing', 'JUnit and Mockito integration in Spring.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring AOP Concepts', 'Aspect-Oriented Programming principles.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Batch Processing', 'Processing large volumes of records.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Boot Actuator', 'Monitoring and managing Spring applications.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW()),
    ('Spring Boot with Docker', 'Containerizing Spring applications.', false, (SELECT id FROM tbl_user WHERE username = 'david_sb'), 5, NOW(), NOW());

-- ----------------------------
-- 3. COLLECTION ITEMS (DAVID - SPRING BOOT)
-- 5 items per collection
-- ----------------------------
INSERT INTO tbl_collection_item (term, definition, image_url, order_index, collection_id, created_at, updated_at)
VALUES
    -- 1. Basics
    ('Bean', 'An object that is instantiated, assembled, and managed by a Spring IoC container.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Basics'), NOW(), NOW()),
    ('Dependency Injection', 'A design pattern used to implement IoC, allowing the creation of dependent objects outside of a class.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Basics'), NOW(), NOW()),
    ('ApplicationContext', 'The central interface to provide configuration for an application in Spring.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Basics'), NOW(), NOW()),
    ('@Autowired', 'Marks a constructor, field, setter method, or config method as to be autowired by Spring.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Basics'), NOW(), NOW()),
    ('@Component', 'Indicates that an annotated class is a "component" (auto-detected candidate).', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Basics'), NOW(), NOW()),

    -- 2. JPA
    ('Entity', 'A lightweight persistence domain object representing a table in a database.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Data JPA'), NOW(), NOW()),
    ('Repository', 'An interface for generic CRUD operations on a repository for a specific type.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Data JPA'), NOW(), NOW()),
    ('JPQL', 'Java Persistence Query Language, an object-oriented query language.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Data JPA'), NOW(), NOW()),
    ('@Transactional', 'Describes a transaction attribute on an individual method or on a class.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Data JPA'), NOW(), NOW()),
    ('EntityManager', 'Interface used to interact with the persistence context.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Data JPA'), NOW(), NOW()),

    -- 3. Security
    ('UserDetails', 'Provides core user information for security authentication.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Security Fundamentals'), NOW(), NOW()),
    ('SecurityFilterChain', 'Defines a filter chain which is capable of being matched against an HttpServletRequest.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Security Fundamentals'), NOW(), NOW()),
    ('Authentication', 'The process of verifying the identity of a user or system.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Security Fundamentals'), NOW(), NOW()),
    ('Authorization', 'The process of determining if a user has permission to perform an action.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Security Fundamentals'), NOW(), NOW()),
    ('JWT', 'JSON Web Token, a compact URL-safe means of representing claims to be transferred between two parties.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Security Fundamentals'), NOW(), NOW()),

    -- 4. REST APIs
    ('@RestController', 'A convenience annotation that is itself annotated with @Controller and @ResponseBody.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot REST APIs'), NOW(), NOW()),
    ('@PathVariable', 'Annotation which indicates that a method parameter should be bound to a URI template variable.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot REST APIs'), NOW(), NOW()),
    ('@RequestBody', 'Annotation indicating a method parameter should be bound to the body of the web request.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot REST APIs'), NOW(), NOW()),
    ('ResponseEntity', 'Extension of HttpEntity that adds an HttpStatus status code.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot REST APIs'), NOW(), NOW()),
    ('DTO', 'Data Transfer Object, an object that carries data between processes.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot REST APIs'), NOW(), NOW()),

    -- 5. Microservices
    ('Eureka', 'A REST (Representational State Transfer) based service that is primarily used for locating services.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Microservices'), NOW(), NOW()),
    ('API Gateway', 'A server that is the single entry point into the system, handling request routing.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Microservices'), NOW(), NOW()),
    ('Config Server', 'Provides an HTTP resource-based API for external configuration.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Microservices'), NOW(), NOW()),
    ('Feign Client', 'A declarative web service client making writing web service clients easier.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Microservices'), NOW(), NOW()),
    ('Circuit Breaker', 'A design pattern used to detect failures and encapsulates the logic of preventing a failure from constantly recurring.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Microservices'), NOW(), NOW()),

    -- 6. Testing
    ('@SpringBootTest', 'Annotation that can be specified on a test class that runs Spring Boot based tests.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Testing'), NOW(), NOW()),
    ('@MockBean', 'Annotation that can be used to add mocks to a Spring ApplicationContext.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Testing'), NOW(), NOW()),
    ('MockMvc', 'Main entry point for server-side Spring MVC test support.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Testing'), NOW(), NOW()),
    ('AssertJ', 'A Java library that provides a fluent interface for writing assertions.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Testing'), NOW(), NOW()),
    ('Testcontainers', 'A Java library that supports JUnit tests, providing lightweight instances of common databases.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Testing'), NOW(), NOW()),

    -- 7. AOP
    ('Aspect', 'A modularization of a concern that cuts across multiple classes.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring AOP Concepts'), NOW(), NOW()),
    ('Join Point', 'A point during the execution of a program, such as the execution of a method.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring AOP Concepts'), NOW(), NOW()),
    ('Advice', 'Action taken by an aspect at a particular join point.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring AOP Concepts'), NOW(), NOW()),
    ('Pointcut', 'A predicate that matches join points.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring AOP Concepts'), NOW(), NOW()),
    ('Weaving', 'Linking aspects with other application types or objects to create an advised object.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring AOP Concepts'), NOW(), NOW()),

    -- 8. Batch
    ('Job', 'An entity that encapsulates an entire batch process.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Batch Processing'), NOW(), NOW()),
    ('Step', 'A domain object that encapsulates an independent, sequential phase of a batch job.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Batch Processing'), NOW(), NOW()),
    ('ItemReader', 'An abstraction that represents the output of a Step, one item at a time.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Batch Processing'), NOW(), NOW()),
    ('ItemProcessor', 'An abstraction that represents the business processing of an item.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Batch Processing'), NOW(), NOW()),
    ('ItemWriter', 'An abstraction that represents the output of a Step, one batch or chunk of items at a time.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Batch Processing'), NOW(), NOW()),

    -- 9. Actuator
    ('/health', 'Endpoint providing basic application health information.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Actuator'), NOW(), NOW()),
    ('/metrics', 'Endpoint showing metrics information for the current application.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Actuator'), NOW(), NOW()),
    ('/env', 'Endpoint exposing properties from Spring''s ConfigurableEnvironment.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Actuator'), NOW(), NOW()),
    ('/loggers', 'Endpoint showing and modifying the configuration of loggers.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Actuator'), NOW(), NOW()),
    ('Micrometer', 'A vendor-neutral application metrics facade.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Actuator'), NOW(), NOW()),

    -- 10. Docker
    ('Dockerfile', 'A text document that contains all the commands a user could call to assemble an image.', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot with Docker'), NOW(), NOW()),
    ('Image', 'A read-only template with instructions for creating a Docker container.', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot with Docker'), NOW(), NOW()),
    ('Container', 'A runnable instance of an image.', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot with Docker'), NOW(), NOW()),
    ('Volume', 'The preferred mechanism for persisting data generated by and used by Docker containers.', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot with Docker'), NOW(), NOW()),
    ('docker-compose', 'A tool for defining and running multi-container Docker applications.', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Spring Boot with Docker'), NOW(), NOW());

-- ----------------------------
-- 4. COLLECTIONS (CÔ BÌNH - TIẾNG ANH 10)
-- Bổ sung Unit 2, 3, 4 (Public)
-- ----------------------------
INSERT INTO tbl_collection (title, description, visibility, user_id, count, created_at, updated_at)
VALUES
    ('Tiếng Anh Lớp 10A – Unit 2', 'Từ vựng Unit 2: Your Body and You.', true, (SELECT id FROM tbl_user WHERE username = 'co_b'), 5, NOW(), NOW()),
    ('Tiếng Anh Lớp 10A – Unit 3', 'Từ vựng Unit 3: Music.', true, (SELECT id FROM tbl_user WHERE username = 'co_b'), 5, NOW(), NOW()),
    ('Tiếng Anh Lớp 10A – Unit 4', 'Từ vựng Unit 4: For a Better Community.', true, (SELECT id FROM tbl_user WHERE username = 'co_b'), 5, NOW(), NOW());

-- ----------------------------
-- 5. COLLECTION ITEMS (CÔ BÌNH)
-- ----------------------------
INSERT INTO tbl_collection_item (term, definition, image_url, order_index, collection_id, created_at, updated_at)
VALUES
    -- Unit 2
    ('ailment', 'an illness, typically a minor one', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 2'), NOW(), NOW()),
    ('nerve', 'a fiber that transmits impulses to the brain or spinal cord', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 2'), NOW(), NOW()),
    ('blood vessel', 'a tubular structure carrying blood through the tissues', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 2'), NOW(), NOW()),
    ('skeleton', 'an internal or external framework of bone, cartilage', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 2'), NOW(), NOW()),
    ('lung', 'each of the pair of organs situated within the rib cage', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 2'), NOW(), NOW()),
    -- Unit 3
    ('passionate', 'showing or caused by strong feelings or a strong belief', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 3'), NOW(), NOW()),
    ('biography', 'an account of someone''s life written by someone else', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 3'), NOW(), NOW()),
    ('rhythm', 'a strong, regular, repeated pattern of movement or sound', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 3'), NOW(), NOW()),
    ('debut', 'a person''s first appearance or performance in a particular capacity', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 3'), NOW(), NOW()),
    ('pop music', 'music of general appeal to teenagers', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 3'), NOW(), NOW()),
    -- Unit 4
    ('volunteer', 'a person who freely offers to take part in an enterprise', NULL, 1, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 4'), NOW(), NOW()),
    ('orphan', 'a child whose parents are dead', NULL, 2, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 4'), NOW(), NOW()),
    ('disadvantaged', 'in unfavorable circumstances, especially regarding financial matters', NULL, 3, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 4'), NOW(), NOW()),
    ('donate', 'give for a good cause, for example to a charity', NULL, 4, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 4'), NOW(), NOW()),
    ('remote', 'situated far from the main centers of population', NULL, 5, (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 4'), NOW(), NOW());

-- ----------------------------
-- 6. USER_COLLECTION MEMBERSHIPS
-- Cấp quyền OWNER cho các bộ sưu tập vừa tạo để tránh lỗi truy vấn khi load danh sách
-- ----------------------------
INSERT INTO tbl_user_collection (user_id, collection_id, role, access_status, created_at, updated_at)
VALUES
    -- Quyền OWNER cho 10 bộ của David
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Basics'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Data JPA'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Security Fundamentals'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Boot REST APIs'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Microservices'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Testing'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring AOP Concepts'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Batch Processing'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Boot Actuator'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'david_sb'), (SELECT id FROM tbl_collection WHERE title = 'Spring Boot with Docker'), 'OWNER', 'ENABLE', NOW(), NOW()),

    -- Quyền OWNER cho 3 bộ mới của Cô Bình
    ((SELECT id FROM tbl_user WHERE username = 'co_b'), (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 2'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'co_b'), (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 3'), 'OWNER', 'ENABLE', NOW(), NOW()),
    ((SELECT id FROM tbl_user WHERE username = 'co_b'), (SELECT id FROM tbl_collection WHERE title = 'Tiếng Anh Lớp 10A – Unit 4'), 'OWNER', 'ENABLE', NOW(), NOW());