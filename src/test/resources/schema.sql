DROP TABLE usms_user IF EXISTS;
DROP TABLE store IF EXISTS;

CREATE TABLE usms_user (
       id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(255) NOT NULL,
       password VARCHAR(255),
       person_name VARCHAR(255),
       phone_number VARCHAR(255) NOT NULL UNIQUE,
       email VARCHAR(255) NOT NULL,
       security_state VARCHAR(255) DEFAULT 'BASIC',
       is_lock BOOLEAN DEFAULT false,
       role VARCHAR(255) DEFAULT 'ROLE_STORE_OWNER'
);

CREATE TABLE store (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_name VARCHAR(50) NOT NULL,
    store_address VARCHAR(50) NOT NULL,
    business_license_code VARCHAR(50)  NOT NULL,
    business_license_img_id VARCHAR(50) NOT NULL,
    store_state SMALLINT NOT NULL,
    admin_comment VARCHAR(50)
);