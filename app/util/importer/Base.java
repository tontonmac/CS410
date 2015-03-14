package util.importer;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class Base {
    // matches one or more space characters.  0xC2 & OX0A = html
    // non-breaking space (i.e. &nbsp)
    protected static final String RE_SPACE = "[\\s\\xC2\\xA0]+";

    protected void log(String msg) {
        System.out.println(msg);
    }

    public abstract ArrayList<Resource> performImport() throws IOException;

    protected Document getDocument(String url) throws IOException {
        int maxRetries = 5;
        int numRetries = 0;
        int timeoutSec = 30;

        while(true) {
            try {
                return Jsoup.connect(url).timeout(timeoutSec * 1000).get();
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
