package controllers;

import java.util.List;
import java.util.ArrayList;

import models.FederatedBook;

import models.Department;
import models.Term;
import play.mvc.*;
import views.html.*;

public class Buying extends Controller {

    public static Result buy() {
    	List<Term> terms = Term.findAll();
    	List<Department> departments = Department.findAll();
    	
        return ok(buy.render(terms, departments));
    }

    public static Result listBooks() {
        // todo: replace this static isbn with a real one
        ArrayList<String> isbns = new ArrayList<>();
        isbns.add("9780393123678");

        ArrayList<FederatedBook> fBooks = new ArrayList<>();

        for (String isbn : isbns) {
            fBooks.add( new FederatedBook(isbn) );
        }

        // todo: set the title string dynamically
        return ok(
                buyListBooks.render(fBooks, "Required books for 'CS-101'")
        );

    }

}