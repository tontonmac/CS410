package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Buying extends Controller {

    public static Result buy() {
        return ok(buy.render());
    }

}