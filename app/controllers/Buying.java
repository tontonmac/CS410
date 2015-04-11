package controllers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import models.FederatedBook;
import models.Department;
import models.Term;
import models.Course;
import models.UMBClass;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;
import scala.collection.JavaConversions;
import views.html.*;

public class Buying extends Controller {

    public static Result buy() {
    	List<Term> terms = Term.findAll();
    	List<Department> departments = Department.findAll();
    	List<Course> courses = Course.findAll();
    	List<String> sections = UMBClass.findUniqueSections();

        return ok(buy.render(terms, departments, courses, sections));
    }

    public static Result searchByCourse() {
    	DynamicForm requestData = Form.form().bindFromRequest();
        String termId = requestData.get("term");
        String departmentId = requestData.get("department");
        String courseId = requestData.get("course");
        String section = requestData.get("section");
        
        UMBClass c = UMBClass.findUnique(Term.findById(Long.parseLong(termId)), Course.findById(Long.parseLong(courseId)), section);
        List<models.Book> books = c.books;
        
        String courseName = Course.findById(Long.parseLong(courseId)).name;
        
        List<String> isbns = new ArrayList<String>();
        
        StringBuilder sb = new StringBuilder();
        for (models.Book book : books) {
        	isbns.add(book.isbn);
        }
        
        return listBooks(JavaConversions.asScalaBuffer(isbns).toList(), courseName, "");
    }
    
    public static Result searchByIsbn(String isbn) {
    	return listBooks(JavaConversions.asScalaBuffer(Collections.singletonList(isbn)).toList(), "", isbn);
    }
    
    public static Result listBooks(scala.collection.immutable.List<String> isbnParam, String courseNumber, String isbn) {
        ArrayList<FederatedBook> fBooks = new ArrayList<>();

        scala.collection.Iterator<String> iterator = isbnParam.iterator();
        
        while(iterator.hasNext()) {
        	fBooks.add(new FederatedBook(iterator.next()));
        }

        return ok(
                buyListBooks.render(fBooks, courseNumber, isbn)
        );

    }

}