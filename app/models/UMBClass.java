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

    @OneToOne
    @JoinColumn(name = "term_id", referencedColumnName = "id")
    public Term term;
    
    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    public Course course;
    
    @Constraints.Required
    public String section_number;

    public String instructor_name;

    @JoinTable(name="required_book")
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Book> books;

    public static Finder<Long,UMBClass> find = new Finder<Long,UMBClass>(Long.class, UMBClass.class);
}