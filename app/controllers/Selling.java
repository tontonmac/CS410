package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.mvc.Http.Request;
import views.html.*;
import models.Course;
import models.Listing;
import models.UMBClass;

public class Selling extends Controller {




    @Security.Authenticated(Secured.class)
	public static Result listings() {
    	String listedBy=session().get("userid");
    	List<Listing> listings12 = Listing.findListingsBySeller(listedBy);
        return ok(listings.render(listings12));
    }
}
