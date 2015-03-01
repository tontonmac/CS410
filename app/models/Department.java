package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity 
public class Department extends Model {

    @Id
    public Long id;
    
    @Constraints.Required
    public String full_name;

    @Constraints.Required
    public String short_name;
    
    @OneToMany
    public List<Course> courses;

    public Department(String full_name, String short_name) {
        this.full_name = full_name;
        this.short_name = short_name;
    }

    public static Finder<Long,Department> find = new Finder<Long,Department>(Long.class, Department.class);

    public static Department find_by_short_name(String short_name) {
        return find.where().eq("short_name", short_name).findUnique();
    }
}
