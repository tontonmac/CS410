package util.importer;

import java.util.ArrayList;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import models.Book;
import models.UMBClass;
import models.Term;
import util.importer.Resource;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 There are a number of potential improvements that could be made here going forward:
   * include books that are recommended, but not required
   * include the digital book prices, where they exist
   * support the case where the course required one of a set of books (i.e. "pick from these two books").
     You can see an example of this at: http://www.bkstr.com/webapp/wcs/stores/servlet/booklookServlet%20?bookstore_id-1=530&term_id-1=2430&div-1=&dept-1=BIOL&course-1=208&section-1=02

  It'd also be super awesome to get the official list of required books directly from UMass, rather than going through the bookstore.  Perhaps we could request this somehow?
*/

public class BookImporter extends Base {
    private final static String bookstoreId = "530";
    private static final String REQUIRED_BOOKS_SELECTOR = "#material-group_REQUIRED > div > ul > li";
    private static final String RECOMMENDED_BOOKS_SELECTOR = "#material-group_RECOMMENDED > div > ul > li";
    private static final String BOOK_PRICES_SELECTOR = ".material-group-table table tr";
    private static final Integer BOOK_TYPE_COL = 1;
    private static final Integer ORDER_TYPE_COL = 2;
    private static final Integer ORDER_OPTION_COL = 3;
    private static final Integer PRICE_COL = 7;
    private static final String TYPE_BUY = "BUY";
    private static final String TYPE_RENT = "RENT";
    private static final String OPTION_USED = "USED";
    private static final String OPTION_NEW = "NEW";

    private static final Pattern EDITION_RE = Pattern.compile("^(?<edition>\\d+)[a-z]{2}$");
    private static final Pattern MONEY_RE = Pattern.compile("^\\$(?<dollars>\\d+)\\.(?<cents>\\d{2})");

    private static enum BookType {
        REQUIRED,
        RECOMMENDED
    };

    private UMBClass umbClass;
    private String url;

    public BookImporter(Resource r) {
        this( r.getTerm(), r.getUMBClass() );
    }

    public BookImporter(Term term, UMBClass umbClass) {
        this(umbClass, bookstoreUrl(term, umbClass) );
    }

    public BookImporter(UMBClass umbClass, String url) {
        this.url = url;
        this.umbClass = umbClass;
    }


    public static String bookstoreUrl(Term term, UMBClass umbClass) {
        if (umbClass.department() == null) {
            throw new RuntimeException("Unable to import resource - department doesn't exist");
        }

        return "http://www.bkstr.com/webapp/wcs/stores/servlet/booklookServlet%20?" +
                "bookstore_id-1=" + bookstoreId + "&term_id-1=" + term.bookstoreId() + "&" +
                "div-1=&dept-1=" + umbClass.department().shortName + "&" +
                "course-1=" + umbClass.course.number + "&section-1=" + umbClass.sectionNumber;
    }

    // import all required and recommended books for a class, along with their
    // prices (used, new, etc)
    public ArrayList<Resource> performImport() throws IOException {
        ArrayList<Resource> resources = new ArrayList<>();

        Document doc = getDocument(url);

        resources.addAll(
                importBooks(doc.select(REQUIRED_BOOKS_SELECTOR), BookType.REQUIRED) );
        resources.addAll(
                importBooks(doc.select(RECOMMENDED_BOOKS_SELECTOR), BookType.RECOMMENDED) );

        return resources;
    }

    private ArrayList<Resource<Book>> importBooks(Elements bookElements, BookType bookType) throws IOException {
        ArrayList<Resource<Book>> resources = new ArrayList<>();

        // for now we'll just ignore recommended books.  We may wish to revisit this in the future
        if (bookType == BookType.RECOMMENDED) {
            return resources;
        }

        for (Element row : bookElements) {
            // import the book
            Resource<Book> r = buildResource(
                    parseTitle(row), parseAuthor(row), parseISBN(row),
                    parseCopyrightYear(row), parsePublisher(row), parseEdition(row) );
            resources.add(r);

            // replace all UMB prices for the book with the data from this page
            importBookPriceData( row, r.getModel() );

            // add this to the required books for our course
            if (!umbClass.books.contains(r.getModel())) {
                umbClass.books.add(r.getModel());
                umbClass.save();
            }
        }

        return resources;
    }

    // replace all UMB prices for the book with the data from this page
    private void importBookPriceData(Element parentElement, Book book) {
        // delete all price data to ensure no stale data is persisted
        book.resetPrices();

        Elements priceRows = parentElement.select(BOOK_PRICES_SELECTOR);
        for (Element priceRow : priceRows) {
            if (isHeaderRow(priceRow)) {
                if (!canParsePriceTable(priceRow)) {
                    throw new ImportException("Book page is in unsupported format. URL:" + url);
                }

                continue;
            }

            Elements cols = priceRow.getElementsByTag("td");
            String bookType = cols.get(BOOK_TYPE_COL).text();

            String orderOption = cols.get(ORDER_OPTION_COL).text();
            String orderType = cols.get(ORDER_TYPE_COL).text();
            Double price = moneyToDouble(cols.get(PRICE_COL).text());

            // some books have prices for both digital and paper formats.  We simply
            // take the first price we see here (which _should_ correspond to a paper format).
            // We may wish to refine this further in the future
            if (orderType.contains(TYPE_BUY)) {
                if (orderOption.contains(OPTION_USED) && book.buyUsedPrice == 0) {
                    book.buyUsedPrice = price;
                } else if (orderOption.contains(OPTION_NEW) && book.buyNewPrice == 0) {
                    book.buyNewPrice = price;
                } 
            } else if (orderType.contains(TYPE_RENT)) {
                if (orderOption.contains(OPTION_USED) && book.rentUsedPrice == 0) {
                    book.rentUsedPrice = price;
                } else if (orderOption.contains(OPTION_NEW) && book.rentNewPrice == 0) {
                    book.rentNewPrice = price;
                } 
            } else {
                System.out.println("skipping unrecognized order type of " + orderType);
            }
        }

        book.save();
    }

    // convert a money string, i.e. $50.20, to a double
    private double moneyToDouble(String money) {
        Matcher matcher = MONEY_RE.matcher(money);
        if (matcher.matches()) {
            return Double.parseDouble(matcher.group("dollars") + "." + matcher.group("cents"));
        } else {
            return 0;
        }
    }

    private Resource<Book> buildResource(String title, String author, String isbn, Date copyrightYear, String publisher, String edition) {
        java.sql.Date sqlDate = null;

        if (copyrightYear != null) {
            sqlDate = new java.sql.Date(copyrightYear.getTime());
        } 

        Book book = Book.findOrCreate(title, author, isbn, sqlDate, publisher, edition);
        return new Resource<Book>(book, null, null, null, null, umbClass);
    }

    private String parseTitle(Element parentElement) {
        return getStringOfDecoratedElement(parentElement, "title", "h3.material-group-title");
    }

    private String parseAuthor(Element parentElement) {
        return getStringOfDecoratedElement(parentElement, "author", "#materialAuthor");
    }

    private String parseISBN(Element parentElement) {
        return getStringOfDecoratedElement(parentElement, "copyright year", "#materialISBN");
    }

    private String parsePublisher(Element parentElement) {
        return getStringOfDecoratedElement(parentElement, "publisher", "#materialPublisher");
    }

    private String parseEdition(Element parentElement) {
        String edition = getStringOfDecoratedElement(parentElement, "edition", "#materialEdition");

        if (edition == "N/A") {
            return null;
        } else {
            // our edition string is likely something like "1st", "2nd", etc.  Strip
            // off the non-numerical suffix for this case
            Matcher matcher = EDITION_RE.matcher(edition);
            if (matcher.matches()) {
                return matcher.group("edition");
            } else {
                return edition;
            }
        }
    }

    private Date parseCopyrightYear(Element parentElement) {
        String dateStr = null;

        try {
            getStringOfDecoratedElement(parentElement, "copyright year", "#materialCopyrightYear");
        } catch (Exception ex) {
            // not all books have a copyright year element
            return null;
        }

        if (dateStr == null) {
            return null;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                return sdf.parse(dateStr);
            } catch (java.text.ParseException ex) {
                return null;
            }
        }
    }

    // return the undecorated text associated with an element that matches the supplied selector.
    // 'undecorated' essentially means text of
    private String getStringOfDecoratedElement(Element parentElem, String resourceName, String selector) {
        Elements elems = parentElem.select(selector);

        if (elems.size() == 1) {
            Element elem = elems.get(0);

            // delete all child elements of our element.  This removes decorative text, such as
            // "Author: "
            deleteChildren(elem);

            return elem.text().trim();
        } else {
            throw new RuntimeException("Unable to find " + resourceName + " on URL: " + url);
        }
    }

    private void deleteChildren(Element element) {
        for (Element e : element.children()){
            e.empty();
        }
    }

    // determine if the table is parseable based on a header row
    private boolean canParsePriceTable(Element headerRow) {
        try {
            Elements cols = headerRow.getElementsByTag("th");
            return
                cols.get(BOOK_TYPE_COL).text().contains("Type") &&
                cols.get(ORDER_TYPE_COL).text().contains("Buy") &&
                cols.get(ORDER_OPTION_COL).text().contains("Option") &&
                cols.get(PRICE_COL).text().contains("Price");
        } catch (Exception ex) {
            return false;
        }
    }

}
