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

    @OneToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @Constraints.Required
    public Department department;

    @OneToMany
    public List<UMBClass> umbClasses;

    public static Finder<Long,Course> find = new Finder<Long,Course>(Long.class, Course.class);
}
