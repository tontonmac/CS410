package controllers;

import play.mvc.*;
import views.html.*;
import models.Book;
import models.Listing;
import models.User;
import java.util.List;
public class BookController extends Controller {

	@Security.Authenticated(Secured.class)
    public static Result newBook() {
        return ok(newBook.render());
    }

	@Security.Authenticated(Secured.class)
	    public static Result editBook(Long id) {
			String userid=session("userid");

			Listing editList=Listing.editListing(userid,id);
			if(editList!=null)
			{
	        return ok(editBook.render(editList));
	        }
			else
			{
				return ok(index.render(null));
			}
	}

	@Security.Authenticated(Secured.class)
    public static Result showBook() {
		Long bookId = 0L;
		
        Book book = Book.findById(bookId);
       	return ok( showBook.render(book) );
        
    }

	/*@Security.Authenticated(Secured.class)
    public static Result deleteBook() {
		//Listing.createUpdate(Book.class, "delete from book_listing")
		//.execute();
    	// return ok(listings.render());
        return ok("TODO");
    }*/

@Security.Authenticated(Secured.class)
	public static Result deleteBook(Long id) {

	     if(id!=null)
	     {
	    	String userid=session("userid");
	    	boolean flag= Listing.deleteListing(userid,id);
	    	if(flag)
	    	{
	    	//List<Listing> newListing=Listing.editListing(userid,id);
	    		System.out.println("wowww");
	    		List<Listing> afterDelete=Listing.findListingsBySeller(userid);
	    	return ok(listings.render(afterDelete));
	    	}
	    	else
	    	{
	    		return ok(index.render(null));
	    	}
	     }
	     return ok(index.render(null));

	}



}
