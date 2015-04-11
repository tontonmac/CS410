package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Selling extends Controller {

	@Security.Authenticated(Secured.class)
    public static Result listings() {
        return ok(listings.render());
    }

}
