package controllers;

import models.User;
import play.*;
import play.data.*;
import play.mvc.*;
import views.html.*;

public class LoginRegister extends Controller {

	public static class Login {

	    public String email;
	    public String password;

	    public String validate() {
	        if (User.authenticate(email, password) == null) {
	          return "Invalid user or password";
	        }
	        return null;
	    }
	}

    public static Result login() {
    	Form<Login> form = Form.form(Login.class);
        return ok(
            login.render(form)
        );
    }
    
    public static Result authenticate() {
    	Form<Login> form = Form.form(Login.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(login.render(form));
        } else {
            session().clear();
            session("email", form.get().email);
            return redirect(
                routes.Application.index()
            );
        }
    }
}
