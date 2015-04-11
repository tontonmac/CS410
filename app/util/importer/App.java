package util.importer;

import java.io.IOException;
import java.util.ArrayList;
import models.Course;
import models.Department;
import models.Term;

public class App {
    private Term term;

    public App(String termStr) {
        term = Term.findUnique(termStr);

        if (term == null) {
            throw new RuntimeException("Term '" + termStr + "' does not exist");
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1 ) {
            showUsage();
            System.exit(1);
        }

        new App(args[0]).performImport();
    }

    public void performImport() throws IOException {
        ArrayList<Resource> toImport = new ArrayList<>();

        DepartmentImporter deptImporter = new DepartmentImporter(term);
        toImport.addAll( deptImporter.performImport() );

        while (toImport.isEmpty() == false) {
            Resource r = toImport.remove(0);

            if ( r.isImportable() ) {
                try {
                    toImport.addAll( r.importer().performImport() );
                } catch (Exception e) {
                    System.err.println("Failed to import resource from url: " + r.getUrl());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void showUsage() {
        System.out.println("Usage:");
        System.out.println("java App term_name");
        System.out.println("e.g. java App 'Spring 2015'");
    }


}
