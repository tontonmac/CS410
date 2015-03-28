package util.importer;

import play.db.ebean.*;
import models.*;

public class Resource<T extends Model> {
    private Department dept;
    private Term term;
    private Course course;
    private UMBClass umbClass;
    private String url;

    private T model;

    public Resource(T model, String url, Department dept, Term term, Course course, UMBClass umbClass) {
        this.model = model;
        this.url = url;
        this.dept = dept;
        this.term = term;
        this.course = course;
        this.umbClass = umbClass;
    }

    public T getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }

    public Term getTerm() {
        return term;
    }

    public Course getCourse() {
        return course;
    }

    public UMBClass getUMBClass() {
        return umbClass;
    }
    public Department getDepartment() {
        return dept;
    }

    public String toString() {
        String str = "";
        if (getModel() != null) {
            str += getModel().toString();
        }

        if (getUrl() != null) {
            str += " (" + getUrl() + ")";
        }

        return str;
    }

    public boolean isImportable() {
        return importer() != null;
    }

    // importer factory - returns an instance of an Importer class that can
    // import the url that this resource references
    public Base importer() {
        if (getUrl() == null || getModel() == null) {
            return null;
        } else if (getModel() instanceof Department) {
            return new CourseImporter(this);
        } else if (getModel() instanceof Course) {
            return new UMBClassImporter(this);
        } else if (getModel() instanceof UMBClass) {
            return null;
        } else {
            throw new RuntimeException("No importer exists for " +
                    getModel().getClass().toString() );
        }
    }
}
