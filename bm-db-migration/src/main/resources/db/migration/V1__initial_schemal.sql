CREATE TABLE IF NOT EXISTS `employees` (
    `id` varchar(50) NOT NULL,
    `full_name` varchar(225) NOT NULL,
    `birth_date` DATE,
    `phone` varchar(50),
    `email` varchar(225) NOT NULL,
    `password` varchar(225) NOT NULL,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

-- pass: 123456
INSERT INTO employees(id, full_name, birth_date, email, password)
VALUES('f7887a85-1e83-55f1-86d0-b3574b7fe3eb', 'Admin', '2020-04-24', 'admin@vlxdapp.com', '$2a$09$DlFMW.ofkjNQXaNhPp7ug..kWu5i3jSacX7w1HcKdm9cd6xq4C8FC');


CREATE TABLE IF NOT EXISTS `settings` (
    `id` int NOT NULL AUTO_INCREMENT,
    `label` varchar(225),
    `key` varchar(225),
    `value` varchar(225),
    PRIMARY KEY (id)
);
