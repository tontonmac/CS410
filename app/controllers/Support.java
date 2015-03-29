package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Support extends Controller {

    public static Result support() {
        return ok(support.render());
    }

}