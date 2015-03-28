use textbook_finder;

insert into department(full_name,short_name) values ("Computer Science", "CS");
insert into department(full_name,short_name) values ("Chemistry", "CHEM");

insert into course(id, department_id,name, number) values (1, 1, "Introduction to Computing", "101");
insert into course(id, department_id,name, number) values (2, 1, "Programming in C", "240");
insert into course(id, department_id,name, number) values (3, 1, "Applied Discrete Mathematics", "320L");
insert into course(id, department_id,name, number) values (4, 2, "Physiological Chemistry", "130");

insert into term(id, name, url) values (1, "Spring 2015", "http://www.umb.edu/academics/course_catalog/subjects/2015%20Spring");
insert into term(id, name, url) values (2, "Summer 2015", "http://www.umb.edu/academics/course_catalog/subjects/2015%20Summer");
insert into term(id, name, url) values (3, "Fall 2015", "http://www.umb.edu/academics/course_catalog/subjects/2015%20Fall");

insert into class(id,term_id,course_id,section_number,instructor_name) values (1, 1, 4, "01", "Dransfield,Timothy");
insert into class(id,term_id,course_id,section_number,instructor_name) values (2, 1, 4, "01D", "Dransfield,Timothy");
insert into class(id,term_id,course_id,section_number,instructor_name) values (3, 1, 1, "05", "Ghorbanzade,Pejman");
insert into class(id,term_id,course_id,section_number,instructor_name) values (4, 1, 2, "01", "Tran,Duc");
insert into class(id,term_id,course_id,section_number,instructor_name) values (5, 1, 2, "02", "Cheung,Ronald S");

insert into book(id,title,author,isbn,publisher) values
    (1, "C Programming Language","Kernighan", "007-6092003106", "Prentice Hall");
insert into book(id,title,author,isbn,publisher) values
    (2, "Intro to Programming in Java","Sedgewick", "978-0321498052", "Addison-Wesley");

insert into book(id,title,author,isbn,publisher, buy_new_price) values
    (3, "MasteringChemistry with Pearson eText -- Standalone Access Card -- for Chemistry: A Molecular Approach (3rd Edition)",
     "Nivaldo J. Tro",
     "978-0321806383",
     "Prentice Hall",
     10.50);

insert into required_book(class_id,book_id) values (1, 3);
insert into required_book(class_id,book_id) values (2, 3);
insert into required_book(class_id,book_id) values (3, 2);
insert into required_book(class_id,book_id) values (4, 1);
insert into required_book(class_id,book_id) values (5, 1);

