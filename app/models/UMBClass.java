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

    public UMBClass(Term term, Course course, String section, String instructor) {
        this.term = term;
        this.course = course;
        this.section_number = section;
        this.instructor_name = instructor;
    }

    public static Finder<Long,UMBClass> find = new Finder<Long,UMBClass>(Long.class, UMBClass.class);

    public static UMBClass findOrCreate(Term term, Course course, String section, String instructor) {
        UMBClass umbClass = findUnique(term, course, section);

        if (umbClass == null) {
            umbClass = new UMBClass(term, course, section, instructor);
            umbClass.save();
        } else {
            umbClass.instructor_name = instructor;
            umbClass.save();
        }

        return umbClass;
    }

    public static UMBClass findUnique(Term term, Course course, String section) {
        return find.where().
            eq("term_id", term.id).
            eq("course_id", course.id).
            eq("section_number", section).
            findUnique();
    }

    public String toString() {
        String str = "";

        if (section_number != null) {
            str += section_number;
        }

        if (instructor_name != null) {
            str += "(" + instructor_name + ")";
        }

        return str;
    }
}