package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;

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
    @Column(name = "section_number")
    public String sectionNumber;

    @Column(name = "instructor_name")
    public String instructorName;

    @JoinTable(name = "required_book")
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Book> books;

    public UMBClass(Term term, Course course, String section, String instructor) {
        this.term = term;
        this.course = course;
        this.sectionNumber = section;
        this.instructorName = instructor;
    }

    public static Finder<Long, UMBClass> find = new Finder<Long, UMBClass>(Long.class, UMBClass.class);

    // TODO: can we use JPA for this has-one :through type association?
    public Department department() {
        return course.department;
    }

    public static UMBClass findOrCreate(Term term, Course course, String section, String instructor) {
        UMBClass umbClass = findUnique(term, course, section);

        if (umbClass == null) {
            umbClass = new UMBClass(term, course, section, instructor);
            umbClass.save();
        } else {
            umbClass.instructorName = instructor;
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

    public static List<String> findUniqueSections() {
        List<SqlRow> rows = Ebean.createSqlQuery("select section_number from class group by section_number").findList();

        List<String> sectionNumbers = new ArrayList<String>();

        for (SqlRow row : rows) {
            sectionNumbers.add(row.getString("section_number"));
        }

        return sectionNumbers;
    }

    // overwrite default equals implementation to compare objects by ID instead
    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        } else if (this.id != null && ((UMBClass)other).id != null) {
            return this.id == ((UMBClass)other).id;
        }

        return false;
    }
    
    public String toString() {
        String str = "";

        if (sectionNumber != null) {
            str += sectionNumber;
        }

        if (instructorName != null) {
            str += "(" + instructorName + ")";
        }

        return str;
    }
}
