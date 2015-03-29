package controllers;

import java.util.List;

import models.Department;
import models.Term;
import play.*;
import play.mvc.*;
import views.html.*;

public class Buying extends Controller {

    public static Result buy() {
    	List<Term> terms = Term.findAll();
    	List<Department> departments = Department.findAll();
    	
        return ok(buy.render(terms, departments));
    }

}