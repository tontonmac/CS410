package util.api;

import java.util.ArrayList;
import util.api.EbayBook;

public class EbaySearchResult {
    private ArrayList<EbayBook> books;
    private String url;

    public EbaySearchResult(String url, ArrayList<EbayBook> books) {
        this.books = books;
        this.url = url;
    }

    public String getUrl() { return this.url; }
    public ArrayList<EbayBook> getBooks() { return this.books; }
}
