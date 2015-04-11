package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Book extends Controller {

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
        return ok(showBook.render());
    }
    
	@Security.Authenticated(Secured.class)
    public static Result deleteBook() {
    	 return ok(listings.render());
    }

}
