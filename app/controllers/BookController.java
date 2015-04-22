package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import models.Book;
import models.Listing;
import models.User;

import java.util.List;
public class BookController extends Controller {

	@Security.Authenticated(Secured.class)
    public static Result newBook() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String title = requestData.get("title");
     	String isbn = requestData.get("isbn");
     	String condition = requestData.get("condition");
     	String author = requestData.get("author");
     	String publisher = requestData.get("publisher");
     	String edition = requestData.get("edition");
     	String description = requestData.get("description");
     	String str_price = requestData.get("price");
     	Double d_price=Double.valueOf(str_price);
    	
    Listing.createOrEdit(null,description, d_price, title, author, isbn, publisher, edition);
    	
       // return redirect(
        //    routes.Application.index()
       // );
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
    public static Result showBook(Long bookId) {
		
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

//@Security.Authenticated(Secured.class)
//	public static Result sendEmail(Long id) {
//		List<Listing> sellerId = Listing.findSellerById(id);
//		return ok(sendEmail.render(sellerId));
//	}
}
