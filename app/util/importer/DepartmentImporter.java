package util.importer;

import java.io.IOException;
import java.util.ArrayList;
import models.Department;
import models.Term;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DepartmentImporter extends Base {
    private final static String courseRowSelector = "#content div.col > table tr";
    private String url;
    private Term term;

    public DepartmentImporter(Term term) {
        this.term = term;
        this.url = term.url;
    }

    public DepartmentImporter(Resource r) {
        this( r.getTerm() );
    }

    public ArrayList<Resource> performImport() throws IOException  {
        ArrayList<Resource> resources = new ArrayList<>();

        Document doc = getDocument(url);
        Elements courseRows = doc.select(courseRowSelector);

        for (Element courseRow : courseRows) {
            if (isHeaderRow(courseRow)) {
                continue;
            }

            Elements courseCols = courseRow.getElementsByTag("td");
            if (courseCols.size() == 2) {
                String shortName,  fullName, deptUrl;

                try {
                    shortName = courseCols.get(0).text();
                    fullName = courseCols.get(1).text();
                    deptUrl = courseCols.get(1).getElementsByTag("a").first().attr("href");
                } catch (Exception ex) {
                    throw new ImportException("Failed to parse course from "
                        + url + ". Error: " + ex.toString());
                }

                Resource r = buildResource(shortName, fullName, deptUrl);
                resources.add(r);
                log("imported department: " + r.toString());
            } else {
                throw new ImportException("Did not find two course columns on " + url);
            }
        }

        return resources;
    }

    private Resource<Department> buildResource(String shortDeptName, String fullDeptName, String deptUrl) {
        Department dept = Department.findOrCreate(shortDeptName, fullDeptName);
        return new Resource<Department>(dept, deptUrl, dept, term, null);
    }

}
