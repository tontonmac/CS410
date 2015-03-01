package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
@Table(name="class")
public class UMBClass extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public Long term_id;

    @Constraints.Required
    public Long course_id;

    @Constraints.Required
    public String section_number;

    public String instructor_name;

    @OneToOne
    public Course course;

    @JoinTable(name="required_book")
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Book> books;

    public static Finder<Long,UMBClass> find = new Finder<Long,UMBClass>(Long.class, UMBClass.class);
}