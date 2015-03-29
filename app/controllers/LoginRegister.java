package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class LoginRegister extends Controller {

    public static Result login() {
        return ok(login.render());
    }

}
