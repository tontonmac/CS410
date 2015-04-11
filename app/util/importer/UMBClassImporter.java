package util.importer;

import java.util.ArrayList;
import java.io.IOException;

import models.Course;
import models.Department;
import models.Term;
import models.UMBClass;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UMBClassImporter extends Base {

    private static final String CLASS_ROW_SELECTOR = "#content div.colR > table tr";
    private static final Integer SECTION_COL = 0;
    private static final Integer INSTRUCTOR_COL = 4;

    private String url;
    private Term term;
    private Course course;
    private Department department;

    public UMBClassImporter(String url, Term term, Course course, Department dept) {
        this.url = url;
        this.term = term;
        this.course = course;
        this.department = dept;
    }

    public UMBClassImporter(Resource r) {
        this(r.getUrl(), r.getTerm(), r.getCourse(), r.getDepartment());
    }

    public ArrayList<Resource> performImport() throws IOException  {
        ArrayList<Resource> resources = new ArrayList<>();

        Document doc = getDocument(url);
        Elements rows = doc.select(CLASS_ROW_SELECTOR);

        for (Element row : rows) {
            if (isHeaderRow(row)) {
                if (!canParseTable(row)) {
                    throw new ImportException("Class page is in unsupported format. URL:" + url);
                }

                continue;
            }

            Elements cols = row.getElementsByTag("td");
            String section = cols.get(SECTION_COL).text();
            String instructor = cols.get(INSTRUCTOR_COL).text();

            Resource r = buildResource(section, instructor);
            resources.add(r);
            log("imported class: " + r.toString());
        }

        return resources;
    }

    private Resource<UMBClass> buildResource(String section, String instructor) {
        UMBClass umbClass = UMBClass.findOrCreate(term, course, section, instructor);
        return new Resource<UMBClass>(umbClass, null, department, term, course, umbClass);
    }

    // determine if the table is parseable based on a header row
    private boolean canParseTable(Element headerRow) {
        try {
            Elements cols = headerRow.getElementsByTag("th");
            return
                cols.get(SECTION_COL).text().contains("Section") &&
                cols.get(INSTRUCTOR_COL).text().contains("Instructor");
        } catch (Exception ex) {
            return false;
        }
    }
}
