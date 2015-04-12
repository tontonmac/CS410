package controllers;

import play.mvc.*;
import views.html.*;
import models.Book;

public class BookController extends Controller {

	@Security.Authenticated(Secured.class)
    public static Result newBook() {
        return ok(newBook.render());
    }

	@Security.Authenticated(Secured.class)
    public static Result editBook() {
        return ok(editBook.render());
    }

	@Security.Authenticated(Secured.class)
    public static Result showBook(Long bookId) {
		//DynamicForm requestData = Form.form().bindFromRequest();
		
		//String id = requestData.get("book_listing");
		//
		//List<Listing> sellerlisting = Listing.findListingsBySeller(id);
		//
		//List<Listing> list = Book.find(Listing.class)
		//			.join("user")
		//			.where()
		//			.gt("id", 0)
		//			.eq("book_listing", models.Listing.id)
		//			.findList();
		//	System.out.println(list);
        Book book = null;
		return ok( showBook.render(book) );
        //return ok(showBook(book).render() );
    }
    
	@Security.Authenticated(Secured.class)
    public static Result deleteBook() {
		//Listing.createUpdate(Book.class, "delete from book_listing")
		//.execute();
    	// return ok(listings.render());
        return ok("TODO");
    }

}
