-- See https://www.playframework.com/documentation/2.0/Evolutions for details on
-- how database migrations (aka evolutions) work in the play framework

-- To generate a schema diagram for this:
-- java -jar schemaSpy_5.0.0.jar -t mysql -db textbook_finder -host localhost -u root -dp mysql-connector-java-5.1.34-bin.jar -o ~/db_output/

# --- !Ups

CREATE TABLE department (
    id int NOT NULL AUTO_INCREMENT,
    full_name varchar(128) NOT NULL,
    short_name varchar(16) NOT NULL,
    UNIQUE (full_name),
    UNIQUE (short_name),
    PRIMARY KEY (id)
);

CREATE TABLE term (
    id int NOT NULL AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    url varchar(256),
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE course (
    id int NOT NULL AUTO_INCREMENT,
    department_id int,
    name varchar(255) NOT NULL,
    number varchar(8) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    UNIQUE (department_id, number),
    INDEX (department_id)
);

CREATE TABLE class (
    id int NOT NULL AUTO_INCREMENT,
    term_id int NOT NULL,
    course_id int NOT NULL,
    section_number varchar(8) NOT NULL,
    instructor_name varchar(128),
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (term_id) REFERENCES term(id),
    INDEX (course_id),
    UNIQUE(term_id,course_id,section_number)
);

CREATE TABLE user (
    id int NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(64) NOT NULL,
    first_name varchar(64),
    last_name varchar(64),
    department_id int,
    photo_url varchar(1024),
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    UNIQUE(email)
);

CREATE TABLE book_condition (
    id int NOT NULL AUTO_INCREMENT,
    name varchar(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(name)
);

CREATE TABLE book_listing (
    id int NOT NULL AUTO_INCREMENT,
    listed_by_id int,
    department_id int,
    description varchar(5120),
    price decimal(8,2),
    title varchar(1024),
    author varchar(512),
    isbn varchar(24),
    copyright_date date,
    publisher varchar(512),
    image_path varchar(256),
    edition varchar(16),
    num_views int,
    book_condition_id int,
    PRIMARY KEY (id),
    FOREIGN KEY (listed_by_id) REFERENCES user(id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    FOREIGN KEY (book_condition_id) REFERENCES book_condition(id),
    INDEX (listed_by_id),
    INDEX (isbn)
);

CREATE TABLE book (
    id int NOT NULL AUTO_INCREMENT,
    title varchar(512) NOT NULL,
    author varchar(512),
    isbn varchar(24),
    copyright_date date,
    publisher varchar(512),
    image_path varchar(256),
    edition varchar(16),
    buy_new_price decimal(8,2),
    buy_used_price decimal(8,2),
    rent_new_price decimal(8,2),
    rent_used_price decimal(8,2),
    PRIMARY KEY (id),
    UNIQUE (isbn)
);
 
CREATE TABLE required_book (
    id int NOT NULL AUTO_INCREMENT,
    class_id int NOT NULL,
    book_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (class_id) REFERENCES class(id),
    UNIQUE (class_id,book_id)
);

# --- !Downs
 
DROP TABLE required_book;
DROP TABLE book;
DROP TABLE book_listing;
DROP TABLE book_condition;
DROP TABLE user;
DROP TABLE class;
DROP TABLE course;
DROP TABLE term;
DROP TABLE department;

