package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class Book extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    public String author;
    public String isbn;
    public String publisher;
    public String image_path;
    public String edition;
    public Date copyright_date;

    public static Finder<Long,Book> find = new Finder<Long,Book>(Long.class, Book.class);
}


