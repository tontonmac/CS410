package models;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.ebean.Model;


import play.db.ebean.Model.Finder;

@Entity
@Table(name="book_listing")
public class Listing extends Model {

    @Id
    public Long id;

    @ManyToOne
    @JoinColumn(name = "listed_by_id", referencedColumnName = "id")
    public User listedBy;

    @ManyToOne
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

    public String conditionName() {
        return condition.name;
    }


public static Finder<Long,Listing> find12 = new Finder<Long,Listing>(Long.class, Listing.class);
    public static List<Listing> findListingsBySellerAndBookId(String listedBy) {

        return find12.where().eq("listed_by_id", listedBy).findList();
    }

    public static Listing editListing(String userid,Long id) {

		   List<Listing> listing=findListingsBySellerAndBookId(userid);
		   for(int i=0;i<listing.size();i++)
		   {
			   Listing list=listing.get(i);
			   //System.out.println(list.id.equals(id));
			   if(list.id.equals(id))
			   {
			   return list;
			   }
			     //System.out.println(id);
			     //System.out.println(list.author);
			     //System.out.println(list.id);

		   }
		   return null;
	       // return find.where().eq("listed_by_id", listedBy).findList();
    }

    public static Finder<Long,Listing> find = new Finder<Long,Listing>(Long.class, Listing.class);

    public static List<Listing> findListingsBySeller(String listedBy) {
        return find.where().eq("listed_by_id", listedBy).findList();
    }

    public static List<Listing> findByIsbn(String isbn) {
        return find.where().eq("isbn", isbn).findList();
    }
}