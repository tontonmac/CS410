package util.api;

import java.util.ArrayList;
import java.io.IOException;

import util.api.AmazonBook;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

public class AmazonApi extends Base {
    private static final String ASSOCIATE_TAG = "beacbook08-20";
    private static final String ACCESS_KEY = "AKIAJKYKCZTGL5WJFYNA";
    private static final String SECRET_KEY = "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";
    private static final String SERVER = "ecs.amazonaws.com";

    public ArrayList<AmazonBook> find(String ISBN) throws IOException {
        ArrayList<AmazonBook> books = new ArrayList<>();
        Document doc = getDocument( searchUrl(ISBN) );

        Elements bookElems = doc.select("itemlookupresponse items item");

        for (Element bookElem : bookElems) {
            AmazonBook book = parseBook(bookElem);
            if (book != null) {
                books.add(book);
            }
        }

        return books;
    }

    private String searchUrl(String ISBN) {
        AmazonSignedRequestsHelper helper;
        try {
            helper = AmazonSignedRequestsHelper.getInstance(SERVER, ACCESS_KEY, SECRET_KEY);
        } catch (Exception ex) {
            System.out.println("failed to get helper instance: " + ex.toString());
            return null;
        }

        String queryString = "Service=AWSECommerceService&Operation=ItemLookup&Version=2011-08-01" +
            "&AssociateTag=" + ASSOCIATE_TAG + "&SearchIndex=Books&Availability=Available" +
            "&ItemId=" + ISBN + "&IdType=ISBN&Condition=All&ResponseGroup=Images,ItemAttributes,OfferFull,Small";

        return helper.sign(queryString);
    }

    private AmazonBook parseBook(Element parent) {
        float newPrice = 0;
        float usedPrice = 0;

        Elements offers = parent.select("offers offer");

        for (Element offer : offers) {
            String condition = getElementText(offer, "offerattributes condition");
            if (condition.equals("New")) {
                newPrice = Float.parseFloat( getElementText(offer, "price amount") ) / 100;
            } else if (condition.equals("Used")) {
                usedPrice = Float.parseFloat( getElementText(offer, "price amount") ) / 100;
            }
        }

        if (newPrice == 0 && usedPrice == 0) {
            return null;        // no copies of the book available
        } else{
            return new AmazonBook(
                    getElementText(parent, "itemattributes title"),
                    getElementText(parent, "itemattributes publisher"),
                    getElementText(parent, "itemattributes author"),
                    getElementText(parent, "itemattributes edition", false),
                    getElementText(parent, "detailpageurl"),
                    newPrice,
                    usedPrice,
                    getElementText(parent, "largeimage url", false));
        }
    }
}
