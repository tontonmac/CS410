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
    public String edition;

    @Column(name = "image_path")
    public String imagePath;
    @Column(name = "copyright_date")
    public java.sql.Date copyrightDate;
    @Column(name = "buy_new_price")
    public Double buyNewPrice;
    @Column(name = "buy_used_price")
    public Double buyUsedPrice;
    @Column(name = "rent_new_price")
    public Double rentNewPrice;
    @Column(name = "rent_used_price")
    public Double rentUsedPrice;

    public Book(String title, String author, String isbn, Date copyrightDate, String publisher, String edition) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.edition = edition;

        if (copyrightDate != null) {
            this.copyrightDate = new java.sql.Date(copyrightDate.getTime());
        }
    }

    public String bookstoreUrl() {
        return "http://www.bkstr.com/webapp/wcs/stores/servlet/NavigationSearch?" +
                "searchTerm=" + this.isbn + "&storeId=10348&resultCatEntryType=2&" +
                "showResultsPage=true";
    }

    public String getCopyrightYear() {
        if (copyrightDate == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(copyrightDate);
            return Integer.toString(cal.get(Calendar.YEAR));
        }
    }

    public static Book findOrCreate(String title, String author, String isbn, java.sql.Date copyrightDate, String publisher, String edition) {
        Book book;

        if (isbn == null || isbn == "") {
            book = findUnique(title, author, publisher, copyrightDate);
        } else {
            book = findUnique(isbn);
        }

        if (book == null) {
            book = new Book(title, author, isbn, copyrightDate, publisher, edition);
        } else {
            book.title = title;
            book.author = author;
            book.isbn = isbn;
            book.copyrightDate = copyrightDate;
            book.publisher = publisher;
            book.edition = edition;
        }

        book.save();
        return book;
    }

    public void resetPrices() {
        this.buyNewPrice = 0.0;
        this.buyUsedPrice = 0.0;
        this.rentNewPrice = 0.0;
        this.rentUsedPrice = 0.0;
    }

    public static Book findUnique(String title, String author, String publisher, java.sql.Date copyrightDate) {
        return find.where().
            eq("title", title).
            eq("author", author).
            eq("publisher", publisher).
            eq("copyright_date", copyrightDate).
            findUnique();
    }

    public static Book findUnique(String isbn) {
        return find.where().eq("isbn", isbn).findUnique();
    }

    public static Finder<Long,Book> find = new Finder<Long,Book>(Long.class, Book.class);
}


