DROP TABLE store IF EXISTS;

CREATE TABLE store (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_name VARCHAR(50) NOT NULL,
    store_address VARCHAR(50) NOT NULL,
    business_license_code VARCHAR(50)  NOT NULL,
    business_license_img_id VARCHAR(50) NOT NULL,
    store_state SMALLINT NOT NULL
);