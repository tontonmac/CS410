package util.api;

import java.util.ArrayList;
import java.io.IOException;

import util.api.EbayBook;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

// this class is not persisted in the database; it's constructed when processing ebay
// API results

public class EbayApi extends Base {
    public static final String API_KEY = "JoeDolla-b044-4206-bfcb-fa167ed6b37e";
    public static Integer MAX_RESULTS = 5;

    public ArrayList<EbayBook> find(String ISBN) throws IOException {
        Document doc = getDocument( searchUrl(ISBN) );
        ArrayList<EbayBook> books = new ArrayList<>();

        if (!isSuccess(doc)) {
            throw new RuntimeException("Failed to make API request.  Output was: " +
                doc.outerHtml());
        }

        Elements bookElems = doc.select("finditemsbyproductresponse searchresult item");

        for (Element bookElem : bookElems) {
            books.add( parseBook(bookElem) );
        }

        return books;
    }

    private boolean isSuccess(Document doc) {
        return doc.select("finditemsbyproductresponse ack").text().contains("Success");
    }

    private String searchUrl(String ISBN) {
        return "http://svcs.ebay.com/services/search/FindingService/v1?" +
                "OPERATION-NAME=findItemsByProduct&SERVICE-VERSION=1.0.0&" +
                "SECURITY-APPNAME=" + API_KEY + "&RESPONSE-DATA-FORMAT=XML&" +
                "paginationInput.entriesPerPage=" + MAX_RESULTS + "&" +
                "sortOrder=PricePlusShippingLowest&REST-PAYLOAD&" +
                "productId.@type=ISBN&productId=" + ISBN;
    }

    private EbayBook parseBook(Element parent) {
        return new EbayBook(
            getElementText(parent, "title"),
            getElementText(parent, "viewitemurl"),
            Float.parseFloat(getElementText(parent, "convertedcurrentprice[currencyid=USD]")),
            Float.parseFloat(getElementText(parent, "shippingservicecost[currencyid=USD]")),
            getElementText(parent, "conditiondisplayname"),
            getElementText(parent, "galleryurl", false),
            getElementText(parent, "listingType") == "Auction");
    }
}
