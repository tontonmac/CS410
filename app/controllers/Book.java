package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Book extends Controller {

    public static Result newBook() {
        return ok(newBook.render());
    }

    public static Result editBook() {
        return ok(editBook.render());
    }

    public static Result showBook() {
        return ok(showBook.render());
    }
    
    public static Result deleteBook() {
    	 return ok(listings.render());
    }

}
