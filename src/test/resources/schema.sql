DROP TABLE usms_user IF EXISTS;
DROP TABLE store IF EXISTS;
DROP TABLE cctv IF EXISTS;
DROP INDEX usms_store_business_license_img_id_idx IF EXISTS;
DROP INDEX usms_cctv_stream_key_idx IF EXISTS;

CREATE TABLE usms_user (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(255) NOT NULL,
       password VARCHAR(255),
       person_name VARCHAR(255),
       phone_number VARCHAR(255) NOT NULL UNIQUE,
       email VARCHAR(255) NOT NULL,
       security_state SMALLINT DEFAULT 0,
       is_lock BOOLEAN DEFAULT false,
       role SMALLINT DEFAULT 1
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

CREATE TABLE cctv (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    cctv_name VARCHAR(50) NOT NULL,
    cctv_stream_key VARCHAR(50) NOT NULL,
    is_expired BOOLEAN  DEFAULT false
);

CREATE UNIQUE INDEX usms_store_business_license_img_id_idx ON store (business_license_img_id);
CREATE UNIQUE INDEX usms_cctv_stream_key_idx ON cctv (cctv_stream_key);