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

    public void setUrl(String url) {
        this.url = url;
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
        try {
            return importer() != null;
        } catch (Exception ex) {
            return false;
        }
    }

    // importer factory - returns an instance of an Importer class that can
    // import the url that this resource references
    public Base importer() {
        if (getModel() == null) {
            return null;
        } else if (getModel() instanceof UMBClass) {
            // the book importer is a special case in that it determines
            // the URL itself
            return new BookImporter(getTerm(), this.getUMBClass());
        } else if (getUrl() == null) {
            return null;
        } else if (getModel() instanceof Department) {
            return new CourseImporter(this);
        } else if (getModel() instanceof Course) {
            return new UMBClassImporter(this);
        } else if (getModel() instanceof UMBClass) {
            return new BookImporter(getTerm(), this.getUMBClass());
        } else {
            throw new RuntimeException("No importer exists for " +
                    getModel().getClass().toString() );
        }
    }
}
