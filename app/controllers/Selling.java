package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Selling extends Controller {

    public static Result listings() {
        return ok(listings.render("Your new application is ready."));
    }

}
