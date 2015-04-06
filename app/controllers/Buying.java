package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import models.Course;
import java.util.ArrayList;

import models.FederatedBook;
import models.Department;
import models.Term;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import play.cache.Cache;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import views.html.*;
import net.fortuna.ical4j.model.*;

public class Buying extends Controller {
    public static List<Term> terms = Term.findAll();
    public static List<Department> departments = Department.findAll();

    public static Result buy() {
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

    public static Result schedule(){
        //todo: use the User model to grab the schedule or cache it?
        List<Course> cacheSchedule = (List<Course>) Cache.get("user.schedule");

        if(cacheSchedule.equals(null)){
            return redirect("/buy");
        }
        return ok(uploadSchedule.render(cacheSchedule));
    }

    public static Result uploadSchedule() {
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart schedule = body.getFile("uploadSchedule");
        List<Course> courseList = new ArrayList<Course>();
        if (schedule != null) {
            String fileName = schedule.getFilename();
            String contentType = schedule.getContentType();
            File file = schedule.getFile();
            String courseName = null;
            String courseNumber  = null;

            // todo: replace hardcoded CS with parsed dept info
            Department dept = new Department("Computer Science","CS");

            try {
                FileInputStream fis = new FileInputStream(file);
                CalendarBuilder builder = new CalendarBuilder();

                Calendar calendar = builder.build(fis);

                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                    Component component = (Component) i.next();

                    System.out.println("Component [" + component.getName() + "]");

                    for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                        Property property = (Property) j.next();
                        System.out.println("Property: " + property.getName());

                        // todo: parse our more specific substrings
                        if(property.getName().equals("DESCRIPTION")){
                            courseName = property.getValue();
                        }
                        if(property.getName().equals("SUMMARY")){
                            courseNumber = property.getValue();
                        }

                    }
                    if(courseName != null && courseNumber != null){
                        courseList.add(new Course(dept, courseName, courseNumber));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Cache.set("user.schedule",courseList);
            return ok(uploadSchedule.render(courseList));
        } else {
            // todo: need better error handling for wrong file extensions
            flash("error", "Missing file");
            return ok("something went wrong...");
        }
    }






}