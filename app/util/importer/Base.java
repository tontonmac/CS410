package util.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class Base {
    // matches one or more space characters.  0xC2 & OX0A = html
    // non-breaking space (i.e. &nbsp)
    protected static final String RE_SPACE = "[\\s\\xC2\\xA0]+";

    private HashMap<String, Document> cachedDocument = new HashMap<>();

    protected void log(String msg) {
        System.out.println(msg);
    }

    public abstract ArrayList<Resource> performImport() throws IOException;

    private HashMap<String, Document> cachedDocuments = new HashMap<>();

    protected Document getDocument(String url) throws IOException {
        return getDocument(url, true);
    }

    // replace all types of spaces (HTML and regular) with a regular space
    protected String normalizeSpaces(String origStr) {
        Pattern pattern = Pattern.compile(RE_SPACE);

        Matcher matcher = pattern.matcher(origStr);
        return matcher.replaceAll(" ");
    }

    protected Document getDocument(String url, Boolean enableCaching) throws IOException {
        int maxRetries = 5;
        int numRetries = 0;
        int timeoutSec = 30;

        if (enableCaching && cachedDocument.containsKey(url)) {
            return cachedDocument.get(url);
        }

        while(true) {
            try {
                log("fetching url: " + url);
                Document doc = Jsoup.connect(url).timeout(timeoutSec * 1000).get();

                if (enableCaching) {
                    cachedDocument.put(url, doc);
                }

                return doc;
            } catch (Exception ex) {
                if (++numRetries > maxRetries) {
                    throw ex;
                } else {
                    System.err.println("Exception while fetching " + url +
                            ". Exception:" + ex.toString() + ". Will retry");
                    try {
                        Thread.sleep(1000 * numRetries);
                    } catch (Exception ex2) {
                    }
                }
            }
        }

    }

    protected boolean isHeaderRow(Element elem) {
        return (elem.getElementsByTag("th").size() > 0 );
    }
}
