package controllers;

import java.util.Collections;
import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import models.*;
import models.Book;
import models.Course;
import models.UMBClass;
import net.fortuna.ical4j.data.CalendarBuilder;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Http.MultipartFormData;
import scala.collection.JavaConversions;
import views.html.*;
import net.fortuna.ical4j.model.*;
import java.lang.Integer;

public class Buying extends Controller {
    public static List<Term> terms = Term.findAll();
    public static List<Department> departments = Department.findAll();

	@Security.Authenticated(Secured.class)
    public static Result buy() {
    	List<Course> courses = Course.findAll();
    	List<String> sections = UMBClass.findUniqueSections();

        return ok(buy.render(terms, departments, courses, sections));
    }

	@Security.Authenticated(Secured.class)
    public static Result searchByCourse() {
    	DynamicForm requestData = Form.form().bindFromRequest();
        String termId = requestData.get("term");
        String departmentId = requestData.get("department");
        String courseId = requestData.get("course");
        String section = requestData.get("section");
        
        UMBClass c = UMBClass.findUnique(Term.findById(Long.parseLong(termId)), Course.findById(Long.parseLong(courseId)), section);
        List<models.Book> books = c.books;
            isbns.add(b.isbn.replace("-",""));
        }
        //isbns.add("9780393123678");
        
        String courseName = Course.findById(Long.parseLong(courseId)).name;
        
        List<String> isbns = new ArrayList<String>();
        
        StringBuilder sb = new StringBuilder();
        for (Book book : books) {
        	isbns.add(book.isbn);
        }
        
        return listBooks(JavaConversions.asScalaBuffer(isbns).toList(), courseName, "");
    }
	
	@Security.Authenticated(Secured.class)
    public static Result searchByIsbn(String isbn) {
    	return listBooks(JavaConversions.asScalaBuffer(Collections.singletonList(isbn)).toList(), "", isbn);
    }
    
	@Security.Authenticated(Secured.class)
    public static Result listBooks(scala.collection.immutable.List<String> isbnParam, String courseNumber, String isbn) {
        ArrayList<FederatedBook> fBooks = new ArrayList<>();

        scala.collection.Iterator<String> iterator = isbnParam.iterator();
        
        while(iterator.hasNext()) {
        	fBooks.add(new FederatedBook(iterator.next()));
        }
        String requiredText = "Required books for " + dept + " " + courseNum;

        return ok(
                buyListBooks.render(fBooks, courseNumber, isbn)
        );

    }
    
     public static Result listBooks(String term, String dept, String courseNum, Long classId, String section) {

        Term tempTerm = Term.findUnique(term);
        dept = dept.substring(dept.indexOf("[")+1,dept.indexOf("]"));
        Department tempDept = Department.findUnique(dept);
        Course tempCourse = Course.findUnique(tempDept,courseNum);
        UMBClass umbClass = UMBClass.findUnique(tempTerm, tempCourse, section);

        ArrayList<String> isbns = new ArrayList<>();
        for(Book b : umbClass.books){
            isbns.add(b.isbn.replace("-",""));
        }
        //isbns.add("9780393123678");

        ArrayList<FederatedBook> fBooks = new ArrayList<>();

        for (String i : isbns) {
            fBooks.add( new FederatedBook(i) );
        }
        String requiredText = "Required books for " + dept + " " + courseNum;

        return ok(
                buyListBooks.render(fBooks, requiredText)
        );

    }


    public static Result schedule(){
        //todo: use the User model to grab the schedule or cache it?
        HashMap<Course,UMBClass> courseAndClass = (HashMap<Course,UMBClass>) Cache.get("user.classes");

        if(courseAndClass.equals(null)){
            return redirect("/buy");
        }
        return ok(uploadSchedule.render(courseAndClass));
    }

    public static Result uploadSchedule() {
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart schedule = body.getFile("uploadSchedule");
        List<String> courseSummaryList = new ArrayList<>();
        List<Course> courseList = new ArrayList<>();
        List<UMBClass> classList = new ArrayList<>();
        String courseTerm = null;

        if (schedule != null) {
            File file = schedule.getFile();
            String courseSummary;

            try {
                FileInputStream fis = new FileInputStream(file);
                CalendarBuilder builder = new CalendarBuilder();

                Calendar calendar = builder.build(fis);

                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                    Component component = (Component) i.next();

                    System.out.println("Component [" + component.getName() + "]");

                    for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                        Property property = (Property) j.next();
                        System.out.println("Property: " + property.getName() + " Value: " + property.getValue());

                        if(property.getName().equals("SUMMARY")){
                            courseSummary = property.getValue();
                            courseSummaryList.add(courseSummary);
                        }
                        if(property.getName().equals("DTSTART")){
                            courseTerm = property.getValue();
                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Term currentTerm = findCurrentTerm(courseTerm);
            HashMap<Course,UMBClass> courseAndClassList = new HashMap<>();
            for(String summary : courseSummaryList) {
                Department parsedDept = parseDepartment(summary);
                String parsedCourseNum = parseCourseNumber(summary);
                Course course = Course.findUnique(parsedDept,parsedCourseNum);
                course.department = parsedDept;
                String courseSection = parseCourseSection(summary);
                UMBClass umbClass = UMBClass.findUnique(currentTerm, course, courseSection);
                umbClass.term = currentTerm;
                classList.add(umbClass);
                courseList.add(course);
                courseAndClassList.put(course,umbClass);
            }

            // Add test Chemistry course here
            Department testDept = Department.findUnique("CHEM");
            Course testCourse = Course.findUnique(testDept,"130");
            testCourse.department = testDept;
            UMBClass testClass = UMBClass.findUnique(currentTerm,testCourse,"01");
            testClass.term = currentTerm;
            courseAndClassList.put(testCourse,testClass);
            courseList.add(testCourse);

            // todo: replace the cache with the user session
            Cache.set("user.schedule",courseList);
            Cache.set("user.classes",courseAndClassList);
            return ok(uploadSchedule.render(courseAndClassList));
        } else {
            // todo: need better error handling for wrong file extensions
            flash("error", "Missing file");
            return ok("something went wrong...");
        }
    }

    public static Department parseDepartment(String summary){
        return Department.findUnique(summary.split(" ")[0]);
    }

    public static String parseCourseNumber(String summary){
        return summary.split(" ")[1].split("-")[0];
    }

    public static String parseCourseSection(String summary){
        return summary.split(" ")[1].split("-")[1];
    }

    public static Term findCurrentTerm(String parsedTerm){
        if(java.lang.Integer.parseInt(parsedTerm.substring(0, 6)) < 201505) {
            return Term.findUnique("Spring 2015");
        }
        if(java.lang.Integer.parseInt(parsedTerm.substring(0, 6)) < 201509){
            return Term.findUnique("Summer 2015");
        }
        return Term.findUnique("Fall 2015");
    }






}