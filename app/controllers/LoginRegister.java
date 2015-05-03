package controllers;

import java.lang.reflect.InvocationTargetException;

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
	    	User user = User.authenticate(email, password);
	        if (user == null) {
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
    	try {
	    		Form<Login> form = Form.form(Login.class).bindFromRequest();
	
	    		if (form.hasErrors()) {
	            return badRequest(login.render(form));
	        } else {
	            session().clear();
	            String email = form.get().email;
	            session("email", email);
	            session("userid", Long.toString(User.findUserByEmail(email).id));
	            return redirect(
	                routes.Application.index()
	            );
	        }
    	} catch (Exception e) {
    		flash("error", "Invalid username or password");
    		return redirect(
    				routes.LoginRegister.login()
    		);
    	}
    }
    
    public static Result register() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	String firstName = requestData.get("firstName");
    	String lastName = requestData.get("lastName");
    	String password = requestData.get("password");
    	String email = requestData.get("email");
    	if (User.findUserByEmail(email) == null) {
    		User.createUser(firstName, lastName, password, email);
    		
    		session("email", email);
            session("userid", Long.toString(User.findUserByEmail(email).id));
            flash("success", "Account created successfully");
            return redirect(
                routes.Application.index()
            );
    	} else {
    		flash("error", "User with this email address already exists!");
    		return redirect(
    				routes.LoginRegister.login()
    		);
    	}
    	
    }
    
}
