package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.LoginRegister.login()
        );
    }
}
