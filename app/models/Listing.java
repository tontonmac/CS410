package models;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name="book_listing")
public class Listing extends Model {
	
    @Id
    public Long id;
    
    @OneToOne
    @JoinColumn(name = "listed_by_id", referencedColumnName = "id")
    public User listedBy;
    
    @OneToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    public Department department;
    
    public String description;
    public Double price;
    public String title;
    public String author;
    public String isbn;
    public Date copyright_date;
    public String publisher;
    public String image_path;
    public String edition;
    public Integer num_views;
    
    @OneToOne
    @JoinColumn(name = "book_condition_id", referencedColumnName = "id")
    public Condition condition;    
    
    public static Finder<Long,Listing> find = new Finder<Long,Listing>(Long.class, Listing.class);
    
}