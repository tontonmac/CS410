package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class Course extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String number;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @Constraints.Required
    public Department department;

    @OneToMany
    public List<UMBClass> umbClasses;

    public static Finder<Long,Course> find = new Finder<Long,Course>(Long.class, Course.class);

    public Course(Department dept, String name, String number) {
        this.name = name;
        this.number = number;
        this.department = dept;
    }

    public static Course findOrCreate(Department dept, String name, String number) {
        Course course = findUnique(dept, number);

        if (course == null) {
            course = new Course(dept, name, number);
            course.save();
        }

        return course;
    }

    public static Course findUnique(Department dept, String number) {
        return find.where().eq("number", number).eq("department_id", dept.id).findUnique();
    }
    
    public static Course findById(Long id) {
    	return find.byId(id);
    }

    public static List<Course> findAll() {
    	return find.all();
    }
    
    public String toString() {
        return this.number + ": " + this.name;
    }
}
