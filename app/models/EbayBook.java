package models;

import java.util.ArrayList;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

// this class is not persisted in the database; it's constructed when processing ebay
// API results

public class EbayBook {
    public static final String API_KEY = "JoeDolla-b044-4206-bfcb-fa167ed6b37e";
    public static Integer MAX_RESULTS = 5;

    private String condition;
    private String imageUrl;
    private String title;
    private String url;
    private boolean isAuction;
    private float price;
    private float shippingPrice;

    public String getCondition() { return condition; }
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public boolean getIsAuction() { return isAuction; }
    public float getPrice() { return price; }
    public float getShippingPrice() { return shippingPrice; }

    public String toString() {
        return title + ": " + "$" + price + ", condition: " + condition;
    }

    public EbayBook(String title, String url, float price, float shippingPrice, String condition,
                    String imageUrl, Boolean isAuction) {
        this.title = title;
        this.url = url;
        this.price = price;
        this.shippingPrice = shippingPrice;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.isAuction = isAuction;
    }

    public static ArrayList<EbayBook> find(String ISBN) throws IOException {
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

        for (EbayBook b : books) {
            System.out.println( b.toString() );
        }

        return books;
    }


    private static String searchUrl(String ISBN) {
        return "http://svcs.ebay.com/services/search/FindingService/v1?" +
                "OPERATION-NAME=findItemsByProduct&SERVICE-VERSION=1.0.0&" +
                "SECURITY-APPNAME=" + API_KEY + "&RESPONSE-DATA-FORMAT=XML&" +
                "paginationInput.entriesPerPage=" + MAX_RESULTS + "&" +
                "sortOrder=PricePlusShippingLowest&REST-PAYLOAD&" +
                "productId.@type=ISBN&productId=" + ISBN;
    }

    private static EbayBook parseBook(Element parent) {
        return new EbayBook(
            getElementText(parent, "title"),
            getElementText(parent, "viewitemurl"),
            Float.parseFloat(getElementText(parent, "convertedcurrentprice[currencyid=USD]")),
            Float.parseFloat(getElementText(parent, "shippingservicecost[currencyid=USD]")),
            getElementText(parent, "conditiondisplayname"),
            getElementText(parent, "galleryurl", false),
            getElementText(parent, "listingType") == "Auction");
    }

    private static String getElementText(Element parent, String elemName) {
        return getElementText(parent, elemName, true);
    }

    private static String getElementText(Element parent, String elemName, Boolean assertExists) {
        Elements e = parent.select(elemName);
        if (e.size() > 0) {
            return e.get(0).text();
        } else if (assertExists) {
            throw new RuntimeException("Failed to parse Ebay Book: could not " +
                "find element with name " + elemName);
        } else {
            return "";
        }
    }

    private static Boolean isSuccess(Document doc) {
        return doc.select("finditemsbyproductresponse ack").text().contains("Success");
    }

    private static Document getDocument(String url) throws IOException {
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