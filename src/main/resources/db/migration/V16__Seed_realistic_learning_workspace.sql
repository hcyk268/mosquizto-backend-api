-- ============================================================
-- V16 : Seed realistic learning workspace
-- Scenario : IELTS class and Data Analytics cohort with users,
--            collections, memberships, courses, folders, and
--            study history.
-- Password : Demo@2026
-- Hash     : BCrypt $2a$, cost 12
-- ============================================================

-- Keep default system roles available for databases that were restored
-- without earlier seed rows.
INSERT INTO tbl_role (name)
VALUES ('ADMIN'), ('USER')
ON CONFLICT (name) DO NOTHING;

-- ----------------------------
-- USERS
-- ----------------------------
WITH seed_users(full_name, email, username, role_name, status, verify_code, created_at, updated_at) AS (
    VALUES
        ('Le Hoang Quan', 'ops.admin.demo@mosquizto.edu.vn', 'ops_admin_demo', 'ADMIN', 'ACTIVE', NULL, TIMESTAMP '2026-04-20 08:00:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('Nguyen Thi Lan', 'lan.nguyen@mosquizto.edu.vn', 'teacher_lan_ielts', 'ADMIN', 'ACTIVE', NULL, TIMESTAMP '2026-04-21 09:00:00', TIMESTAMP '2026-05-04 17:45:00'),
        ('Pham Minh Duc', 'duc.pham@mosquizto.edu.vn', 'teacher_minh_data', 'ADMIN', 'ACTIVE', NULL, TIMESTAMP '2026-04-22 09:30:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('Tran Thuy Phuong', 'phuong.tran@mosquizto.edu.vn', 'mentor_phuong', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-23 10:00:00', TIMESTAMP '2026-05-04 12:15:00'),
        ('Do Minh An', 'an.do@student.mosquizto.edu.vn', 'student_an', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-24 13:00:00', TIMESTAMP '2026-05-05 07:45:00'),
        ('Bui Gia Bao', 'bao.bui@student.mosquizto.edu.vn', 'student_bao', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-24 13:05:00', TIMESTAMP '2026-05-04 20:25:00'),
        ('Cao Ngoc Chi', 'chi.cao@student.mosquizto.edu.vn', 'student_chi', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-24 13:10:00', TIMESTAMP '2026-05-04 21:16:00'),
        ('Hoang Quang Dung', 'dung.hoang@student.mosquizto.edu.vn', 'student_dung', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-24 13:15:00', TIMESTAMP '2026-05-03 22:10:00'),
        ('Lam Bao Hieu', 'hieu.lam@student.mosquizto.edu.vn', 'student_hieu', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-24 13:20:00', TIMESTAMP '2026-05-02 19:40:00'),
        ('Mai Khanh Linh', 'linh.mai@student.mosquizto.edu.vn', 'student_linh', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-24 13:25:00', TIMESTAMP '2026-05-05 07:34:00'),
        ('Nguyen Thanh Nam', 'nam.nguyen@student.mosquizto.edu.vn', 'student_nam', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-25 08:10:00', TIMESTAMP '2026-05-02 18:17:00'),
        ('Pham Le Quyen', 'quyen.pham@student.mosquizto.edu.vn', 'student_quyen', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-25 08:15:00', TIMESTAMP '2026-05-03 17:55:00'),
        ('Vo Dang Khoa', 'khoa.vo@student.mosquizto.edu.vn', 'student_khoa_pending', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-25 08:20:00', TIMESTAMP '2026-05-01 10:05:00'),
        ('Tran Minh Tram', 'tram.tran@student.mosquizto.edu.vn', 'student_tram_denied', 'USER', 'ACTIVE', NULL, TIMESTAMP '2026-04-25 08:25:00', TIMESTAMP '2026-05-01 10:35:00'),
        ('Dang Thu Mai', 'mai.dang@student.mosquizto.edu.vn', 'student_mai_inactive', 'USER', 'INACTIVE', 'MAIL-VERIFY-2026', TIMESTAMP '2026-05-04 08:30:00', TIMESTAMP '2026-05-04 08:30:00')
)
INSERT INTO tbl_user (full_name, email, username, password, status, verify_code, role_id, created_at, updated_at)
SELECT
    su.full_name,
    su.email,
    su.username,
    '$2a$12$.OPiV..ruEvvKTyKObJOK.cyiYvrXIZ106trV6g68g.NVFEQ.bFru',
    su.status::user_status,
    su.verify_code,
    r.id,
    su.created_at,
    su.updated_at
FROM seed_users su
JOIN tbl_role r ON r.name = su.role_name
ON CONFLICT (username) DO NOTHING;

-- ----------------------------
-- COLLECTIONS
-- ----------------------------
WITH seed_collections(owner_username, title, description, visibility, item_count, created_at, updated_at) AS (
    VALUES
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'Core vocabulary for school systems, learning outcomes, and classroom discussion.', TRUE, 8, TIMESTAMP '2026-04-25 09:00:00', TIMESTAMP '2026-05-04 09:30:00'),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'Topic vocabulary for climate, recycling, energy, and local environmental projects.', TRUE, 8, TIMESTAMP '2026-04-26 09:00:00', TIMESTAMP '2026-05-04 10:15:00'),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'Practical vocabulary for apps, data privacy, automation, and digital habits.', FALSE, 8, TIMESTAMP '2026-04-27 09:00:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 'Reusable speaking prompts and lexical chunks for part 2 answers.', FALSE, 6, TIMESTAMP '2026-04-28 14:00:00', TIMESTAMP '2026-05-04 14:35:00'),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'Database vocabulary for joins, keys, grouping, and transaction safety.', TRUE, 6, TIMESTAMP '2026-04-25 15:00:00', TIMESTAMP '2026-05-03 09:40:00'),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 'Vocabulary for dashboards, metric review, cohorts, and trend analysis.', FALSE, 6, TIMESTAMP '2026-04-26 15:00:00', TIMESTAMP '2026-05-03 10:20:00'),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'Business vocabulary for agendas, minutes, deadlines, and team updates.', TRUE, 8, TIMESTAMP '2026-04-29 08:30:00', TIMESTAMP '2026-05-04 16:00:00'),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'Common email phrases for requests, follow-ups, attachments, and polite replies.', TRUE, 8, TIMESTAMP '2026-04-29 09:30:00', TIMESTAMP '2026-05-04 16:10:00'),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 'Vocabulary for offers, trade-offs, contracts, and closing a business deal.', FALSE, 8, TIMESTAMP '2026-04-29 10:30:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 'Practical travel vocabulary for check-in, baggage, reservations, and service requests.', TRUE, 8, TIMESTAMP '2026-04-29 11:30:00', TIMESTAMP '2026-05-04 16:30:00'),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 'Useful words for symptoms, appointments, prescriptions, and follow-up care.', TRUE, 8, TIMESTAMP '2026-04-29 13:30:00', TIMESTAMP '2026-05-04 16:40:00'),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'Academic science vocabulary for cell structure, DNA, heredity, and lab work.', TRUE, 8, TIMESTAMP '2026-04-30 08:30:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 'Core physics terms for speed, acceleration, force, energy, and simple experiments.', TRUE, 8, TIMESTAMP '2026-04-30 09:30:00', TIMESTAMP '2026-05-04 17:00:00'),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 'Vocabulary for urban planning, public transport, infrastructure, and city life.', FALSE, 8, TIMESTAMP '2026-04-30 10:30:00', TIMESTAMP '2026-05-04 17:10:00'),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'Programming vocabulary for variables, loops, conditionals, and functions.', TRUE, 8, TIMESTAMP '2026-04-30 14:00:00', TIMESTAMP '2026-05-04 17:20:00'),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 'Backend development vocabulary for classes, interfaces, inheritance, and error handling.', TRUE, 8, TIMESTAMP '2026-04-30 15:00:00', TIMESTAMP '2026-05-04 17:30:00'),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'Cloud vocabulary for compute, storage, networking, identity, and deployment basics.', FALSE, 8, TIMESTAMP '2026-04-30 16:00:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'Operational vocabulary for pipelines, releases, metrics, alerts, and incident response.', TRUE, 8, TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-04 17:50:00'),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'Security vocabulary for risks, attacks, controls, authentication, and response.', TRUE, 8, TIMESTAMP '2026-05-01 09:30:00', TIMESTAMP '2026-05-04 18:00:00'),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 'Product vocabulary for research, prioritization, roadmap planning, and delivery.', FALSE, 8, TIMESTAMP '2026-05-01 10:30:00', TIMESTAMP '2026-05-04 18:10:00'),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 'Research vocabulary for user interviews, usability tests, findings, and prototypes.', TRUE, 8, TIMESTAMP '2026-05-01 11:30:00', TIMESTAMP '2026-05-04 18:20:00'),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'Marketing vocabulary for funnels, audiences, ads, conversions, and retention.', TRUE, 8, TIMESTAMP '2026-05-01 13:30:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'Practical finance vocabulary for expenses, savings, interest, risk, and portfolios.', TRUE, 8, TIMESTAMP '2026-05-01 14:30:00', TIMESTAMP '2026-05-04 18:40:00'),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'Statistics vocabulary for samples, distributions, bias, confidence, and variation.', FALSE, 8, TIMESTAMP '2026-05-01 15:30:00', TIMESTAMP '2026-05-04 18:50:00')
)
INSERT INTO tbl_collection (title, description, visibility, user_id, count, created_at, updated_at)
SELECT
    sc.title,
    sc.description,
    sc.visibility,
    u.id,
    sc.item_count,
    sc.created_at,
    sc.updated_at
FROM seed_collections sc
JOIN tbl_user u ON u.username = sc.owner_username
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_collection c
    WHERE c.title = sc.title
      AND c.user_id = u.id
);

-- ----------------------------
-- COLLECTION ITEMS
-- ----------------------------
WITH seed_items(owner_username, collection_title, order_index, term, definition, image_url) AS (
    VALUES
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 1, 'curriculum', 'the subjects and learning activities included in a course or school program', 'https://cdn.mosquizto.local/sample/ielts-education-curriculum.png'),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 2, 'tuition fee', 'the money paid to study at a school, college, or training center', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 3, 'scholarship', 'financial support given to a learner because of ability or need', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 4, 'vocational training', 'education that prepares people for a specific job or practical career', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 5, 'enrollment', 'the act of officially joining a course or school', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 6, 'peer feedback', 'comments and suggestions given by classmates or colleagues', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 7, 'academic performance', 'how well a learner does in tests, assignments, and class activities', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 8, 'lifelong learning', 'the habit of continuing to learn new skills throughout life', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 1, 'carbon footprint', 'the amount of greenhouse gas produced by a person, group, or activity', 'https://cdn.mosquizto.local/sample/environment-carbon-footprint.png'),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 2, 'renewable energy', 'energy from sources that are naturally replaced, such as sunlight or wind', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 3, 'landfill', 'a place where waste is buried under layers of soil', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 4, 'biodiversity', 'the variety of plant and animal life in a particular place', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 5, 'conservation', 'the protection of nature, resources, or historic places', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 6, 'drought', 'a long period with little or no rain', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 7, 'recycling campaign', 'an organized effort to encourage people to reuse waste materials', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 8, 'sustainable habit', 'a regular action that reduces harm to the environment', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 1, 'artificial intelligence', 'computer systems that can perform tasks usually requiring human intelligence', 'https://cdn.mosquizto.local/sample/technology-ai.png'),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 2, 'data privacy', 'the protection of personal information from misuse or unwanted access', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 3, 'cloud storage', 'online storage that lets people access files from different devices', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 4, 'cybersecurity', 'the practice of protecting computers, networks, and data from attacks', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 5, 'automation', 'the use of technology to complete tasks with little human effort', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 6, 'digital literacy', 'the ability to use digital tools and evaluate online information', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 7, 'user interface', 'the screens and controls through which a person uses software', NULL),
        ('teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 8, 'software update', 'a change released to fix, improve, or secure a program', NULL),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 1, 'role model', 'a person whose behavior or success is admired and copied by others', NULL),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 2, 'hometown', 'the town or city where a person was born or grew up', NULL),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 3, 'memorable journey', 'a trip that is easy to remember because it was special or important', NULL),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 4, 'public park', 'an outdoor area maintained for people to relax, exercise, or meet friends', NULL),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 5, 'community event', 'an activity organized for people living in the same area', NULL),
        ('teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 6, 'daily routine', 'the normal activities a person does on most days', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 1, 'primary key', 'a column or set of columns that uniquely identifies each row in a table', 'https://cdn.mosquizto.local/sample/sql-primary-key.png'),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 2, 'foreign key', 'a column that links a row to a primary key in another table', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 3, 'join', 'a query operation that combines rows from two or more tables', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 4, 'aggregate function', 'a function that calculates one value from many rows, such as count or average', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 5, 'index', 'a database structure that helps queries find rows faster', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - SQL Basics', 6, 'transaction', 'a group of database operations that succeed or fail together', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 1, 'conversion rate', 'the percentage of users who complete a target action', 'https://cdn.mosquizto.local/sample/metrics-dashboard.png'),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 2, 'cohort', 'a group of users who share a common trait or start date', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 3, 'dashboard', 'a screen that summarizes important data and metrics', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 4, 'outlier', 'a value that is very different from most other values in a dataset', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 5, 'trend line', 'a line on a chart that shows the general direction of data over time', NULL),
        ('teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 6, 'benchmark', 'a standard used to compare performance or quality', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 1, 'agenda', 'a list of topics that will be discussed in a meeting', 'https://cdn.mosquizto.local/sample/toeic-meeting-agenda.png'),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 2, 'minutes', 'written notes that summarize what was discussed and decided in a meeting', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 3, 'deadline', 'the latest time or date by which a task must be finished', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 4, 'action item', 'a specific task assigned to a person after a meeting', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 5, 'conference room', 'a room used for formal meetings or group discussions', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 6, 'postpone', 'to arrange for something to happen at a later time', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 7, 'attendee', 'a person who is present at a meeting or event', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 8, 'follow-up', 'an action or message sent after an earlier discussion', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 1, 'attachment', 'a file that is sent together with an email message', 'https://cdn.mosquizto.local/sample/toeic-email-attachment.png'),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 2, 'inquiry', 'a request for information about a product, service, or situation', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 3, 'recipient', 'a person who receives an email, letter, or package', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 4, 'confirm receipt', 'to say that a message, file, or payment has been received', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 5, 'template', 'a standard format that can be reused for similar messages', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 6, 'courteous', 'polite and respectful in communication', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 7, 'reply all', 'to send a response to everyone included in the original email', NULL),
        ('teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 8, 'subject line', 'the short title of an email that shows what it is about', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 1, 'proposal', 'a formal suggestion or plan offered for discussion', 'https://cdn.mosquizto.local/sample/business-negotiation-proposal.png'),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 2, 'counteroffer', 'a new offer made in response to a previous offer', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 3, 'discount', 'a reduction from the usual price', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 4, 'terms and conditions', 'the rules and requirements of an agreement or service', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 5, 'mutual benefit', 'an advantage shared by both sides in an agreement', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 6, 'compromise', 'an agreement in which each side accepts less than it originally wanted', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 7, 'contract clause', 'a specific condition or section in a legal agreement', NULL),
        ('teacher_lan_ielts', 'Business English - Negotiation Basics', 8, 'finalize', 'to complete the last details of a plan or agreement', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 1, 'boarding pass', 'a document that allows a passenger to get on a plane', 'https://cdn.mosquizto.local/sample/travel-boarding-pass.png'),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 2, 'carry-on luggage', 'a small bag that a passenger takes into the airplane cabin', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 3, 'customs declaration', 'a form listing items that must be reported when entering a country', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 4, 'reservation', 'an arrangement to keep a room, seat, or service available', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 5, 'front desk', 'the reception area in a hotel where guests are helped', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 6, 'late checkout', 'permission to leave a hotel room later than the normal time', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 7, 'itinerary', 'a plan of travel with places, dates, and activities', NULL),
        ('teacher_lan_ielts', 'Travel English - Airport and Hotel', 8, 'shuttle bus', 'a bus that regularly carries passengers between nearby places', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 1, 'symptom', 'a physical or mental sign that may show an illness or condition', 'https://cdn.mosquizto.local/sample/health-clinic-symptom.png'),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 2, 'appointment', 'an arranged time to meet a doctor or service provider', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 3, 'prescription', 'written instructions from a doctor for medicine or treatment', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 4, 'dosage', 'the amount of medicine that should be taken at one time', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 5, 'side effect', 'an unwanted result caused by a medicine or treatment', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 6, 'checkup', 'a medical examination to see if someone is healthy', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 7, 'insurance card', 'a card showing that a person has health insurance coverage', NULL),
        ('teacher_lan_ielts', 'Health English - Clinic Conversations', 8, 'follow-up visit', 'a later appointment to check progress after treatment', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 1, 'cell membrane', 'the thin outer layer that controls what enters and leaves a cell', 'https://cdn.mosquizto.local/sample/biology-cell-membrane.png'),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 2, 'nucleus', 'the part of a cell that contains genetic material and controls activities', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 3, 'chromosome', 'a structure made of DNA that carries genetic information', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 4, 'mutation', 'a change in genetic material that may affect traits', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 5, 'dominant trait', 'a trait that appears when at least one matching gene is present', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 6, 'recessive trait', 'a trait that appears only when two matching genes are present', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 7, 'mitosis', 'the process in which one cell divides into two identical cells', NULL),
        ('teacher_lan_ielts', 'High School Biology - Cells and Genetics', 8, 'ecosystem', 'a community of living things and their physical environment', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 1, 'velocity', 'speed in a particular direction', 'https://cdn.mosquizto.local/sample/physics-velocity.png'),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 2, 'acceleration', 'the rate at which velocity changes over time', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 3, 'friction', 'a force that resists motion when surfaces touch', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 4, 'gravity', 'the force that pulls objects toward each other', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 5, 'momentum', 'the quantity of motion an object has because of its mass and velocity', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 6, 'kinetic energy', 'energy that an object has because it is moving', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 7, 'potential energy', 'stored energy based on position or condition', NULL),
        ('teacher_lan_ielts', 'High School Physics - Motion and Forces', 8, 'net force', 'the overall force acting on an object after forces are combined', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 1, 'urban area', 'a city or town with many people, buildings, and services', 'https://cdn.mosquizto.local/sample/geography-urban-area.png'),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 2, 'suburb', 'a residential area near the edge of a city', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 3, 'commuter', 'a person who travels regularly between home and work or school', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 4, 'traffic congestion', 'a situation where vehicles move slowly because roads are crowded', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 5, 'public transport', 'buses, trains, and other services used by the public', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 6, 'infrastructure', 'basic systems such as roads, bridges, power, and water supply', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 7, 'pedestrian zone', 'an area where people can walk and vehicles are limited or banned', NULL),
        ('teacher_lan_ielts', 'Geography English - Cities and Transport', 8, 'population density', 'the number of people living in a specific area', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 1, 'variable', 'a named place in a program that stores a value', 'https://cdn.mosquizto.local/sample/python-variable.png'),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 2, 'list', 'an ordered collection of values in Python', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 3, 'dictionary', 'a collection of key and value pairs', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 4, 'conditional statement', 'code that runs only when a condition is true', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 5, 'loop', 'code that repeats while a condition is true or for each item', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 6, 'function', 'a reusable block of code that performs a task', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 7, 'argument', 'a value passed into a function', NULL),
        ('teacher_minh_data', 'Python Starter - Data Types and Control Flow', 8, 'return value', 'the result that a function sends back after running', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 1, 'class', 'a blueprint used to create objects in object-oriented programming', 'https://cdn.mosquizto.local/sample/java-class.png'),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 2, 'object', 'an instance created from a class', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 3, 'interface', 'a type that defines methods a class can implement', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 4, 'inheritance', 'a mechanism where one class receives behavior from another class', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 5, 'polymorphism', 'the ability of code to treat different object types through a common type', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 6, 'exception', 'an event that interrupts normal program execution', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 7, 'stack trace', 'a report showing the sequence of method calls that led to an error', NULL),
        ('teacher_minh_data', 'Java Backend - OOP and Exceptions', 8, 'constructor', 'a special method used to initialize a new object', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 1, 'compute instance', 'a virtual server used to run applications in the cloud', 'https://cdn.mosquizto.local/sample/cloud-compute-instance.png'),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 2, 'object storage', 'a storage system that keeps data as objects with metadata', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 3, 'virtual private cloud', 'an isolated network area inside a cloud provider', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 4, 'load balancer', 'a service that distributes traffic across multiple servers', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 5, 'identity policy', 'rules that define what a user or service can access', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 6, 'availability zone', 'a separate data center area within a cloud region', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 7, 'serverless function', 'code that runs on demand without managing servers directly', NULL),
        ('teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 8, 'managed database', 'a database service operated and maintained by the cloud provider', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 1, 'pipeline', 'an automated sequence of steps for building, testing, and releasing code', 'https://cdn.mosquizto.local/sample/devops-pipeline.png'),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 2, 'continuous integration', 'the practice of frequently merging and testing code changes', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 3, 'continuous delivery', 'the practice of keeping software ready to release safely', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 4, 'deployment', 'the process of putting software into an environment where users can access it', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 5, 'rollback', 'the act of returning software to a previous stable version', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 6, 'health check', 'a test that confirms a service is running correctly', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 7, 'alert', 'a notification that warns about a system problem or threshold', NULL),
        ('teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 8, 'incident report', 'a written summary of what happened during a service problem', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 1, 'phishing', 'a trick that tries to steal private information through fake messages', 'https://cdn.mosquizto.local/sample/security-phishing.png'),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 2, 'malware', 'software designed to damage, disrupt, or gain unauthorized access', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 3, 'firewall', 'a system that filters network traffic based on security rules', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 4, 'multi-factor authentication', 'a login method that requires more than one proof of identity', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 5, 'vulnerability', 'a weakness that could be used to attack a system', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 6, 'encryption', 'the process of changing data into a protected unreadable form', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 7, 'security patch', 'an update that fixes a security weakness', NULL),
        ('teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 8, 'access control', 'rules and processes that limit who can use resources', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 1, 'user need', 'a problem or goal that a product should help a user address', 'https://cdn.mosquizto.local/sample/product-user-need.png'),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 2, 'stakeholder', 'a person or group affected by a product decision', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 3, 'roadmap', 'a high-level plan showing product priorities over time', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 4, 'backlog', 'a prioritized list of work that may be done by a team', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 5, 'acceptance criteria', 'conditions that must be met before work is considered complete', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 6, 'market segment', 'a group of customers with similar needs or traits', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 7, 'launch plan', 'a plan for releasing a product or feature to users', NULL),
        ('teacher_minh_data', 'Product Management - Discovery and Roadmap', 8, 'success metric', 'a measurement used to judge whether a product goal is achieved', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 1, 'usability test', 'a research method where people try to use a product while being observed', 'https://cdn.mosquizto.local/sample/ux-usability-test.png'),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 2, 'prototype', 'an early version used to test ideas before full development', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 3, 'participant', 'a person who takes part in a research session', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 4, 'task scenario', 'a realistic activity given to a user during a test', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 5, 'pain point', 'a problem or frustration experienced by a user', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 6, 'insight', 'a useful understanding discovered from research data', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 7, 'moderator', 'a person who guides a research session', NULL),
        ('teacher_minh_data', 'UX Research - Interviews and Usability', 8, 'satisfaction score', 'a rating that shows how satisfied users are with an experience', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 1, 'target audience', 'the group of people a campaign is designed to reach', 'https://cdn.mosquizto.local/sample/marketing-target-audience.png'),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 2, 'impression', 'one view or display of an advertisement or piece of content', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 3, 'click-through rate', 'the percentage of views that lead to clicks', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 4, 'landing page', 'a web page where visitors arrive after clicking a campaign link', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 5, 'lead', 'a potential customer who has shown interest in a product or service', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 6, 'retention', 'the ability to keep customers or users over time', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 7, 'cost per acquisition', 'the average cost of gaining one new customer', NULL),
        ('teacher_minh_data', 'Digital Marketing - Campaign Metrics', 8, 'campaign budget', 'the amount of money planned for a marketing campaign', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 1, 'income', 'money received from work, business, or investments', 'https://cdn.mosquizto.local/sample/finance-budget.png'),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 2, 'expense', 'money spent on goods, services, or obligations', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 3, 'emergency fund', 'money saved for unexpected problems or urgent needs', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 4, 'compound interest', 'interest calculated on both the original amount and earlier interest', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 5, 'diversification', 'spreading investments across different assets to reduce risk', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 6, 'portfolio', 'a collection of investments owned by a person or organization', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 7, 'credit score', 'a number that estimates how likely a person is to repay debt', NULL),
        ('teacher_minh_data', 'Personal Finance - Budgeting and Investing', 8, 'cash flow', 'the movement of money in and out over a period of time', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 1, 'sample', 'a smaller group selected from a larger population for analysis', 'https://cdn.mosquizto.local/sample/statistics-sample.png'),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 2, 'population', 'the full group that a study or analysis is interested in', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 3, 'probability', 'a measure of how likely an event is to happen', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 4, 'distribution', 'the pattern showing how values appear in a dataset', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 5, 'sampling bias', 'a problem caused when a sample does not represent the population well', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 6, 'confidence interval', 'a range of values likely to contain the true population value', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 7, 'standard deviation', 'a measure of how spread out values are from the average', NULL),
        ('teacher_minh_data', 'Statistics Starter - Probability and Sampling', 8, 'correlation', 'a relationship showing how two variables move together', NULL)
)
INSERT INTO tbl_collection_item (term, definition, image_url, order_index, collection_id, created_at, updated_at)
SELECT
    si.term,
    si.definition,
    si.image_url,
    si.order_index,
    c.id,
    TIMESTAMP '2026-05-04 09:00:00' + ((si.order_index - 1) * INTERVAL '3 minutes'),
    TIMESTAMP '2026-05-04 09:00:00' + ((si.order_index - 1) * INTERVAL '3 minutes')
FROM seed_items si
JOIN tbl_user owner ON owner.username = si.owner_username
JOIN tbl_collection c ON c.title = si.collection_title AND c.user_id = owner.id
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_collection_item ci
    WHERE ci.collection_id = c.id
      AND ci.order_index = si.order_index
);

-- ----------------------------
-- USER COLLECTION MEMBERSHIPS
-- ----------------------------
WITH seed_memberships(username, owner_username, collection_title, member_role, access_status, last_opened_at, created_at, updated_at) AS (
    VALUES
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 09:15:00', TIMESTAMP '2026-04-25 09:00:00', TIMESTAMP '2026-05-04 09:15:00'),
        ('teacher_minh_data', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-03 14:10:00', TIMESTAMP '2026-04-25 09:05:00', TIMESTAMP '2026-05-03 14:10:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 10:00:00', TIMESTAMP '2026-04-25 09:10:00', TIMESTAMP '2026-05-04 10:00:00'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-05 07:30:00', TIMESTAMP '2026-04-25 10:00:00', TIMESTAMP '2026-05-05 07:45:00'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 20:10:00', TIMESTAMP '2026-04-25 10:05:00', TIMESTAMP '2026-05-04 20:25:00'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 21:00:00', TIMESTAMP '2026-04-25 10:10:00', TIMESTAMP '2026-05-04 21:16:00'),
        ('student_dung', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 22:00:00', TIMESTAMP '2026-04-25 10:15:00', TIMESTAMP '2026-05-03 22:10:00'),
        ('student_hieu', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-01 08:00:00', TIMESTAMP '2026-05-01 08:00:00'),
        ('student_khoa_pending', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-01 08:30:00'),
        ('student_tram_denied', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-01 09:00:00', TIMESTAMP '2026-05-01 09:30:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 10:15:00', TIMESTAMP '2026-04-26 09:00:00', TIMESTAMP '2026-05-04 10:15:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 11:00:00', TIMESTAMP '2026-04-26 09:05:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:10:00', TIMESTAMP '2026-04-26 10:00:00', TIMESTAMP '2026-05-04 19:10:00'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:20:00', TIMESTAMP '2026-04-26 10:05:00', TIMESTAMP '2026-05-04 19:20:00'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 21:00:00', TIMESTAMP '2026-04-26 10:10:00', TIMESTAMP '2026-05-04 21:16:00'),
        ('student_linh', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:20:00', TIMESTAMP '2026-04-26 10:15:00', TIMESTAMP '2026-05-05 07:20:00'),
        ('student_khoa_pending', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-01 08:35:00', TIMESTAMP '2026-05-01 08:35:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 11:00:00', TIMESTAMP '2026-04-27 09:00:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('teacher_minh_data', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-03 15:00:00', TIMESTAMP '2026-04-27 09:05:00', TIMESTAMP '2026-05-03 15:00:00'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 21:00:00', TIMESTAMP '2026-04-27 10:00:00', TIMESTAMP '2026-05-03 21:00:00'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 21:30:00', TIMESTAMP '2026-04-27 10:05:00', TIMESTAMP '2026-05-03 21:30:00'),
        ('student_nam', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 09:00:00', TIMESTAMP '2026-05-02 09:00:00'),
        ('student_quyen', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 09:10:00', TIMESTAMP '2026-05-02 09:40:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 14:35:00', TIMESTAMP '2026-04-28 14:00:00', TIMESTAMP '2026-05-04 14:35:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 14:45:00', TIMESTAMP '2026-04-28 14:05:00', TIMESTAMP '2026-05-04 14:45:00'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 22:00:00', TIMESTAMP '2026-04-28 15:00:00', TIMESTAMP '2026-05-04 22:00:00'),
        ('student_linh', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 22:15:00', TIMESTAMP '2026-04-28 15:05:00', TIMESTAMP '2026-05-04 22:15:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-03 09:40:00', TIMESTAMP '2026-04-25 15:00:00', TIMESTAMP '2026-05-03 09:40:00'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-02 18:05:00', TIMESTAMP '2026-04-25 16:00:00', TIMESTAMP '2026-05-02 18:17:00'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 17:30:00', TIMESTAMP '2026-04-25 16:05:00', TIMESTAMP '2026-05-03 17:30:00'),
        ('student_an', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 20:00:00', TIMESTAMP '2026-04-25 16:10:00', TIMESTAMP '2026-05-02 20:00:00'),
        ('student_bao', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 20:20:00', TIMESTAMP '2026-04-25 16:15:00', TIMESTAMP '2026-05-02 20:20:00'),
        ('student_chi', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-01 11:00:00', TIMESTAMP '2026-05-01 11:00:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-03 10:20:00', TIMESTAMP '2026-04-26 15:00:00', TIMESTAMP '2026-05-03 10:20:00'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 17:20:00', TIMESTAMP '2026-04-26 16:00:00', TIMESTAMP '2026-05-03 17:20:00'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 17:45:00', TIMESTAMP '2026-04-26 16:05:00', TIMESTAMP '2026-05-03 17:55:00'),
        ('student_tram_denied', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-01 11:20:00', TIMESTAMP '2026-05-01 11:50:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 16:00:00', TIMESTAMP '2026-04-29 08:30:00', TIMESTAMP '2026-05-04 16:00:00'),
        ('co_b', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 16:20:00', TIMESTAMP '2026-04-29 08:40:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 16:35:00', TIMESTAMP '2026-04-29 08:45:00', TIMESTAMP '2026-05-04 16:35:00'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:00:00', TIMESTAMP '2026-04-29 09:00:00', TIMESTAMP '2026-05-04 19:00:00'),
        ('student_an', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:40:00', TIMESTAMP '2026-04-29 09:05:00', TIMESTAMP '2026-05-05 07:40:00'),
        ('student_bao', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 20:35:00', TIMESTAMP '2026-04-29 09:10:00', TIMESTAMP '2026-05-04 20:35:00'),
        ('student_khoa_pending', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 08:00:00', TIMESTAMP '2026-05-02 08:00:00'),
        ('student_tram_denied', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 08:20:00', TIMESTAMP '2026-05-02 08:45:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 16:10:00', TIMESTAMP '2026-04-29 09:30:00', TIMESTAMP '2026-05-04 16:10:00'),
        ('co_b', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 16:25:00', TIMESTAMP '2026-04-29 09:40:00', TIMESTAMP '2026-05-04 16:25:00'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:15:00', TIMESTAMP '2026-04-29 10:00:00', TIMESTAMP '2026-05-04 19:15:00'),
        ('student_chi', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 21:30:00', TIMESTAMP '2026-04-29 10:05:00', TIMESTAMP '2026-05-04 21:30:00'),
        ('student_dung', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 22:30:00', TIMESTAMP '2026-04-29 10:10:00', TIMESTAMP '2026-05-03 22:30:00'),
        ('student_hieu', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 08:10:00', TIMESTAMP '2026-05-02 08:10:00'),
        ('student_07', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 08:30:00', TIMESTAMP '2026-05-02 08:55:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 16:20:00', TIMESTAMP '2026-04-29 10:30:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('teacher_minh_data', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 16:45:00', TIMESTAMP '2026-04-29 10:40:00', TIMESTAMP '2026-05-04 16:45:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:05:00', TIMESTAMP '2026-04-29 10:45:00', TIMESTAMP '2026-05-04 17:05:00'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 22:20:00', TIMESTAMP '2026-04-29 11:00:00', TIMESTAMP '2026-05-04 22:20:00'),
        ('student_linh', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:25:00', TIMESTAMP '2026-04-29 11:05:00', TIMESTAMP '2026-05-05 07:25:00'),
        ('student_03', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 18:50:00', TIMESTAMP '2026-04-29 11:10:00', TIMESTAMP '2026-05-04 18:50:00'),
        ('student_nam', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 08:40:00', TIMESTAMP '2026-05-02 08:40:00'),
        ('student_quyen', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 09:00:00', TIMESTAMP '2026-05-02 09:25:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 16:30:00', TIMESTAMP '2026-04-29 11:30:00', TIMESTAMP '2026-05-04 16:30:00'),
        ('co_b', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 16:50:00', TIMESTAMP '2026-04-29 11:40:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('student_04', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:25:00', TIMESTAMP '2026-04-29 12:00:00', TIMESTAMP '2026-05-04 19:25:00'),
        ('student_05', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:45:00', TIMESTAMP '2026-04-29 12:05:00', TIMESTAMP '2026-05-04 19:45:00'),
        ('student_bao', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 20:45:00', TIMESTAMP '2026-04-29 12:10:00', TIMESTAMP '2026-05-04 20:45:00'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:28:00', TIMESTAMP '2026-04-29 12:15:00', TIMESTAMP '2026-05-05 07:28:00'),
        ('student_khoa_pending', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 09:10:00', TIMESTAMP '2026-05-02 09:10:00'),
        ('student_08', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 09:30:00', TIMESTAMP '2026-05-02 09:50:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 16:40:00', TIMESTAMP '2026-04-29 13:30:00', TIMESTAMP '2026-05-04 16:40:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:00:00', TIMESTAMP '2026-04-29 13:40:00', TIMESTAMP '2026-05-04 17:00:00'),
        ('student_06', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:50:00', TIMESTAMP '2026-04-29 14:00:00', TIMESTAMP '2026-05-04 19:50:00'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 21:40:00', TIMESTAMP '2026-04-29 14:05:00', TIMESTAMP '2026-05-04 21:40:00'),
        ('student_dung', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 22:40:00', TIMESTAMP '2026-04-29 14:10:00', TIMESTAMP '2026-05-03 22:40:00'),
        ('student_09', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 09:40:00', TIMESTAMP '2026-05-02 09:40:00'),
        ('student_tram_denied', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 09:55:00', TIMESTAMP '2026-05-02 10:20:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 16:50:00', TIMESTAMP '2026-04-30 08:30:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('co_b', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:10:00', TIMESTAMP '2026-04-30 08:40:00', TIMESTAMP '2026-05-04 17:10:00'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:05:00', TIMESTAMP '2026-04-30 09:00:00', TIMESTAMP '2026-05-04 19:05:00'),
        ('student_02', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:20:00', TIMESTAMP '2026-04-30 09:05:00', TIMESTAMP '2026-05-04 19:20:00'),
        ('student_an', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:42:00', TIMESTAMP '2026-04-30 09:10:00', TIMESTAMP '2026-05-05 07:42:00'),
        ('student_linh', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:32:00', TIMESTAMP '2026-04-30 09:15:00', TIMESTAMP '2026-05-05 07:32:00'),
        ('student_hieu', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 10:00:00', TIMESTAMP '2026-05-02 10:00:00'),
        ('student_10', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 10:20:00', TIMESTAMP '2026-05-02 10:45:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 17:00:00', TIMESTAMP '2026-04-30 09:30:00', TIMESTAMP '2026-05-04 17:00:00'),
        ('co_b', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:15:00', TIMESTAMP '2026-04-30 09:40:00', TIMESTAMP '2026-05-04 17:15:00'),
        ('student_03', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:35:00', TIMESTAMP '2026-04-30 10:00:00', TIMESTAMP '2026-05-04 19:35:00'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 19:55:00', TIMESTAMP '2026-04-30 10:05:00', TIMESTAMP '2026-05-04 19:55:00'),
        ('student_bao', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 20:50:00', TIMESTAMP '2026-04-30 10:10:00', TIMESTAMP '2026-05-04 20:50:00'),
        ('student_chi', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 21:45:00', TIMESTAMP '2026-04-30 10:15:00', TIMESTAMP '2026-05-04 21:45:00'),
        ('student_khoa_pending', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 10:30:00', TIMESTAMP '2026-05-02 10:30:00'),
        ('student_07', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 10:50:00', TIMESTAMP '2026-05-02 11:15:00'),
        ('teacher_lan_ielts', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 17:10:00', TIMESTAMP '2026-04-30 10:30:00', TIMESTAMP '2026-05-04 17:10:00'),
        ('mentor_phuong', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:25:00', TIMESTAMP '2026-04-30 10:40:00', TIMESTAMP '2026-05-04 17:25:00'),
        ('student_05', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 20:00:00', TIMESTAMP '2026-04-30 11:00:00', TIMESTAMP '2026-05-04 20:00:00'),
        ('student_06', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 20:10:00', TIMESTAMP '2026-04-30 11:05:00', TIMESTAMP '2026-05-04 20:10:00'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 22:50:00', TIMESTAMP '2026-04-30 11:10:00', TIMESTAMP '2026-05-03 22:50:00'),
        ('student_linh', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:33:00', TIMESTAMP '2026-04-30 11:15:00', TIMESTAMP '2026-05-05 07:33:00'),
        ('student_nam', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 11:00:00', TIMESTAMP '2026-05-02 11:00:00'),
        ('student_tram_denied', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 11:20:00', TIMESTAMP '2026-05-02 11:45:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 17:20:00', TIMESTAMP '2026-04-30 14:00:00', TIMESTAMP '2026-05-04 17:20:00'),
        ('david_sb', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:35:00', TIMESTAMP '2026-04-30 14:10:00', TIMESTAMP '2026-05-04 17:35:00'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-03 18:05:00', TIMESTAMP '2026-04-30 14:30:00', TIMESTAMP '2026-05-03 18:05:00'),
        ('student_quyen', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 18:15:00', TIMESTAMP '2026-04-30 14:35:00', TIMESTAMP '2026-05-03 18:15:00'),
        ('student_an', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 20:30:00', TIMESTAMP '2026-04-30 14:40:00', TIMESTAMP '2026-05-02 20:30:00'),
        ('student_bao', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 20:45:00', TIMESTAMP '2026-04-30 14:45:00', TIMESTAMP '2026-05-02 20:45:00'),
        ('student_chi', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 12:00:00', TIMESTAMP '2026-05-02 12:00:00'),
        ('student_tram_denied', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 12:20:00', TIMESTAMP '2026-05-02 12:45:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 17:30:00', TIMESTAMP '2026-04-30 15:00:00', TIMESTAMP '2026-05-04 17:30:00'),
        ('david_sb', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:40:00', TIMESTAMP '2026-04-30 15:10:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('student_nam', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-03 18:20:00', TIMESTAMP '2026-04-30 15:30:00', TIMESTAMP '2026-05-03 18:20:00'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 18:30:00', TIMESTAMP '2026-04-30 15:35:00', TIMESTAMP '2026-05-03 18:30:00'),
        ('student_dung', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 23:00:00', TIMESTAMP '2026-04-30 15:40:00', TIMESTAMP '2026-05-03 23:00:00'),
        ('student_linh', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:36:00', TIMESTAMP '2026-04-30 15:45:00', TIMESTAMP '2026-05-05 07:36:00'),
        ('student_khoa_pending', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 12:30:00', TIMESTAMP '2026-05-02 12:30:00'),
        ('student_08', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 12:50:00', TIMESTAMP '2026-05-02 13:15:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 17:40:00', TIMESTAMP '2026-04-30 16:00:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('david_sb', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 17:55:00', TIMESTAMP '2026-04-30 16:10:00', TIMESTAMP '2026-05-04 17:55:00'),
        ('ops_admin_demo', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:10:00', TIMESTAMP '2026-04-30 16:15:00', TIMESTAMP '2026-05-04 18:10:00'),
        ('student_nam', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 18:35:00', TIMESTAMP '2026-04-30 16:30:00', TIMESTAMP '2026-05-03 18:35:00'),
        ('student_quyen', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 18:45:00', TIMESTAMP '2026-04-30 16:35:00', TIMESTAMP '2026-05-03 18:45:00'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 20:50:00', TIMESTAMP '2026-04-30 16:40:00', TIMESTAMP '2026-05-02 20:50:00'),
        ('student_hieu', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 13:00:00', TIMESTAMP '2026-05-02 13:00:00'),
        ('student_09', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 13:20:00', TIMESTAMP '2026-05-02 13:45:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 17:50:00', TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-04 17:50:00'),
        ('david_sb', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:05:00', TIMESTAMP '2026-05-01 08:40:00', TIMESTAMP '2026-05-04 18:05:00'),
        ('student_nam', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 18:50:00', TIMESTAMP '2026-05-01 09:00:00', TIMESTAMP '2026-05-03 18:50:00'),
        ('student_quyen', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 19:00:00', TIMESTAMP '2026-05-01 09:05:00', TIMESTAMP '2026-05-03 19:00:00'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:00:00', TIMESTAMP '2026-05-01 09:10:00', TIMESTAMP '2026-05-02 21:00:00'),
        ('student_chi', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 21:55:00', TIMESTAMP '2026-05-01 09:15:00', TIMESTAMP '2026-05-04 21:55:00'),
        ('student_khoa_pending', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 13:30:00', TIMESTAMP '2026-05-02 13:30:00'),
        ('student_10', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 13:50:00', TIMESTAMP '2026-05-02 14:15:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 18:00:00', TIMESTAMP '2026-05-01 09:30:00', TIMESTAMP '2026-05-04 18:00:00'),
        ('david_sb', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:15:00', TIMESTAMP '2026-05-01 09:40:00', TIMESTAMP '2026-05-04 18:15:00'),
        ('ops_admin_demo', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:25:00', TIMESTAMP '2026-05-01 09:45:00', TIMESTAMP '2026-05-04 18:25:00'),
        ('student_an', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:05:00', TIMESTAMP '2026-05-01 10:00:00', TIMESTAMP '2026-05-02 21:05:00'),
        ('student_bao', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:15:00', TIMESTAMP '2026-05-01 10:05:00', TIMESTAMP '2026-05-02 21:15:00'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:38:00', TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-05 07:38:00'),
        ('student_nam', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 14:00:00', TIMESTAMP '2026-05-02 14:00:00'),
        ('student_tram_denied', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 14:20:00', TIMESTAMP '2026-05-02 14:45:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 18:10:00', TIMESTAMP '2026-05-01 10:30:00', TIMESTAMP '2026-05-04 18:10:00'),
        ('mentor_phuong', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:30:00', TIMESTAMP '2026-05-01 10:40:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('student_nam', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 19:10:00', TIMESTAMP '2026-05-01 11:00:00', TIMESTAMP '2026-05-03 19:10:00'),
        ('student_quyen', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 19:20:00', TIMESTAMP '2026-05-01 11:05:00', TIMESTAMP '2026-05-03 19:20:00'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 22:05:00', TIMESTAMP '2026-05-01 11:10:00', TIMESTAMP '2026-05-04 22:05:00'),
        ('student_dung', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 23:10:00', TIMESTAMP '2026-05-01 11:15:00', TIMESTAMP '2026-05-03 23:10:00'),
        ('student_hieu', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 14:30:00', TIMESTAMP '2026-05-02 14:30:00'),
        ('student_07', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 14:50:00', TIMESTAMP '2026-05-02 15:15:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 18:20:00', TIMESTAMP '2026-05-01 11:30:00', TIMESTAMP '2026-05-04 18:20:00'),
        ('mentor_phuong', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:35:00', TIMESTAMP '2026-05-01 11:40:00', TIMESTAMP '2026-05-04 18:35:00'),
        ('student_an', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:20:00', TIMESTAMP '2026-05-01 12:00:00', TIMESTAMP '2026-05-02 21:20:00'),
        ('student_bao', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:30:00', TIMESTAMP '2026-05-01 12:05:00', TIMESTAMP '2026-05-02 21:30:00'),
        ('student_linh', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:39:00', TIMESTAMP '2026-05-01 12:10:00', TIMESTAMP '2026-05-05 07:39:00'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 19:30:00', TIMESTAMP '2026-05-01 12:15:00', TIMESTAMP '2026-05-03 19:30:00'),
        ('student_khoa_pending', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 15:00:00', TIMESTAMP '2026-05-02 15:00:00'),
        ('student_08', 'teacher_minh_data', 'UX Research - Interviews and Usability', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 15:20:00', TIMESTAMP '2026-05-02 15:45:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 18:30:00', TIMESTAMP '2026-05-01 13:30:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('teacher_lan_ielts', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:45:00', TIMESTAMP '2026-05-01 13:40:00', TIMESTAMP '2026-05-04 18:45:00'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 19:40:00', TIMESTAMP '2026-05-01 14:00:00', TIMESTAMP '2026-05-03 19:40:00'),
        ('student_quyen', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 19:50:00', TIMESTAMP '2026-05-01 14:05:00', TIMESTAMP '2026-05-03 19:50:00'),
        ('student_chi', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 22:15:00', TIMESTAMP '2026-05-01 14:10:00', TIMESTAMP '2026-05-04 22:15:00'),
        ('student_dung', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 23:20:00', TIMESTAMP '2026-05-01 14:15:00', TIMESTAMP '2026-05-03 23:20:00'),
        ('student_hieu', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 15:30:00', TIMESTAMP '2026-05-02 15:30:00'),
        ('student_09', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 15:50:00', TIMESTAMP '2026-05-02 16:15:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 18:40:00', TIMESTAMP '2026-05-01 14:30:00', TIMESTAMP '2026-05-04 18:40:00'),
        ('teacher_lan_ielts', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 18:55:00', TIMESTAMP '2026-05-01 14:40:00', TIMESTAMP '2026-05-04 18:55:00'),
        ('student_an', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:40:00', TIMESTAMP '2026-05-01 15:00:00', TIMESTAMP '2026-05-02 21:40:00'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-02 21:50:00', TIMESTAMP '2026-05-01 15:05:00', TIMESTAMP '2026-05-02 21:50:00'),
        ('student_linh', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-05 07:41:00', TIMESTAMP '2026-05-01 15:10:00', TIMESTAMP '2026-05-05 07:41:00'),
        ('student_nam', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 20:00:00', TIMESTAMP '2026-05-01 15:15:00', TIMESTAMP '2026-05-03 20:00:00'),
        ('student_khoa_pending', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 16:00:00', TIMESTAMP '2026-05-02 16:00:00'),
        ('student_10', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 16:20:00', TIMESTAMP '2026-05-02 16:45:00'),
        ('teacher_minh_data', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'OWNER', 'ENABLE', TIMESTAMP '2026-05-04 18:50:00', TIMESTAMP '2026-05-01 15:30:00', TIMESTAMP '2026-05-04 18:50:00'),
        ('david_sb', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'EDITOR', 'ENABLE', TIMESTAMP '2026-05-04 19:05:00', TIMESTAMP '2026-05-01 15:40:00', TIMESTAMP '2026-05-04 19:05:00'),
        ('student_nam', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 20:10:00', TIMESTAMP '2026-05-01 16:00:00', TIMESTAMP '2026-05-03 20:10:00'),
        ('student_quyen', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 20:20:00', TIMESTAMP '2026-05-01 16:05:00', TIMESTAMP '2026-05-03 20:20:00'),
        ('student_chi', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-04 22:25:00', TIMESTAMP '2026-05-01 16:10:00', TIMESTAMP '2026-05-04 22:25:00'),
        ('student_dung', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'VIEWER', 'ENABLE', TIMESTAMP '2026-05-03 23:30:00', TIMESTAMP '2026-05-01 16:15:00', TIMESTAMP '2026-05-03 23:30:00'),
        ('student_hieu', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'VIEWER', 'PENDING', NULL, TIMESTAMP '2026-05-02 16:30:00', TIMESTAMP '2026-05-02 16:30:00'),
        ('student_tram_denied', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 'VIEWER', 'DENIED', NULL, TIMESTAMP '2026-05-02 16:50:00', TIMESTAMP '2026-05-02 17:15:00')
)
INSERT INTO tbl_user_collection (user_id, collection_id, role, access_status, last_opened_at, created_at, updated_at)
SELECT
    member_user.id,
    c.id,
    sm.member_role::collection_role,
    sm.access_status::access_status,
    sm.last_opened_at,
    sm.created_at,
    sm.updated_at
FROM seed_memberships sm
JOIN tbl_user member_user ON member_user.username = sm.username
JOIN tbl_user owner_user ON owner_user.username = sm.owner_username
JOIN tbl_collection c ON c.title = sm.collection_title AND c.user_id = owner_user.id
ON CONFLICT (user_id, collection_id) DO NOTHING;

-- ----------------------------
-- COURSES
-- ----------------------------
WITH seed_courses(title, description, visibility, thumbnail_url, created_at, updated_at) AS (
    VALUES
        ('IELTS Foundation A2-B1 - May 2026', 'Four-week IELTS foundation course with guided vocabulary sets, speaking practice, and weekly quizzes.', TRUE, 'https://cdn.mosquizto.local/course/ielts-foundation-may-2026.jpg', TIMESTAMP '2026-04-25 08:30:00', TIMESTAMP '2026-05-04 18:00:00'),
        ('Data Analytics Starter - Cohort 04', 'Introductory analytics cohort focused on SQL foundations and dashboard vocabulary.', TRUE, 'https://cdn.mosquizto.local/course/data-analytics-cohort-04.jpg', TIMESTAMP '2026-04-25 14:30:00', TIMESTAMP '2026-05-03 18:00:00'),
        ('TOEIC Workplace Sprint - May 2026', 'Short workplace English course for meetings, email writing, and simple negotiation practice.', TRUE, 'https://cdn.mosquizto.local/course/toeic-workplace-sprint-may-2026.jpg', TIMESTAMP '2026-04-29 08:00:00', TIMESTAMP '2026-05-04 19:00:00'),
        ('Practical English for Travel and Health', 'Scenario-based English course for airport, hotel, clinic, and service conversations.', TRUE, 'https://cdn.mosquizto.local/course/practical-english-travel-health.jpg', TIMESTAMP '2026-04-29 11:00:00', TIMESTAMP '2026-05-04 19:10:00'),
        ('Science English Grade 10 Review', 'English vocabulary course for biology, physics, and geography topics in high school.', TRUE, 'https://cdn.mosquizto.local/course/science-english-grade-10.jpg', TIMESTAMP '2026-04-30 08:00:00', TIMESTAMP '2026-05-04 19:20:00'),
        ('Backend Engineering Foundations', 'Programming course covering Python basics, Java OOP, SQL, and backend vocabulary.', TRUE, 'https://cdn.mosquizto.local/course/backend-engineering-foundations.jpg', TIMESTAMP '2026-04-30 13:30:00', TIMESTAMP '2026-05-04 19:30:00'),
        ('Cloud DevOps Security Bootcamp', 'Operational technology course for cloud infrastructure, CI/CD, monitoring, and security basics.', FALSE, 'https://cdn.mosquizto.local/course/cloud-devops-security-bootcamp.jpg', TIMESTAMP '2026-05-01 08:00:00', TIMESTAMP '2026-05-04 19:40:00'),
        ('Product Growth and Analytics Lab', 'Applied product course for discovery, UX research, marketing metrics, finance, and statistics.', FALSE, 'https://cdn.mosquizto.local/course/product-growth-analytics-lab.jpg', TIMESTAMP '2026-05-01 10:00:00', TIMESTAMP '2026-05-04 19:50:00')
)
INSERT INTO tbl_course (title, description, visibility, thumbnail_url, created_at, updated_at)
SELECT
    sc.title,
    sc.description,
    sc.visibility,
    sc.thumbnail_url,
    sc.created_at,
    sc.updated_at
FROM seed_courses sc
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_course c
    WHERE c.title = sc.title
);

WITH seed_user_courses(username, course_title, course_role, access_status, joined_at, created_at, updated_at) AS (
    VALUES
        ('teacher_lan_ielts', 'IELTS Foundation A2-B1 - May 2026', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-25 08:30:00', TIMESTAMP '2026-04-25 08:30:00', TIMESTAMP '2026-05-04 18:00:00'),
        ('teacher_minh_data', 'IELTS Foundation A2-B1 - May 2026', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-25 08:35:00', TIMESTAMP '2026-04-25 08:35:00', TIMESTAMP '2026-05-03 15:00:00'),
        ('mentor_phuong', 'IELTS Foundation A2-B1 - May 2026', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-25 08:40:00', TIMESTAMP '2026-04-25 08:40:00', TIMESTAMP '2026-05-04 14:45:00'),
        ('student_an', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 10:00:00', TIMESTAMP '2026-04-25 10:00:00', TIMESTAMP '2026-05-05 07:45:00'),
        ('student_bao', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 10:05:00', TIMESTAMP '2026-04-25 10:05:00', TIMESTAMP '2026-05-04 20:25:00'),
        ('student_chi', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 10:10:00', TIMESTAMP '2026-04-25 10:10:00', TIMESTAMP '2026-05-04 21:16:00'),
        ('student_dung', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 10:15:00', TIMESTAMP '2026-04-25 10:15:00', TIMESTAMP '2026-05-03 22:10:00'),
        ('student_hieu', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 10:20:00', TIMESTAMP '2026-04-25 10:20:00', TIMESTAMP '2026-05-02 19:40:00'),
        ('student_linh', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 10:25:00', TIMESTAMP '2026-04-25 10:25:00', TIMESTAMP '2026-05-05 07:34:00'),
        ('student_khoa_pending', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-01 08:30:00'),
        ('student_tram_denied', 'IELTS Foundation A2-B1 - May 2026', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-01 09:00:00', TIMESTAMP '2026-05-01 09:00:00', TIMESTAMP '2026-05-01 09:30:00'),
        ('teacher_minh_data', 'Data Analytics Starter - Cohort 04', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-25 14:30:00', TIMESTAMP '2026-04-25 14:30:00', TIMESTAMP '2026-05-03 18:00:00'),
        ('student_nam', 'Data Analytics Starter - Cohort 04', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 16:00:00', TIMESTAMP '2026-04-25 16:00:00', TIMESTAMP '2026-05-02 18:17:00'),
        ('student_quyen', 'Data Analytics Starter - Cohort 04', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 16:05:00', TIMESTAMP '2026-04-25 16:05:00', TIMESTAMP '2026-05-03 17:55:00'),
        ('student_an', 'Data Analytics Starter - Cohort 04', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 16:10:00', TIMESTAMP '2026-04-25 16:10:00', TIMESTAMP '2026-05-02 20:00:00'),
        ('student_bao', 'Data Analytics Starter - Cohort 04', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-25 16:15:00', TIMESTAMP '2026-04-25 16:15:00', TIMESTAMP '2026-05-02 20:20:00'),
        ('student_chi', 'Data Analytics Starter - Cohort 04', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-01 11:00:00', TIMESTAMP '2026-05-01 11:00:00', TIMESTAMP '2026-05-01 11:00:00'),
        ('student_tram_denied', 'Data Analytics Starter - Cohort 04', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-01 11:20:00', TIMESTAMP '2026-05-01 11:20:00', TIMESTAMP '2026-05-01 11:50:00'),
        ('teacher_lan_ielts', 'TOEIC Workplace Sprint - May 2026', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-29 08:00:00', TIMESTAMP '2026-04-29 08:00:00', TIMESTAMP '2026-05-04 19:00:00'),
        ('co_b', 'TOEIC Workplace Sprint - May 2026', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-29 08:10:00', TIMESTAMP '2026-04-29 08:10:00', TIMESTAMP '2026-05-04 16:25:00'),
        ('mentor_phuong', 'TOEIC Workplace Sprint - May 2026', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-29 08:15:00', TIMESTAMP '2026-04-29 08:15:00', TIMESTAMP '2026-05-04 17:05:00'),
        ('student_01', 'TOEIC Workplace Sprint - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 09:00:00', TIMESTAMP '2026-04-29 09:00:00', TIMESTAMP '2026-05-04 19:00:00'),
        ('student_02', 'TOEIC Workplace Sprint - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 09:05:00', TIMESTAMP '2026-04-29 09:05:00', TIMESTAMP '2026-05-04 19:28:00'),
        ('student_an', 'TOEIC Workplace Sprint - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 09:10:00', TIMESTAMP '2026-04-29 09:10:00', TIMESTAMP '2026-05-05 07:40:00'),
        ('student_bao', 'TOEIC Workplace Sprint - May 2026', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 09:15:00', TIMESTAMP '2026-04-29 09:15:00', TIMESTAMP '2026-05-04 20:35:00'),
        ('student_khoa_pending', 'TOEIC Workplace Sprint - May 2026', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-02 08:00:00', TIMESTAMP '2026-05-02 08:00:00', TIMESTAMP '2026-05-02 08:00:00'),
        ('student_tram_denied', 'TOEIC Workplace Sprint - May 2026', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-02 08:20:00', TIMESTAMP '2026-05-02 08:20:00', TIMESTAMP '2026-05-02 08:45:00'),
        ('teacher_lan_ielts', 'Practical English for Travel and Health', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-29 11:00:00', TIMESTAMP '2026-04-29 11:00:00', TIMESTAMP '2026-05-04 19:10:00'),
        ('co_b', 'Practical English for Travel and Health', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-29 11:10:00', TIMESTAMP '2026-04-29 11:10:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('mentor_phuong', 'Practical English for Travel and Health', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-29 11:15:00', TIMESTAMP '2026-04-29 11:15:00', TIMESTAMP '2026-05-04 17:00:00'),
        ('student_04', 'Practical English for Travel and Health', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 12:00:00', TIMESTAMP '2026-04-29 12:00:00', TIMESTAMP '2026-05-04 19:25:00'),
        ('student_05', 'Practical English for Travel and Health', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 12:05:00', TIMESTAMP '2026-04-29 12:05:00', TIMESTAMP '2026-05-04 19:45:00'),
        ('student_chi', 'Practical English for Travel and Health', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 12:10:00', TIMESTAMP '2026-04-29 12:10:00', TIMESTAMP '2026-05-04 21:53:00'),
        ('student_linh', 'Practical English for Travel and Health', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-29 12:15:00', TIMESTAMP '2026-04-29 12:15:00', TIMESTAMP '2026-05-05 07:39:00'),
        ('student_09', 'Practical English for Travel and Health', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-02 09:40:00', TIMESTAMP '2026-05-02 09:40:00', TIMESTAMP '2026-05-02 09:40:00'),
        ('student_08', 'Practical English for Travel and Health', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-02 09:30:00', TIMESTAMP '2026-05-02 09:30:00', TIMESTAMP '2026-05-02 09:50:00'),
        ('teacher_lan_ielts', 'Science English Grade 10 Review', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-30 08:00:00', TIMESTAMP '2026-04-30 08:00:00', TIMESTAMP '2026-05-04 19:20:00'),
        ('co_b', 'Science English Grade 10 Review', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-30 08:10:00', TIMESTAMP '2026-04-30 08:10:00', TIMESTAMP '2026-05-04 17:15:00'),
        ('mentor_phuong', 'Science English Grade 10 Review', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-30 08:15:00', TIMESTAMP '2026-04-30 08:15:00', TIMESTAMP '2026-05-04 17:25:00'),
        ('student_01', 'Science English Grade 10 Review', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 09:00:00', TIMESTAMP '2026-04-30 09:00:00', TIMESTAMP '2026-05-04 19:18:00'),
        ('student_02', 'Science English Grade 10 Review', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 09:05:00', TIMESTAMP '2026-04-30 09:05:00', TIMESTAMP '2026-05-04 19:20:00'),
        ('student_03', 'Science English Grade 10 Review', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 09:10:00', TIMESTAMP '2026-04-30 09:10:00', TIMESTAMP '2026-05-04 19:35:00'),
        ('student_04', 'Science English Grade 10 Review', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 09:15:00', TIMESTAMP '2026-04-30 09:15:00', TIMESTAMP '2026-05-04 20:10:00'),
        ('student_dung', 'Science English Grade 10 Review', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 09:20:00', TIMESTAMP '2026-04-30 09:20:00', TIMESTAMP '2026-05-03 23:04:00'),
        ('student_hieu', 'Science English Grade 10 Review', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-02 10:00:00', TIMESTAMP '2026-05-02 10:00:00', TIMESTAMP '2026-05-02 10:00:00'),
        ('student_10', 'Science English Grade 10 Review', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-02 10:20:00', TIMESTAMP '2026-05-02 10:20:00', TIMESTAMP '2026-05-02 10:45:00'),
        ('teacher_minh_data', 'Backend Engineering Foundations', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-30 13:30:00', TIMESTAMP '2026-04-30 13:30:00', TIMESTAMP '2026-05-04 19:30:00'),
        ('david_sb', 'Backend Engineering Foundations', 'TEACHER', 'ENABLE', TIMESTAMP '2026-04-30 13:40:00', TIMESTAMP '2026-04-30 13:40:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('student_nam', 'Backend Engineering Foundations', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 14:30:00', TIMESTAMP '2026-04-30 14:30:00', TIMESTAMP '2026-05-03 18:20:00'),
        ('student_quyen', 'Backend Engineering Foundations', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 14:35:00', TIMESTAMP '2026-04-30 14:35:00', TIMESTAMP '2026-05-03 18:44:00'),
        ('student_an', 'Backend Engineering Foundations', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 14:40:00', TIMESTAMP '2026-04-30 14:40:00', TIMESTAMP '2026-05-02 20:30:00'),
        ('student_bao', 'Backend Engineering Foundations', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 14:45:00', TIMESTAMP '2026-04-30 14:45:00', TIMESTAMP '2026-05-02 20:45:00'),
        ('student_linh', 'Backend Engineering Foundations', 'STUDENT', 'ENABLE', TIMESTAMP '2026-04-30 14:50:00', TIMESTAMP '2026-04-30 14:50:00', TIMESTAMP '2026-05-05 07:36:00'),
        ('student_chi', 'Backend Engineering Foundations', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-02 12:00:00', TIMESTAMP '2026-05-02 12:00:00', TIMESTAMP '2026-05-02 12:00:00'),
        ('student_08', 'Backend Engineering Foundations', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-02 12:50:00', TIMESTAMP '2026-05-02 12:50:00', TIMESTAMP '2026-05-02 13:15:00'),
        ('teacher_minh_data', 'Cloud DevOps Security Bootcamp', 'TEACHER', 'ENABLE', TIMESTAMP '2026-05-01 08:00:00', TIMESTAMP '2026-05-01 08:00:00', TIMESTAMP '2026-05-04 19:40:00'),
        ('david_sb', 'Cloud DevOps Security Bootcamp', 'TEACHER', 'ENABLE', TIMESTAMP '2026-05-01 08:10:00', TIMESTAMP '2026-05-01 08:10:00', TIMESTAMP '2026-05-04 18:15:00'),
        ('ops_admin_demo', 'Cloud DevOps Security Bootcamp', 'TEACHER', 'ENABLE', TIMESTAMP '2026-05-01 08:15:00', TIMESTAMP '2026-05-01 08:15:00', TIMESTAMP '2026-05-04 18:25:00'),
        ('student_an', 'Cloud DevOps Security Bootcamp', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 10:00:00', TIMESTAMP '2026-05-01 10:00:00', TIMESTAMP '2026-05-02 21:06:00'),
        ('student_bao', 'Cloud DevOps Security Bootcamp', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 10:05:00', TIMESTAMP '2026-05-01 10:05:00', TIMESTAMP '2026-05-02 21:14:00'),
        ('student_linh', 'Cloud DevOps Security Bootcamp', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-05 07:51:00'),
        ('student_nam', 'Cloud DevOps Security Bootcamp', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 10:15:00', TIMESTAMP '2026-05-01 10:15:00', TIMESTAMP '2026-05-03 18:50:00'),
        ('student_khoa_pending', 'Cloud DevOps Security Bootcamp', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-02 13:30:00', TIMESTAMP '2026-05-02 13:30:00', TIMESTAMP '2026-05-02 13:30:00'),
        ('student_tram_denied', 'Cloud DevOps Security Bootcamp', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-02 14:20:00', TIMESTAMP '2026-05-02 14:20:00', TIMESTAMP '2026-05-02 14:45:00'),
        ('teacher_minh_data', 'Product Growth and Analytics Lab', 'TEACHER', 'ENABLE', TIMESTAMP '2026-05-01 10:00:00', TIMESTAMP '2026-05-01 10:00:00', TIMESTAMP '2026-05-04 19:50:00'),
        ('teacher_lan_ielts', 'Product Growth and Analytics Lab', 'TEACHER', 'ENABLE', TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-04 18:55:00'),
        ('mentor_phuong', 'Product Growth and Analytics Lab', 'TEACHER', 'ENABLE', TIMESTAMP '2026-05-01 10:15:00', TIMESTAMP '2026-05-01 10:15:00', TIMESTAMP '2026-05-04 18:35:00'),
        ('student_nam', 'Product Growth and Analytics Lab', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 11:00:00', TIMESTAMP '2026-05-01 11:00:00', TIMESTAMP '2026-05-03 20:10:00'),
        ('student_quyen', 'Product Growth and Analytics Lab', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 11:05:00', TIMESTAMP '2026-05-01 11:05:00', TIMESTAMP '2026-05-03 20:20:00'),
        ('student_chi', 'Product Growth and Analytics Lab', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 11:10:00', TIMESTAMP '2026-05-01 11:10:00', TIMESTAMP '2026-05-04 22:25:00'),
        ('student_dung', 'Product Growth and Analytics Lab', 'STUDENT', 'ENABLE', TIMESTAMP '2026-05-01 11:15:00', TIMESTAMP '2026-05-01 11:15:00', TIMESTAMP '2026-05-03 23:38:00'),
        ('student_hieu', 'Product Growth and Analytics Lab', 'STUDENT', 'PENDING', TIMESTAMP '2026-05-02 16:30:00', TIMESTAMP '2026-05-02 16:30:00', TIMESTAMP '2026-05-02 16:30:00'),
        ('student_07', 'Product Growth and Analytics Lab', 'STUDENT', 'DENIED', TIMESTAMP '2026-05-02 14:50:00', TIMESTAMP '2026-05-02 14:50:00', TIMESTAMP '2026-05-02 15:15:00')
)
INSERT INTO tbl_user_course (user_id, course_id, role, joined_at, access_status, created_at, updated_at)
SELECT
    u.id,
    c.id,
    suc.course_role::course_role,
    suc.joined_at,
    suc.access_status::access_status,
    suc.created_at,
    suc.updated_at
FROM seed_user_courses suc
JOIN tbl_user u ON u.username = suc.username
JOIN tbl_course c ON c.title = suc.course_title
ON CONFLICT (user_id, course_id) DO NOTHING;

WITH seed_course_collections(course_title, owner_username, collection_title, order_index, access_status, created_at, updated_at) AS (
    VALUES
        ('IELTS Foundation A2-B1 - May 2026', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 1, 'ENABLE', TIMESTAMP '2026-04-25 08:45:00', TIMESTAMP '2026-05-04 09:30:00'),
        ('IELTS Foundation A2-B1 - May 2026', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 2, 'ENABLE', TIMESTAMP '2026-04-26 08:45:00', TIMESTAMP '2026-05-04 10:15:00'),
        ('IELTS Foundation A2-B1 - May 2026', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 3, 'PENDING', TIMESTAMP '2026-04-27 08:45:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('IELTS Foundation A2-B1 - May 2026', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 4, 'ENABLE', TIMESTAMP '2026-04-28 14:10:00', TIMESTAMP '2026-05-04 14:35:00'),
        ('Data Analytics Starter - Cohort 04', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 1, 'ENABLE', TIMESTAMP '2026-04-25 15:30:00', TIMESTAMP '2026-05-03 09:40:00'),
        ('Data Analytics Starter - Cohort 04', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 2, 'ENABLE', TIMESTAMP '2026-04-26 15:30:00', TIMESTAMP '2026-05-03 10:20:00'),
        ('TOEIC Workplace Sprint - May 2026', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 1, 'ENABLE', TIMESTAMP '2026-04-29 08:20:00', TIMESTAMP '2026-05-04 16:00:00'),
        ('TOEIC Workplace Sprint - May 2026', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 2, 'ENABLE', TIMESTAMP '2026-04-29 08:25:00', TIMESTAMP '2026-05-04 16:10:00'),
        ('TOEIC Workplace Sprint - May 2026', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 3, 'PENDING', TIMESTAMP '2026-04-29 08:30:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('Practical English for Travel and Health', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 1, 'ENABLE', TIMESTAMP '2026-04-29 11:20:00', TIMESTAMP '2026-05-04 16:30:00'),
        ('Practical English for Travel and Health', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 2, 'ENABLE', TIMESTAMP '2026-04-29 11:25:00', TIMESTAMP '2026-05-04 16:40:00'),
        ('Practical English for Travel and Health', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 3, 'ENABLE', TIMESTAMP '2026-04-29 11:30:00', TIMESTAMP '2026-05-04 16:10:00'),
        ('Science English Grade 10 Review', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 1, 'ENABLE', TIMESTAMP '2026-04-30 08:20:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('Science English Grade 10 Review', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 2, 'ENABLE', TIMESTAMP '2026-04-30 08:25:00', TIMESTAMP '2026-05-04 17:00:00'),
        ('Science English Grade 10 Review', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 3, 'ENABLE', TIMESTAMP '2026-04-30 08:30:00', TIMESTAMP '2026-05-04 17:10:00'),
        ('Science English Grade 10 Review', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 4, 'PENDING', TIMESTAMP '2026-04-30 08:35:00', TIMESTAMP '2026-05-04 10:15:00'),
        ('Backend Engineering Foundations', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 1, 'ENABLE', TIMESTAMP '2026-04-30 13:50:00', TIMESTAMP '2026-05-04 17:20:00'),
        ('Backend Engineering Foundations', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 2, 'ENABLE', TIMESTAMP '2026-04-30 13:55:00', TIMESTAMP '2026-05-04 17:30:00'),
        ('Backend Engineering Foundations', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 3, 'ENABLE', TIMESTAMP '2026-04-30 14:00:00', TIMESTAMP '2026-05-03 09:40:00'),
        ('Backend Engineering Foundations', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 4, 'PENDING', TIMESTAMP '2026-04-30 14:05:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('Cloud DevOps Security Bootcamp', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 1, 'ENABLE', TIMESTAMP '2026-05-01 08:20:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('Cloud DevOps Security Bootcamp', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 2, 'ENABLE', TIMESTAMP '2026-05-01 08:25:00', TIMESTAMP '2026-05-04 17:50:00'),
        ('Cloud DevOps Security Bootcamp', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 3, 'ENABLE', TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-04 18:00:00'),
        ('Cloud DevOps Security Bootcamp', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 4, 'PENDING', TIMESTAMP '2026-05-01 08:35:00', TIMESTAMP '2026-05-04 17:30:00'),
        ('Product Growth and Analytics Lab', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 1, 'ENABLE', TIMESTAMP '2026-05-01 10:20:00', TIMESTAMP '2026-05-04 18:10:00'),
        ('Product Growth and Analytics Lab', 'teacher_minh_data', 'UX Research - Interviews and Usability', 2, 'ENABLE', TIMESTAMP '2026-05-01 10:25:00', TIMESTAMP '2026-05-04 18:20:00'),
        ('Product Growth and Analytics Lab', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 3, 'ENABLE', TIMESTAMP '2026-05-01 10:30:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('Product Growth and Analytics Lab', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 4, 'ENABLE', TIMESTAMP '2026-05-01 10:35:00', TIMESTAMP '2026-05-04 18:40:00'),
        ('Product Growth and Analytics Lab', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 5, 'PENDING', TIMESTAMP '2026-05-01 10:40:00', TIMESTAMP '2026-05-04 18:50:00')
)
INSERT INTO tbl_course_collection (course_id, collection_id, order_index, access_status, created_at, updated_at)
SELECT
    course.id,
    collection.id,
    scc.order_index,
    scc.access_status::access_status,
    scc.created_at,
    scc.updated_at
FROM seed_course_collections scc
JOIN tbl_course course ON course.title = scc.course_title
JOIN tbl_user owner_user ON owner_user.username = scc.owner_username
JOIN tbl_collection collection ON collection.title = scc.collection_title AND collection.user_id = owner_user.id
ON CONFLICT (course_id, collection_id) DO NOTHING;

-- ----------------------------
-- FOLDERS
-- ----------------------------
WITH seed_folders(owner_username, folder_name, description, created_at, updated_at) AS (
    VALUES
        ('teacher_lan_ielts', 'IELTS May 2026 lesson packs', 'Teacher workspace for weekly IELTS vocabulary sets and speaking practice.', TIMESTAMP '2026-04-28 08:30:00', TIMESTAMP '2026-05-04 15:00:00'),
        ('mentor_phuong', 'Speaking feedback queue', 'Collections used for mentoring speaking answers and follow-up notes.', TIMESTAMP '2026-04-29 09:30:00', TIMESTAMP '2026-05-04 15:10:00'),
        ('student_an', 'Review before Friday quiz', 'Personal review folder for weak IELTS topics and analytics extras.', TIMESTAMP '2026-05-02 07:30:00', TIMESTAMP '2026-05-05 07:35:00'),
        ('teacher_minh_data', 'Analytics cohort 04 materials', 'Course collections used in the first analytics cohort.', TIMESTAMP '2026-04-26 14:30:00', TIMESTAMP '2026-05-03 11:00:00'),
        ('student_nam', 'SQL mistakes to retry', 'Collections that Nam wants to revisit after practice sessions.', TIMESTAMP '2026-05-02 18:20:00', TIMESTAMP '2026-05-03 17:25:00'),
        ('teacher_lan_ielts', 'TOEIC workplace sprint materials', 'Meeting, email, and negotiation collections for workplace English practice.', TIMESTAMP '2026-04-29 08:05:00', TIMESTAMP '2026-05-04 16:25:00'),
        ('teacher_lan_ielts', 'Practical English scenario cards', 'Travel, hotel, airport, and clinic vocabulary grouped for role-play classes.', TIMESTAMP '2026-04-29 11:05:00', TIMESTAMP '2026-05-04 16:45:00'),
        ('teacher_lan_ielts', 'Grade 10 science English review', 'Science vocabulary folders for biology, physics, geography, and environment review.', TIMESTAMP '2026-04-30 08:05:00', TIMESTAMP '2026-05-04 17:15:00'),
        ('co_b', 'Shared grade 10 support packs', 'Collections shared by Co B for grade 10 students who need extra practice.', TIMESTAMP '2026-04-30 08:20:00', TIMESTAMP '2026-05-04 17:20:00'),
        ('co_b', 'Class leader TOEIC support', 'Workplace English collections used by class leaders and peer tutors.', TIMESTAMP '2026-04-29 08:50:00', TIMESTAMP '2026-05-04 16:55:00'),
        ('mentor_phuong', 'Mentor feedback drills', 'Collections used when giving quick feedback after speaking and writing practice.', TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-04 18:35:00'),
        ('teacher_minh_data', 'Backend engineering track', 'Python, Java, SQL, and API-adjacent vocabulary for backend learners.', TIMESTAMP '2026-04-30 13:35:00', TIMESTAMP '2026-05-04 19:30:00'),
        ('teacher_minh_data', 'Cloud operations track', 'Cloud, DevOps, and security collections for operations-focused study.', TIMESTAMP '2026-05-01 08:05:00', TIMESTAMP '2026-05-04 19:40:00'),
        ('teacher_minh_data', 'Product growth lab board', 'Product, UX, marketing, finance, and statistics collections for growth experiments.', TIMESTAMP '2026-05-01 10:05:00', TIMESTAMP '2026-05-04 19:50:00'),
        ('david_sb', 'Guest expert backend picks', 'Technical collections David recommends for backend and cloud learners.', TIMESTAMP '2026-05-01 08:20:00', TIMESTAMP '2026-05-04 18:15:00'),
        ('student_linh', 'Morning review queue', 'Collections Linh opens before class for quick vocabulary warm-up.', TIMESTAMP '2026-05-05 07:20:00', TIMESTAMP '2026-05-05 07:51:00'),
        ('student_bao', 'DevOps retry list', 'Technical collections Bao marked for another round after slow answers.', TIMESTAMP '2026-05-02 21:20:00', TIMESTAMP '2026-05-02 22:04:00'),
        ('student_chi', 'Product vocabulary watchlist', 'Product and UX collections Chi wants to revisit after practice sessions.', TIMESTAMP '2026-05-04 22:00:00', TIMESTAMP '2026-05-04 22:25:00'),
        ('student_quyen', 'UX marketing revision', 'UX, marketing, and analytics collections for Quyen before the next lab.', TIMESTAMP '2026-05-03 19:25:00', TIMESTAMP '2026-05-03 20:20:00'),
        ('student_dung', 'Science and statistics quick review', 'Mixed science and statistics collections for short evening review.', TIMESTAMP '2026-05-03 22:45:00', TIMESTAMP '2026-05-03 23:38:00'),
        ('student_01', 'TOEIC meeting catch-up', 'Meeting and email vocabulary that student 01 needs before the workplace quiz.', TIMESTAMP '2026-05-04 18:55:00', TIMESTAMP '2026-05-04 19:18:00')
)
INSERT INTO tbl_folder (name, description, user_id, created_at, updated_at)
SELECT
    sf.folder_name,
    sf.description,
    u.id,
    sf.created_at,
    sf.updated_at
FROM seed_folders sf
JOIN tbl_user u ON u.username = sf.owner_username
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_folder f
    WHERE f.name = sf.folder_name
      AND f.user_id = u.id
);

WITH seed_folder_collections(folder_owner_username, folder_name, collection_owner_username, collection_title, order_index, created_at, updated_at) AS (
    VALUES
        ('teacher_lan_ielts', 'IELTS May 2026 lesson packs', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 1, TIMESTAMP '2026-04-28 08:35:00', TIMESTAMP '2026-05-04 09:30:00'),
        ('teacher_lan_ielts', 'IELTS May 2026 lesson packs', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 2, TIMESTAMP '2026-04-28 08:40:00', TIMESTAMP '2026-05-04 10:15:00'),
        ('teacher_lan_ielts', 'IELTS May 2026 lesson packs', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 3, TIMESTAMP '2026-04-28 08:45:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('teacher_lan_ielts', 'IELTS May 2026 lesson packs', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 4, TIMESTAMP '2026-04-28 08:50:00', TIMESTAMP '2026-05-04 14:35:00'),
        ('mentor_phuong', 'Speaking feedback queue', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 1, TIMESTAMP '2026-04-29 09:35:00', TIMESTAMP '2026-05-04 14:45:00'),
        ('mentor_phuong', 'Speaking feedback queue', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 2, TIMESTAMP '2026-04-29 09:40:00', TIMESTAMP '2026-05-04 10:00:00'),
        ('student_an', 'Review before Friday quiz', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 1, TIMESTAMP '2026-05-02 07:35:00', TIMESTAMP '2026-05-04 19:10:00'),
        ('student_an', 'Review before Friday quiz', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 2, TIMESTAMP '2026-05-02 07:40:00', TIMESTAMP '2026-05-04 22:00:00'),
        ('student_an', 'Review before Friday quiz', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 3, TIMESTAMP '2026-05-02 07:45:00', TIMESTAMP '2026-05-02 20:00:00'),
        ('teacher_minh_data', 'Analytics cohort 04 materials', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 1, TIMESTAMP '2026-04-26 14:35:00', TIMESTAMP '2026-05-03 09:40:00'),
        ('teacher_minh_data', 'Analytics cohort 04 materials', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 2, TIMESTAMP '2026-04-26 14:40:00', TIMESTAMP '2026-05-03 10:20:00'),
        ('student_nam', 'SQL mistakes to retry', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 1, TIMESTAMP '2026-05-02 18:25:00', TIMESTAMP '2026-05-02 18:25:00'),
        ('student_nam', 'SQL mistakes to retry', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 2, TIMESTAMP '2026-05-03 17:25:00', TIMESTAMP '2026-05-03 17:25:00'),
        ('teacher_lan_ielts', 'TOEIC workplace sprint materials', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 1, TIMESTAMP '2026-04-29 08:10:00', TIMESTAMP '2026-05-04 16:00:00'),
        ('teacher_lan_ielts', 'TOEIC workplace sprint materials', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 2, TIMESTAMP '2026-04-29 08:15:00', TIMESTAMP '2026-05-04 16:10:00'),
        ('teacher_lan_ielts', 'TOEIC workplace sprint materials', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 3, TIMESTAMP '2026-04-29 08:20:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('teacher_lan_ielts', 'TOEIC workplace sprint materials', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 4, TIMESTAMP '2026-04-29 08:25:00', TIMESTAMP '2026-05-04 14:35:00'),
        ('teacher_lan_ielts', 'Practical English scenario cards', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 1, TIMESTAMP '2026-04-29 11:10:00', TIMESTAMP '2026-05-04 16:30:00'),
        ('teacher_lan_ielts', 'Practical English scenario cards', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 2, TIMESTAMP '2026-04-29 11:15:00', TIMESTAMP '2026-05-04 16:40:00'),
        ('teacher_lan_ielts', 'Practical English scenario cards', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 3, TIMESTAMP '2026-04-29 11:20:00', TIMESTAMP '2026-05-04 16:10:00'),
        ('teacher_lan_ielts', 'Practical English scenario cards', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 4, TIMESTAMP '2026-04-29 11:25:00', TIMESTAMP '2026-05-04 14:35:00'),
        ('teacher_lan_ielts', 'Grade 10 science English review', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 1, TIMESTAMP '2026-04-30 08:10:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('teacher_lan_ielts', 'Grade 10 science English review', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 2, TIMESTAMP '2026-04-30 08:15:00', TIMESTAMP '2026-05-04 17:00:00'),
        ('teacher_lan_ielts', 'Grade 10 science English review', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 3, TIMESTAMP '2026-04-30 08:20:00', TIMESTAMP '2026-05-04 17:10:00'),
        ('teacher_lan_ielts', 'Grade 10 science English review', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 4, TIMESTAMP '2026-04-30 08:25:00', TIMESTAMP '2026-05-04 10:15:00'),
        ('co_b', 'Shared grade 10 support packs', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 1, TIMESTAMP '2026-04-30 08:25:00', TIMESTAMP '2026-05-04 17:10:00'),
        ('co_b', 'Shared grade 10 support packs', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 2, TIMESTAMP '2026-04-30 08:30:00', TIMESTAMP '2026-05-04 17:15:00'),
        ('co_b', 'Shared grade 10 support packs', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 3, TIMESTAMP '2026-04-30 08:35:00', TIMESTAMP '2026-05-04 17:20:00'),
        ('co_b', 'Shared grade 10 support packs', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 4, TIMESTAMP '2026-04-30 08:40:00', TIMESTAMP '2026-05-04 09:30:00'),
        ('co_b', 'Class leader TOEIC support', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 1, TIMESTAMP '2026-04-29 08:55:00', TIMESTAMP '2026-05-04 16:20:00'),
        ('co_b', 'Class leader TOEIC support', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 2, TIMESTAMP '2026-04-29 09:00:00', TIMESTAMP '2026-05-04 16:25:00'),
        ('co_b', 'Class leader TOEIC support', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 3, TIMESTAMP '2026-04-29 09:05:00', TIMESTAMP '2026-05-04 16:50:00'),
        ('mentor_phuong', 'Mentor feedback drills', 'teacher_lan_ielts', 'IELTS Speaking Bank - People and Places', 1, TIMESTAMP '2026-05-01 10:15:00', TIMESTAMP '2026-05-04 14:45:00'),
        ('mentor_phuong', 'Mentor feedback drills', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 2, TIMESTAMP '2026-05-01 10:20:00', TIMESTAMP '2026-05-04 17:05:00'),
        ('mentor_phuong', 'Mentor feedback drills', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 3, TIMESTAMP '2026-05-01 10:25:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('mentor_phuong', 'Mentor feedback drills', 'teacher_minh_data', 'UX Research - Interviews and Usability', 4, TIMESTAMP '2026-05-01 10:30:00', TIMESTAMP '2026-05-04 18:35:00'),
        ('teacher_minh_data', 'Backend engineering track', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 1, TIMESTAMP '2026-04-30 13:40:00', TIMESTAMP '2026-05-04 17:20:00'),
        ('teacher_minh_data', 'Backend engineering track', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 2, TIMESTAMP '2026-04-30 13:45:00', TIMESTAMP '2026-05-04 17:30:00'),
        ('teacher_minh_data', 'Backend engineering track', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 3, TIMESTAMP '2026-04-30 13:50:00', TIMESTAMP '2026-05-03 09:40:00'),
        ('teacher_minh_data', 'Backend engineering track', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Technology', 4, TIMESTAMP '2026-04-30 13:55:00', TIMESTAMP '2026-05-04 11:00:00'),
        ('teacher_minh_data', 'Cloud operations track', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 1, TIMESTAMP '2026-05-01 08:10:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('teacher_minh_data', 'Cloud operations track', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 2, TIMESTAMP '2026-05-01 08:15:00', TIMESTAMP '2026-05-04 17:50:00'),
        ('teacher_minh_data', 'Cloud operations track', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 3, TIMESTAMP '2026-05-01 08:20:00', TIMESTAMP '2026-05-04 18:00:00'),
        ('teacher_minh_data', 'Cloud operations track', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 4, TIMESTAMP '2026-05-01 08:25:00', TIMESTAMP '2026-05-04 17:30:00'),
        ('teacher_minh_data', 'Product growth lab board', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 1, TIMESTAMP '2026-05-01 10:10:00', TIMESTAMP '2026-05-04 18:10:00'),
        ('teacher_minh_data', 'Product growth lab board', 'teacher_minh_data', 'UX Research - Interviews and Usability', 2, TIMESTAMP '2026-05-01 10:15:00', TIMESTAMP '2026-05-04 18:20:00'),
        ('teacher_minh_data', 'Product growth lab board', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 3, TIMESTAMP '2026-05-01 10:20:00', TIMESTAMP '2026-05-04 18:30:00'),
        ('teacher_minh_data', 'Product growth lab board', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 4, TIMESTAMP '2026-05-01 10:25:00', TIMESTAMP '2026-05-04 18:40:00'),
        ('teacher_minh_data', 'Product growth lab board', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 5, TIMESTAMP '2026-05-01 10:30:00', TIMESTAMP '2026-05-04 18:50:00'),
        ('david_sb', 'Guest expert backend picks', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 1, TIMESTAMP '2026-05-01 08:25:00', TIMESTAMP '2026-05-04 17:40:00'),
        ('david_sb', 'Guest expert backend picks', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 2, TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-04 17:55:00'),
        ('david_sb', 'Guest expert backend picks', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 3, TIMESTAMP '2026-05-01 08:35:00', TIMESTAMP '2026-05-04 18:05:00'),
        ('david_sb', 'Guest expert backend picks', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 4, TIMESTAMP '2026-05-01 08:40:00', TIMESTAMP '2026-05-04 19:05:00'),
        ('student_linh', 'Morning review queue', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 1, TIMESTAMP '2026-05-05 07:22:00', TIMESTAMP '2026-05-05 07:39:00'),
        ('student_linh', 'Morning review queue', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 2, TIMESTAMP '2026-05-05 07:24:00', TIMESTAMP '2026-05-05 07:51:00'),
        ('student_linh', 'Morning review queue', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 3, TIMESTAMP '2026-05-05 07:26:00', TIMESTAMP '2026-05-05 07:25:00'),
        ('student_linh', 'Morning review queue', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 4, TIMESTAMP '2026-05-05 07:28:00', TIMESTAMP '2026-05-05 07:32:00'),
        ('student_bao', 'DevOps retry list', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 1, TIMESTAMP '2026-05-02 21:22:00', TIMESTAMP '2026-05-02 21:14:00'),
        ('student_bao', 'DevOps retry list', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 2, TIMESTAMP '2026-05-02 21:24:00', TIMESTAMP '2026-05-02 22:04:00'),
        ('student_bao', 'DevOps retry list', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 3, TIMESTAMP '2026-05-02 21:26:00', TIMESTAMP '2026-05-02 20:45:00'),
        ('student_bao', 'DevOps retry list', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 4, TIMESTAMP '2026-05-02 21:28:00', TIMESTAMP '2026-05-02 21:15:00'),
        ('student_chi', 'Product vocabulary watchlist', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 1, TIMESTAMP '2026-05-04 22:02:00', TIMESTAMP '2026-05-04 22:22:00'),
        ('student_chi', 'Product vocabulary watchlist', 'teacher_minh_data', 'UX Research - Interviews and Usability', 2, TIMESTAMP '2026-05-04 22:04:00', TIMESTAMP '2026-05-04 22:05:00'),
        ('student_chi', 'Product vocabulary watchlist', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 3, TIMESTAMP '2026-05-04 22:06:00', TIMESTAMP '2026-05-04 22:15:00'),
        ('student_chi', 'Product vocabulary watchlist', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 4, TIMESTAMP '2026-05-04 22:08:00', TIMESTAMP '2026-05-04 22:25:00'),
        ('student_quyen', 'UX marketing revision', 'teacher_minh_data', 'UX Research - Interviews and Usability', 1, TIMESTAMP '2026-05-03 19:27:00', TIMESTAMP '2026-05-03 19:45:00'),
        ('student_quyen', 'UX marketing revision', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 2, TIMESTAMP '2026-05-03 19:29:00', TIMESTAMP '2026-05-03 19:50:00'),
        ('student_quyen', 'UX marketing revision', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 3, TIMESTAMP '2026-05-03 19:31:00', TIMESTAMP '2026-05-03 20:20:00'),
        ('student_quyen', 'UX marketing revision', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 4, TIMESTAMP '2026-05-03 19:33:00', TIMESTAMP '2026-05-03 18:44:00'),
        ('student_dung', 'Science and statistics quick review', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 1, TIMESTAMP '2026-05-03 22:47:00', TIMESTAMP '2026-05-03 23:04:00'),
        ('student_dung', 'Science and statistics quick review', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 2, TIMESTAMP '2026-05-03 22:49:00', TIMESTAMP '2026-05-03 23:38:00'),
        ('student_dung', 'Science and statistics quick review', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 3, TIMESTAMP '2026-05-03 22:51:00', TIMESTAMP '2026-05-03 23:20:00'),
        ('student_dung', 'Science and statistics quick review', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 4, TIMESTAMP '2026-05-03 22:53:00', TIMESTAMP '2026-05-03 22:40:00'),
        ('student_01', 'TOEIC meeting catch-up', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 1, TIMESTAMP '2026-05-04 18:57:00', TIMESTAMP '2026-05-04 19:11:00'),
        ('student_01', 'TOEIC meeting catch-up', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 2, TIMESTAMP '2026-05-04 18:59:00', TIMESTAMP '2026-05-04 19:15:00'),
        ('student_01', 'TOEIC meeting catch-up', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 3, TIMESTAMP '2026-05-04 19:01:00', TIMESTAMP '2026-05-04 19:18:00')
)
INSERT INTO tbl_folder_collection (folder_id, collection_id, order_index, created_at, updated_at)
SELECT
    f.id,
    c.id,
    sfc.order_index,
    sfc.created_at,
    sfc.updated_at
FROM seed_folder_collections sfc
JOIN tbl_user folder_owner ON folder_owner.username = sfc.folder_owner_username
JOIN tbl_folder f ON f.name = sfc.folder_name AND f.user_id = folder_owner.id
JOIN tbl_user collection_owner ON collection_owner.username = sfc.collection_owner_username
JOIN tbl_collection c ON c.title = sfc.collection_title AND c.user_id = collection_owner.id
ON CONFLICT (folder_id, collection_id) DO NOTHING;

-- ----------------------------
-- STUDY SESSIONS
-- ----------------------------
WITH seed_sessions(username, owner_username, collection_title, total_score, total_correct, total_wrong, started_at, completed_at, created_at, updated_at) AS (
    VALUES
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 7, 7, 1, TIMESTAMP '2026-05-03 19:30:00', TIMESTAMP '2026-05-03 19:42:00', TIMESTAMP '2026-05-03 19:30:00', TIMESTAMP '2026-05-03 19:42:00'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 5, 5, 3, TIMESTAMP '2026-05-04 20:10:00', TIMESTAMP '2026-05-04 20:25:00', TIMESTAMP '2026-05-04 20:10:00', TIMESTAMP '2026-05-04 20:25:00'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', 6, 6, 2, TIMESTAMP '2026-05-04 21:00:00', TIMESTAMP '2026-05-04 21:16:00', TIMESTAMP '2026-05-04 21:00:00', TIMESTAMP '2026-05-04 21:16:00'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', 4, 4, 2, TIMESTAMP '2026-05-02 18:05:00', TIMESTAMP '2026-05-02 18:17:00', TIMESTAMP '2026-05-02 18:05:00', TIMESTAMP '2026-05-02 18:17:00'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', 5, 5, 1, TIMESTAMP '2026-05-03 17:45:00', TIMESTAMP '2026-05-03 17:55:00', TIMESTAMP '2026-05-03 17:45:00', TIMESTAMP '2026-05-03 17:55:00'),
        ('student_linh', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', 2, 2, 1, TIMESTAMP '2026-05-05 07:30:00', NULL, TIMESTAMP '2026-05-05 07:30:00', TIMESTAMP '2026-05-05 07:34:10'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', 5, 5, 1, TIMESTAMP '2026-05-04 19:00:00', TIMESTAMP '2026-05-04 19:11:00', TIMESTAMP '2026-05-04 19:00:00', TIMESTAMP '2026-05-04 19:11:00'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', 4, 4, 2, TIMESTAMP '2026-05-04 19:15:00', TIMESTAMP '2026-05-04 19:28:00', TIMESTAMP '2026-05-04 19:15:00', TIMESTAMP '2026-05-04 19:28:00'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', 5, 5, 1, TIMESTAMP '2026-05-04 22:20:00', TIMESTAMP '2026-05-04 22:34:00', TIMESTAMP '2026-05-04 22:20:00', TIMESTAMP '2026-05-04 22:34:00'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', 6, 6, 0, TIMESTAMP '2026-05-05 07:28:00', TIMESTAMP '2026-05-05 07:39:00', TIMESTAMP '2026-05-05 07:28:00', TIMESTAMP '2026-05-05 07:39:00'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', 4, 4, 2, TIMESTAMP '2026-05-04 21:40:00', TIMESTAMP '2026-05-04 21:53:00', TIMESTAMP '2026-05-04 21:40:00', TIMESTAMP '2026-05-04 21:53:00'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', 5, 5, 1, TIMESTAMP '2026-05-04 19:05:00', TIMESTAMP '2026-05-04 19:18:00', TIMESTAMP '2026-05-04 19:05:00', TIMESTAMP '2026-05-04 19:18:00'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', 3, 3, 3, TIMESTAMP '2026-05-04 19:55:00', TIMESTAMP '2026-05-04 20:10:00', TIMESTAMP '2026-05-04 19:55:00', TIMESTAMP '2026-05-04 20:10:00'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', 4, 4, 2, TIMESTAMP '2026-05-03 22:50:00', TIMESTAMP '2026-05-03 23:04:00', TIMESTAMP '2026-05-03 22:50:00', TIMESTAMP '2026-05-03 23:04:00'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', 5, 5, 1, TIMESTAMP '2026-05-03 18:05:00', TIMESTAMP '2026-05-03 18:16:00', TIMESTAMP '2026-05-03 18:05:00', TIMESTAMP '2026-05-03 18:16:00'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', 4, 4, 2, TIMESTAMP '2026-05-03 18:30:00', TIMESTAMP '2026-05-03 18:44:00', TIMESTAMP '2026-05-03 18:30:00', TIMESTAMP '2026-05-03 18:44:00'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', 3, 3, 3, TIMESTAMP '2026-05-02 20:50:00', TIMESTAMP '2026-05-02 21:06:00', TIMESTAMP '2026-05-02 20:50:00', TIMESTAMP '2026-05-02 21:06:00'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', 4, 4, 2, TIMESTAMP '2026-05-02 21:00:00', TIMESTAMP '2026-05-02 21:14:00', TIMESTAMP '2026-05-02 21:00:00', TIMESTAMP '2026-05-02 21:14:00'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', 5, 5, 1, TIMESTAMP '2026-05-05 07:38:00', TIMESTAMP '2026-05-05 07:51:00', TIMESTAMP '2026-05-05 07:38:00', TIMESTAMP '2026-05-05 07:51:00'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', 3, 3, 3, TIMESTAMP '2026-05-04 22:05:00', TIMESTAMP '2026-05-04 22:22:00', TIMESTAMP '2026-05-04 22:05:00', TIMESTAMP '2026-05-04 22:22:00'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', 4, 4, 2, TIMESTAMP '2026-05-03 19:30:00', TIMESTAMP '2026-05-03 19:45:00', TIMESTAMP '2026-05-03 19:30:00', TIMESTAMP '2026-05-03 19:45:00'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', 5, 5, 1, TIMESTAMP '2026-05-03 19:40:00', TIMESTAMP '2026-05-03 19:53:00', TIMESTAMP '2026-05-03 19:40:00', TIMESTAMP '2026-05-03 19:53:00'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', 4, 4, 2, TIMESTAMP '2026-05-02 21:50:00', TIMESTAMP '2026-05-02 22:04:00', TIMESTAMP '2026-05-02 21:50:00', TIMESTAMP '2026-05-02 22:04:00'),
        ('student_dung', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', 3, 3, 1, TIMESTAMP '2026-05-03 23:30:00', NULL, TIMESTAMP '2026-05-03 23:30:00', TIMESTAMP '2026-05-03 23:38:00')
)
INSERT INTO tbl_study_session (user_id, collection_id, total_score, total_correct, total_wrong, started_at, completed_at, created_at, updated_at)
SELECT
    learner.id,
    c.id,
    ss.total_score,
    ss.total_correct,
    ss.total_wrong,
    ss.started_at,
    ss.completed_at,
    ss.created_at,
    ss.updated_at
FROM seed_sessions ss
JOIN tbl_user learner ON learner.username = ss.username
JOIN tbl_user owner_user ON owner_user.username = ss.owner_username
JOIN tbl_collection c ON c.title = ss.collection_title AND c.user_id = owner_user.id
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_study_session existing
    WHERE existing.user_id = learner.id
      AND existing.collection_id = c.id
      AND existing.started_at = ss.started_at
);

WITH seed_session_details(username, owner_username, collection_title, session_started_at, item_order, is_correct, response_time_ms, answer_mode, answered_at) AS (
    VALUES
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 1, TRUE, 5200.0, TRUE, TIMESTAMP '2026-05-03 19:31:05'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 2, TRUE, 6100.5, FALSE, TIMESTAMP '2026-05-03 19:32:10'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 3, TRUE, 4800.0, TRUE, TIMESTAMP '2026-05-03 19:33:10'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 4, FALSE, 8300.0, FALSE, TIMESTAMP '2026-05-03 19:34:20'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 5, TRUE, 5700.0, TRUE, TIMESTAMP '2026-05-03 19:35:20'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 6, TRUE, 6900.0, FALSE, TIMESTAMP '2026-05-03 19:36:35'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 7, TRUE, 7300.0, TRUE, TIMESTAMP '2026-05-03 19:38:00'),
        ('student_an', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-03 19:30:00', 8, TRUE, 6600.0, TRUE, TIMESTAMP '2026-05-03 19:40:10'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 1, TRUE, 7200.0, TRUE, TIMESTAMP '2026-05-04 20:11:05'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 2, FALSE, 9800.0, FALSE, TIMESTAMP '2026-05-04 20:12:30'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 3, TRUE, 6800.0, TRUE, TIMESTAMP '2026-05-04 20:13:35'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 4, FALSE, 10100.0, TRUE, TIMESTAMP '2026-05-04 20:15:10'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 5, TRUE, 7600.0, FALSE, TIMESTAMP '2026-05-04 20:16:40'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 6, TRUE, 8100.0, TRUE, TIMESTAMP '2026-05-04 20:18:20'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 7, FALSE, 11200.0, FALSE, TIMESTAMP '2026-05-04 20:21:00'),
        ('student_bao', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-04 20:10:00', 8, TRUE, 7900.0, TRUE, TIMESTAMP '2026-05-04 20:23:30'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 1, TRUE, 6400.0, TRUE, TIMESTAMP '2026-05-04 21:01:10'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 2, TRUE, 7100.0, TRUE, TIMESTAMP '2026-05-04 21:02:15'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 3, FALSE, 11800.0, FALSE, TIMESTAMP '2026-05-04 21:04:05'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 4, TRUE, 8400.0, TRUE, TIMESTAMP '2026-05-04 21:05:50'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 5, TRUE, 7600.0, FALSE, TIMESTAMP '2026-05-04 21:07:10'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 6, FALSE, 10500.0, TRUE, TIMESTAMP '2026-05-04 21:09:10'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 7, TRUE, 6900.0, FALSE, TIMESTAMP '2026-05-04 21:11:40'),
        ('student_chi', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Environment', TIMESTAMP '2026-05-04 21:00:00', 8, TRUE, 6200.0, TRUE, TIMESTAMP '2026-05-04 21:14:30'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', TIMESTAMP '2026-05-02 18:05:00', 1, TRUE, 4300.0, TRUE, TIMESTAMP '2026-05-02 18:06:00'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', TIMESTAMP '2026-05-02 18:05:00', 2, TRUE, 5100.0, FALSE, TIMESTAMP '2026-05-02 18:07:20'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', TIMESTAMP '2026-05-02 18:05:00', 3, FALSE, 9200.0, TRUE, TIMESTAMP '2026-05-02 18:09:05'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', TIMESTAMP '2026-05-02 18:05:00', 4, TRUE, 6000.0, TRUE, TIMESTAMP '2026-05-02 18:10:40'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', TIMESTAMP '2026-05-02 18:05:00', 5, FALSE, 8700.0, FALSE, TIMESTAMP '2026-05-02 18:12:30'),
        ('student_nam', 'teacher_minh_data', 'Data Analytics Starter - SQL Basics', TIMESTAMP '2026-05-02 18:05:00', 6, TRUE, 5500.0, TRUE, TIMESTAMP '2026-05-02 18:14:45'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', TIMESTAMP '2026-05-03 17:45:00', 1, TRUE, 5200.0, TRUE, TIMESTAMP '2026-05-03 17:46:05'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', TIMESTAMP '2026-05-03 17:45:00', 2, TRUE, 6300.0, TRUE, TIMESTAMP '2026-05-03 17:47:15'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', TIMESTAMP '2026-05-03 17:45:00', 3, TRUE, 5800.0, FALSE, TIMESTAMP '2026-05-03 17:48:25'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', TIMESTAMP '2026-05-03 17:45:00', 4, FALSE, 9300.0, TRUE, TIMESTAMP '2026-05-03 17:50:00'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', TIMESTAMP '2026-05-03 17:45:00', 5, TRUE, 6100.0, FALSE, TIMESTAMP '2026-05-03 17:51:20'),
        ('student_quyen', 'teacher_minh_data', 'Data Analytics Starter - Charts and Metrics', TIMESTAMP '2026-05-03 17:45:00', 6, TRUE, 7000.0, TRUE, TIMESTAMP '2026-05-03 17:53:10'),
        ('student_linh', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-05 07:30:00', 1, TRUE, 5900.0, TRUE, TIMESTAMP '2026-05-05 07:31:00'),
        ('student_linh', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-05 07:30:00', 2, FALSE, 10400.0, FALSE, TIMESTAMP '2026-05-05 07:32:20'),
        ('student_linh', 'teacher_lan_ielts', 'IELTS Foundation May 2026 - Education', TIMESTAMP '2026-05-05 07:30:00', 3, TRUE, 6800.0, TRUE, TIMESTAMP '2026-05-05 07:34:10'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', TIMESTAMP '2026-05-04 19:00:00', 1, TRUE, 4900.0, TRUE, TIMESTAMP '2026-05-04 19:01:05'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', TIMESTAMP '2026-05-04 19:00:00', 2, TRUE, 6100.0, FALSE, TIMESTAMP '2026-05-04 19:02:40'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', TIMESTAMP '2026-05-04 19:00:00', 3, FALSE, 9200.0, TRUE, TIMESTAMP '2026-05-04 19:04:20'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', TIMESTAMP '2026-05-04 19:00:00', 4, TRUE, 5700.0, TRUE, TIMESTAMP '2026-05-04 19:06:05'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', TIMESTAMP '2026-05-04 19:00:00', 5, TRUE, 6800.0, FALSE, TIMESTAMP '2026-05-04 19:08:10'),
        ('student_01', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Meetings', TIMESTAMP '2026-05-04 19:00:00', 6, TRUE, 7200.0, TRUE, TIMESTAMP '2026-05-04 19:10:30'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', TIMESTAMP '2026-05-04 19:15:00', 1, TRUE, 5300.0, TRUE, TIMESTAMP '2026-05-04 19:16:00'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', TIMESTAMP '2026-05-04 19:15:00', 2, FALSE, 9900.0, FALSE, TIMESTAMP '2026-05-04 19:17:45'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', TIMESTAMP '2026-05-04 19:15:00', 3, TRUE, 6400.0, TRUE, TIMESTAMP '2026-05-04 19:19:20'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', TIMESTAMP '2026-05-04 19:15:00', 4, FALSE, 11200.0, TRUE, TIMESTAMP '2026-05-04 19:21:00'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', TIMESTAMP '2026-05-04 19:15:00', 5, TRUE, 7700.0, FALSE, TIMESTAMP '2026-05-04 19:23:10'),
        ('student_02', 'teacher_lan_ielts', 'TOEIC Workplace Essentials - Emails', TIMESTAMP '2026-05-04 19:15:00', 6, TRUE, 8100.0, TRUE, TIMESTAMP '2026-05-04 19:26:20'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', TIMESTAMP '2026-05-04 22:20:00', 1, TRUE, 4600.0, TRUE, TIMESTAMP '2026-05-04 22:21:00'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', TIMESTAMP '2026-05-04 22:20:00', 2, TRUE, 5500.0, TRUE, TIMESTAMP '2026-05-04 22:22:20'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', TIMESTAMP '2026-05-04 22:20:00', 3, TRUE, 6200.0, FALSE, TIMESTAMP '2026-05-04 22:24:05'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', TIMESTAMP '2026-05-04 22:20:00', 4, FALSE, 10400.0, TRUE, TIMESTAMP '2026-05-04 22:26:00'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', TIMESTAMP '2026-05-04 22:20:00', 5, TRUE, 7300.0, FALSE, TIMESTAMP '2026-05-04 22:29:20'),
        ('student_an', 'teacher_lan_ielts', 'Business English - Negotiation Basics', TIMESTAMP '2026-05-04 22:20:00', 6, TRUE, 8000.0, TRUE, TIMESTAMP '2026-05-04 22:33:10'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', TIMESTAMP '2026-05-05 07:28:00', 1, TRUE, 4100.0, TRUE, TIMESTAMP '2026-05-05 07:29:00'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', TIMESTAMP '2026-05-05 07:28:00', 2, TRUE, 4800.0, FALSE, TIMESTAMP '2026-05-05 07:30:30'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', TIMESTAMP '2026-05-05 07:28:00', 3, TRUE, 5200.0, TRUE, TIMESTAMP '2026-05-05 07:32:00'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', TIMESTAMP '2026-05-05 07:28:00', 4, TRUE, 5900.0, TRUE, TIMESTAMP '2026-05-05 07:34:00'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', TIMESTAMP '2026-05-05 07:28:00', 5, TRUE, 6300.0, FALSE, TIMESTAMP '2026-05-05 07:36:10'),
        ('student_linh', 'teacher_lan_ielts', 'Travel English - Airport and Hotel', TIMESTAMP '2026-05-05 07:28:00', 6, TRUE, 6900.0, TRUE, TIMESTAMP '2026-05-05 07:38:50'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', TIMESTAMP '2026-05-04 21:40:00', 1, TRUE, 6600.0, TRUE, TIMESTAMP '2026-05-04 21:41:00'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', TIMESTAMP '2026-05-04 21:40:00', 2, FALSE, 10300.0, FALSE, TIMESTAMP '2026-05-04 21:42:40'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', TIMESTAMP '2026-05-04 21:40:00', 3, TRUE, 7100.0, TRUE, TIMESTAMP '2026-05-04 21:44:10'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', TIMESTAMP '2026-05-04 21:40:00', 4, TRUE, 7600.0, TRUE, TIMESTAMP '2026-05-04 21:46:15'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', TIMESTAMP '2026-05-04 21:40:00', 5, FALSE, 11800.0, FALSE, TIMESTAMP '2026-05-04 21:49:00'),
        ('student_chi', 'teacher_lan_ielts', 'Health English - Clinic Conversations', TIMESTAMP '2026-05-04 21:40:00', 6, TRUE, 8200.0, TRUE, TIMESTAMP '2026-05-04 21:52:20'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', TIMESTAMP '2026-05-04 19:05:00', 1, TRUE, 5800.0, TRUE, TIMESTAMP '2026-05-04 19:06:15'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', TIMESTAMP '2026-05-04 19:05:00', 2, TRUE, 6400.0, TRUE, TIMESTAMP '2026-05-04 19:08:00'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', TIMESTAMP '2026-05-04 19:05:00', 3, FALSE, 9700.0, FALSE, TIMESTAMP '2026-05-04 19:10:10'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', TIMESTAMP '2026-05-04 19:05:00', 4, TRUE, 7200.0, TRUE, TIMESTAMP '2026-05-04 19:12:25'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', TIMESTAMP '2026-05-04 19:05:00', 5, TRUE, 7600.0, FALSE, TIMESTAMP '2026-05-04 19:15:00'),
        ('student_01', 'teacher_lan_ielts', 'High School Biology - Cells and Genetics', TIMESTAMP '2026-05-04 19:05:00', 6, TRUE, 8100.0, TRUE, TIMESTAMP '2026-05-04 19:17:30'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', TIMESTAMP '2026-05-04 19:55:00', 1, TRUE, 6200.0, TRUE, TIMESTAMP '2026-05-04 19:56:00'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', TIMESTAMP '2026-05-04 19:55:00', 2, FALSE, 10900.0, FALSE, TIMESTAMP '2026-05-04 19:58:20'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', TIMESTAMP '2026-05-04 19:55:00', 3, FALSE, 12600.0, TRUE, TIMESTAMP '2026-05-04 20:00:40'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', TIMESTAMP '2026-05-04 19:55:00', 4, TRUE, 8300.0, TRUE, TIMESTAMP '2026-05-04 20:03:00'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', TIMESTAMP '2026-05-04 19:55:00', 5, FALSE, 11400.0, FALSE, TIMESTAMP '2026-05-04 20:06:10'),
        ('student_04', 'teacher_lan_ielts', 'High School Physics - Motion and Forces', TIMESTAMP '2026-05-04 19:55:00', 6, TRUE, 8700.0, TRUE, TIMESTAMP '2026-05-04 20:09:30'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', TIMESTAMP '2026-05-03 22:50:00', 1, TRUE, 7100.0, TRUE, TIMESTAMP '2026-05-03 22:51:00'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', TIMESTAMP '2026-05-03 22:50:00', 2, TRUE, 7600.0, FALSE, TIMESTAMP '2026-05-03 22:52:40'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', TIMESTAMP '2026-05-03 22:50:00', 3, FALSE, 10800.0, TRUE, TIMESTAMP '2026-05-03 22:55:00'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', TIMESTAMP '2026-05-03 22:50:00', 4, TRUE, 8200.0, TRUE, TIMESTAMP '2026-05-03 22:57:30'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', TIMESTAMP '2026-05-03 22:50:00', 5, FALSE, 11900.0, FALSE, TIMESTAMP '2026-05-03 23:00:10'),
        ('student_dung', 'teacher_lan_ielts', 'Geography English - Cities and Transport', TIMESTAMP '2026-05-03 22:50:00', 6, TRUE, 9100.0, TRUE, TIMESTAMP '2026-05-03 23:03:30'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', TIMESTAMP '2026-05-03 18:05:00', 1, TRUE, 3900.0, TRUE, TIMESTAMP '2026-05-03 18:06:00'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', TIMESTAMP '2026-05-03 18:05:00', 2, TRUE, 4500.0, FALSE, TIMESTAMP '2026-05-03 18:07:20'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', TIMESTAMP '2026-05-03 18:05:00', 3, TRUE, 5200.0, TRUE, TIMESTAMP '2026-05-03 18:08:50'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', TIMESTAMP '2026-05-03 18:05:00', 4, FALSE, 8900.0, TRUE, TIMESTAMP '2026-05-03 18:10:20'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', TIMESTAMP '2026-05-03 18:05:00', 5, TRUE, 6100.0, FALSE, TIMESTAMP '2026-05-03 18:12:30'),
        ('student_nam', 'teacher_minh_data', 'Python Starter - Data Types and Control Flow', TIMESTAMP '2026-05-03 18:05:00', 6, TRUE, 6800.0, TRUE, TIMESTAMP '2026-05-03 18:15:20'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', TIMESTAMP '2026-05-03 18:30:00', 1, TRUE, 5600.0, TRUE, TIMESTAMP '2026-05-03 18:31:00'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', TIMESTAMP '2026-05-03 18:30:00', 2, FALSE, 9700.0, FALSE, TIMESTAMP '2026-05-03 18:32:50'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', TIMESTAMP '2026-05-03 18:30:00', 3, TRUE, 6500.0, TRUE, TIMESTAMP '2026-05-03 18:35:10'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', TIMESTAMP '2026-05-03 18:30:00', 4, TRUE, 7200.0, TRUE, TIMESTAMP '2026-05-03 18:37:25'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', TIMESTAMP '2026-05-03 18:30:00', 5, FALSE, 10300.0, FALSE, TIMESTAMP '2026-05-03 18:40:00'),
        ('student_quyen', 'teacher_minh_data', 'Java Backend - OOP and Exceptions', TIMESTAMP '2026-05-03 18:30:00', 6, TRUE, 8100.0, TRUE, TIMESTAMP '2026-05-03 18:43:20'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', TIMESTAMP '2026-05-02 20:50:00', 1, TRUE, 6700.0, TRUE, TIMESTAMP '2026-05-02 20:51:10'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', TIMESTAMP '2026-05-02 20:50:00', 2, FALSE, 11400.0, FALSE, TIMESTAMP '2026-05-02 20:53:00'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', TIMESTAMP '2026-05-02 20:50:00', 3, FALSE, 12600.0, TRUE, TIMESTAMP '2026-05-02 20:55:20'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', TIMESTAMP '2026-05-02 20:50:00', 4, TRUE, 8200.0, TRUE, TIMESTAMP '2026-05-02 20:58:00'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', TIMESTAMP '2026-05-02 20:50:00', 5, FALSE, 13100.0, FALSE, TIMESTAMP '2026-05-02 21:01:00'),
        ('student_an', 'teacher_minh_data', 'Cloud Computing - AWS Fundamentals', TIMESTAMP '2026-05-02 20:50:00', 6, TRUE, 9400.0, TRUE, TIMESTAMP '2026-05-02 21:05:30'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', TIMESTAMP '2026-05-02 21:00:00', 1, TRUE, 6100.0, TRUE, TIMESTAMP '2026-05-02 21:01:00'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', TIMESTAMP '2026-05-02 21:00:00', 2, TRUE, 6800.0, TRUE, TIMESTAMP '2026-05-02 21:02:40'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', TIMESTAMP '2026-05-02 21:00:00', 3, FALSE, 10300.0, FALSE, TIMESTAMP '2026-05-02 21:04:40'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', TIMESTAMP '2026-05-02 21:00:00', 4, TRUE, 7600.0, TRUE, TIMESTAMP '2026-05-02 21:07:00'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', TIMESTAMP '2026-05-02 21:00:00', 5, FALSE, 11100.0, FALSE, TIMESTAMP '2026-05-02 21:10:10'),
        ('student_bao', 'teacher_minh_data', 'DevOps Essentials - CI CD and Monitoring', TIMESTAMP '2026-05-02 21:00:00', 6, TRUE, 8400.0, TRUE, TIMESTAMP '2026-05-02 21:13:20'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', TIMESTAMP '2026-05-05 07:38:00', 1, TRUE, 4700.0, TRUE, TIMESTAMP '2026-05-05 07:39:00'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', TIMESTAMP '2026-05-05 07:38:00', 2, TRUE, 5400.0, FALSE, TIMESTAMP '2026-05-05 07:40:40'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', TIMESTAMP '2026-05-05 07:38:00', 3, TRUE, 6000.0, TRUE, TIMESTAMP '2026-05-05 07:42:20'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', TIMESTAMP '2026-05-05 07:38:00', 4, FALSE, 9800.0, TRUE, TIMESTAMP '2026-05-05 07:44:30'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', TIMESTAMP '2026-05-05 07:38:00', 5, TRUE, 6900.0, FALSE, TIMESTAMP '2026-05-05 07:47:00'),
        ('student_linh', 'teacher_minh_data', 'Cybersecurity Basics - Threats and Defense', TIMESTAMP '2026-05-05 07:38:00', 6, TRUE, 7200.0, TRUE, TIMESTAMP '2026-05-05 07:50:30'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', TIMESTAMP '2026-05-04 22:05:00', 1, TRUE, 7400.0, TRUE, TIMESTAMP '2026-05-04 22:06:00'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', TIMESTAMP '2026-05-04 22:05:00', 2, FALSE, 11900.0, FALSE, TIMESTAMP '2026-05-04 22:08:10'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', TIMESTAMP '2026-05-04 22:05:00', 3, FALSE, 12800.0, TRUE, TIMESTAMP '2026-05-04 22:10:40'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', TIMESTAMP '2026-05-04 22:05:00', 4, TRUE, 8300.0, TRUE, TIMESTAMP '2026-05-04 22:13:20'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', TIMESTAMP '2026-05-04 22:05:00', 5, FALSE, 12100.0, FALSE, TIMESTAMP '2026-05-04 22:17:00'),
        ('student_chi', 'teacher_minh_data', 'Product Management - Discovery and Roadmap', TIMESTAMP '2026-05-04 22:05:00', 6, TRUE, 9000.0, TRUE, TIMESTAMP '2026-05-04 22:21:40'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', TIMESTAMP '2026-05-03 19:30:00', 1, TRUE, 5800.0, TRUE, TIMESTAMP '2026-05-03 19:31:00'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', TIMESTAMP '2026-05-03 19:30:00', 2, TRUE, 6500.0, FALSE, TIMESTAMP '2026-05-03 19:32:45'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', TIMESTAMP '2026-05-03 19:30:00', 3, FALSE, 10600.0, TRUE, TIMESTAMP '2026-05-03 19:35:00'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', TIMESTAMP '2026-05-03 19:30:00', 4, TRUE, 7600.0, TRUE, TIMESTAMP '2026-05-03 19:37:30'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', TIMESTAMP '2026-05-03 19:30:00', 5, FALSE, 11400.0, FALSE, TIMESTAMP '2026-05-03 19:40:20'),
        ('student_quyen', 'teacher_minh_data', 'UX Research - Interviews and Usability', TIMESTAMP '2026-05-03 19:30:00', 6, TRUE, 8100.0, TRUE, TIMESTAMP '2026-05-03 19:44:10'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', TIMESTAMP '2026-05-03 19:40:00', 1, TRUE, 5000.0, TRUE, TIMESTAMP '2026-05-03 19:41:00'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', TIMESTAMP '2026-05-03 19:40:00', 2, TRUE, 5700.0, TRUE, TIMESTAMP '2026-05-03 19:42:20'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', TIMESTAMP '2026-05-03 19:40:00', 3, TRUE, 6200.0, FALSE, TIMESTAMP '2026-05-03 19:44:00'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', TIMESTAMP '2026-05-03 19:40:00', 4, FALSE, 9700.0, TRUE, TIMESTAMP '2026-05-03 19:46:20'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', TIMESTAMP '2026-05-03 19:40:00', 5, TRUE, 6900.0, FALSE, TIMESTAMP '2026-05-03 19:49:00'),
        ('student_nam', 'teacher_minh_data', 'Digital Marketing - Campaign Metrics', TIMESTAMP '2026-05-03 19:40:00', 6, TRUE, 7400.0, TRUE, TIMESTAMP '2026-05-03 19:52:30'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', TIMESTAMP '2026-05-02 21:50:00', 1, TRUE, 6200.0, TRUE, TIMESTAMP '2026-05-02 21:51:00'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', TIMESTAMP '2026-05-02 21:50:00', 2, FALSE, 10200.0, FALSE, TIMESTAMP '2026-05-02 21:52:50'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', TIMESTAMP '2026-05-02 21:50:00', 3, TRUE, 7100.0, TRUE, TIMESTAMP '2026-05-02 21:55:00'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', TIMESTAMP '2026-05-02 21:50:00', 4, TRUE, 7800.0, TRUE, TIMESTAMP '2026-05-02 21:57:20'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', TIMESTAMP '2026-05-02 21:50:00', 5, FALSE, 11600.0, FALSE, TIMESTAMP '2026-05-02 22:00:10'),
        ('student_bao', 'teacher_minh_data', 'Personal Finance - Budgeting and Investing', TIMESTAMP '2026-05-02 21:50:00', 6, TRUE, 8400.0, TRUE, TIMESTAMP '2026-05-02 22:03:30'),
        ('student_dung', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', TIMESTAMP '2026-05-03 23:30:00', 1, TRUE, 6800.0, TRUE, TIMESTAMP '2026-05-03 23:31:00'),
        ('student_dung', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', TIMESTAMP '2026-05-03 23:30:00', 2, TRUE, 7400.0, FALSE, TIMESTAMP '2026-05-03 23:33:10'),
        ('student_dung', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', TIMESTAMP '2026-05-03 23:30:00', 3, FALSE, 11900.0, TRUE, TIMESTAMP '2026-05-03 23:35:40'),
        ('student_dung', 'teacher_minh_data', 'Statistics Starter - Probability and Sampling', TIMESTAMP '2026-05-03 23:30:00', 4, TRUE, 8600.0, TRUE, TIMESTAMP '2026-05-03 23:38:00')
)
INSERT INTO tbl_study_session_detail (session_id, collection_item_id, is_correct, response_time_ms, answer_mode, created_at, updated_at)
SELECT
    session.id,
    item.id,
    ssd.is_correct,
    ssd.response_time_ms,
    ssd.answer_mode,
    ssd.answered_at,
    ssd.answered_at
FROM seed_session_details ssd
JOIN tbl_user learner ON learner.username = ssd.username
JOIN tbl_user owner_user ON owner_user.username = ssd.owner_username
JOIN tbl_collection collection ON collection.title = ssd.collection_title AND collection.user_id = owner_user.id
JOIN tbl_study_session session ON session.user_id = learner.id
    AND session.collection_id = collection.id
    AND session.started_at = ssd.session_started_at
JOIN tbl_collection_item item ON item.collection_id = collection.id
    AND item.order_index = ssd.item_order
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_study_session_detail existing
    WHERE existing.session_id = session.id
      AND existing.collection_item_id = item.id
);

-- Recalculate denormalized collection counts from inserted items.
UPDATE tbl_collection c
SET count = item_counts.item_count,
    updated_at = GREATEST(c.updated_at, TIMESTAMP '2026-05-04 18:00:00')
FROM (
    SELECT collection_id, COUNT(*)::INTEGER AS item_count
    FROM tbl_collection_item
    GROUP BY collection_id
) item_counts
WHERE c.id = item_counts.collection_id
  AND c.title IN (
      'IELTS Foundation May 2026 - Education',
      'IELTS Foundation May 2026 - Environment',
      'IELTS Foundation May 2026 - Technology',
      'IELTS Speaking Bank - People and Places',
      'Data Analytics Starter - SQL Basics',
      'Data Analytics Starter - Charts and Metrics',
      'TOEIC Workplace Essentials - Meetings',
      'TOEIC Workplace Essentials - Emails',
      'Business English - Negotiation Basics',
      'Travel English - Airport and Hotel',
      'Health English - Clinic Conversations',
      'High School Biology - Cells and Genetics',
      'High School Physics - Motion and Forces',
      'Geography English - Cities and Transport',
      'Python Starter - Data Types and Control Flow',
      'Java Backend - OOP and Exceptions',
      'Cloud Computing - AWS Fundamentals',
      'DevOps Essentials - CI CD and Monitoring',
      'Cybersecurity Basics - Threats and Defense',
      'Product Management - Discovery and Roadmap',
      'UX Research - Interviews and Usability',
      'Digital Marketing - Campaign Metrics',
      'Personal Finance - Budgeting and Investing',
      'Statistics Starter - Probability and Sampling'
  );
