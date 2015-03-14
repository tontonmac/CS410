package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity 
public class Department extends Model {

    @Id
    public Integer id;

    @Constraints.Required
    @Column(name = "full_name")
    public String fullName;

    @Constraints.Required
    @Column(name = "short_name")
    public String shortName;
    
    @OneToMany
    public List<Course> courses;

    public Department(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public static Department findOrCreate(String shortName, String fullName) {
        Department dept = findUnique(shortName);

        if (dept == null) {
            dept = new Department(fullName, shortName);
            dept.save();
        }

        return dept;
    }

    public String toString() {
        return this.fullName + "[" + this.shortName + "]";
    }

    public static Finder<Long,Department> find = new Finder<Long,Department>(Long.class, Department.class);

    public static Department findUnique(String short_name) {
        return find.where().eq("short_name", short_name).findUnique();
    }
}
