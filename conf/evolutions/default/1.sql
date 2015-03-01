-- See https://www.playframework.com/documentation/2.0/Evolutions for details on
-- how database migrations (aka evolutions) work in the play framework

-- To generate a schema diagram for this:
-- java -jar schemaSpy_5.0.0.jar -t mysql -db bookstore -host localhost -u root -dp mysql-connector-java-5.1.34-bin.jar -o ~/db_output/

# --- !Ups

CREATE TABLE departments (
    id int NOT NULL AUTO_INCREMENT,
    full_name varchar(128) NOT NULL,
    short_name varchar(16) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE terms (
    id int NOT NULL AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE courses (
    id int NOT NULL AUTO_INCREMENT,
    department_id int,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX (department_id)
);

CREATE TABLE classes (
    id int NOT NULL AUTO_INCREMENT,
    term_id int NOT NULL,
    course_id int NOT NULL,
    section_number varchar(8) NOT NULL,
    instructor_name varchar(128),
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (term_id) REFERENCES terms(id),
    INDEX (course_id),
    UNIQUE(term_id,course_id,section_number)
);

CREATE TABLE users (
    id int NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(64) NOT NULL,
    first_name varchar(64),
    last_name varchar(64),
    department_id int,
    photo_url varchar(1024),
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    UNIQUE(email)
);

CREATE TABLE conditions (
    id int NOT NULL AUTO_INCREMENT,
    name varchar(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(name)
);

CREATE TABLE book_listings (
    id int NOT NULL AUTO_INCREMENT,
    listed_by_id int,
    department_id int,
    description varchar(5120),
    price decimal(8,2),
    title varchar(512),
    author varchar(512),
    isbn bigint,
    copyright_date date,
    publisher varchar(512),
    image_path varchar(256),
    edition varchar(16),
    num_views int,
    condition_id int,
    PRIMARY KEY (id),
    FOREIGN KEY (listed_by_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (condition_id) REFERENCES conditions(id),
    INDEX (listed_by_id),
    INDEX (isbn)
);

CREATE TABLE books (
    id int NOT NULL AUTO_INCREMENT,
    title varchar(512),
    author varchar(512),
    isbn bigint,
    copyright_date date,
    publisher varchar(512),
    image_path varchar(256),
    edition varchar(16),
    PRIMARY KEY (id),
    UNIQUE (isbn)
);
 
CREATE TABLE required_books (
    id int NOT NULL AUTO_INCREMENT,
    class_id int NOT NULL,
    book_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    UNIQUE (class_id,book_id)
);

CREATE TABLE book_prices (
    id int NOT NULL AUTO_INCREMENT,
    book_id int NOT NULL,
    buy_new_price decimal(8,2),
    buy_used_price decimal(8,2),
    rent_price decimal(8,2),
    url varchar(1024),
    PRIMARY KEY (id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    UNIQUE (book_id)
);


# --- !Downs
 
DROP TABLE book_prices;
DROP TABLE required_books;
DROP TABLE books;
DROP TABLE book_listings;
DROP TABLE conditions;
DROP TABLE users;
DROP TABLE classes;
DROP TABLE courses;
DROP TABLE terms;
DROP TABLE departments;

