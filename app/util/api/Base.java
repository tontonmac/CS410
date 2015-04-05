package util.api;

import java.util.ArrayList;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

public class Base {

    protected String getElementText(Element parent, String elemName) {
        return getElementText(parent, elemName, true);
    }

    protected String getElementText(Element parent, String elemName, Boolean assertExists) {
        Elements e = parent.select(elemName);
        if (e.size() > 0) {
            return e.get(0).text().trim();
        } else if (assertExists) {
            throw new RuntimeException("Failed to parse Ebay Book: could not " +
                "find element with name " + elemName);
        } else {
            return "";
        }
    }

    protected Document getDocument(String url) throws IOException {
        int maxRetries = 3;
        int numRetries = 0;
        int timeoutSec = 30;

        while(true) {
            try {
                System.out.println("fetching url: " + url);
                Document doc = Jsoup.connect(url).
                    timeout(timeoutSec * 1000).
                    parser(Parser.xmlParser()).
                    get();

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
}
