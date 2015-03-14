package util.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import models.Course;
import models.Department;
import models.Term;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CourseImporter extends Base {
    // the html on this page is invalid => h4 isn't valid child of ul
    private final static String COURSE_SELECTOR = "#content .colR ul > h4 a";

    private Department dept;
    private Term term;
    private String url;

    public CourseImporter(String url, Department dept, Term term) {
        this.dept = dept;
        this.url = url;
        this.term = term;
    }

    public CourseImporter(Resource r) {
        this( r.getUrl(), r.getDepartment(), r.getTerm() );
    }

    public ArrayList<Resource> performImport() throws IOException {
        // Department short names can have spaces in them (e.g. 'SPE G' =
        // special education).  Course numbers can have a letter in them
        // (e.g. 320L, 5V00), but always start with a number.
        Pattern pattern = Pattern.compile(
                "(?<deptShortName>.+?)\\s+(?<courseNum>\\d[\\d\\w]*)" +
                        RE_SPACE + "(?<courseName>.+)" );
        ArrayList<Resource> resources = new ArrayList<>();

        Document doc = getDocument(url);
        Elements courseRows = doc.select(COURSE_SELECTOR);

        for (Element courseRow : courseRows) {
            String courseFullText = courseRow.text();
            Matcher matcher = pattern.matcher(courseFullText);

            if (matcher.matches()) {
                Resource r = buildResource(
                    matcher.group("courseName"),
                    matcher.group("courseNum"),
                    courseRow.attr("href"));
                resources.add(r);
                log("imported course: " + r.toString());
            } else {
                throw new ImportException("Failed to parse course string: " +
                    courseFullText + ".  Source URL: " + url);
            }
        }

        return resources;
    }

    private Resource buildResource(String courseName, String courseNumber, String courseUrl) {
        Course course = Course.findOrCreate(dept, courseName, courseNumber);
        return new Resource<Course>(course, courseUrl, this.dept, this.term, course);
    }
}
