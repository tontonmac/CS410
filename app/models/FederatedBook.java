package models;

import util.api.AmazonApi;
import util.api.EbayApi;
import util.api.EbayBook;
import util.api.EbaySearchResult;
import util.api.AmazonBook;
import models.Book;

import java.util.ArrayList;
import java.util.List;

public class FederatedBook {
    private models.Book bookstoreBook;
    private AmazonBook amazonBook;
    private String isbn;
    private EbaySearchResult ebayResult;
    private List<Listing> localResults;


    public FederatedBook(String isbn) {
        this.isbn = isbn;
        bookstoreBook = models.Book.findUnique(isbn);
        localResults = Listing.findByIsbn(isbn);

        AmazonApi az = new AmazonApi();
        try {
            ArrayList<AmazonBook> amazonBooks = az.find(isbn);

            // the first book corresponds to a physical book, while later ones (if any) correspond to kindle, we
            // should probably enforce this through code at some point
            if (amazonBooks.size() > 0) {
                amazonBook = amazonBooks.get(0);
            }
        } catch (Exception ex) {
            System.out.println("Failed to make amazon API query: " + ex.toString());
        }

        EbayApi ebay = new EbayApi();
        try {
            ebayResult = ebay.find(isbn);
        } catch (Exception ex) {
            System.out.println("Failed to make eBay API query: " + ex.toString());
        }
    }

    public boolean foundBook() {
        return foundAmazonBook() || foundBookstoreBook() || foundEbayBook();
    }

    public String getTitle() {
        if (foundAmazonBook()) {
            return amazonBook.getTitle();
        } else if (foundBookstoreBook()) {
            return bookstoreBook.title;
        } else if (foundEbayBook()) {
            return getEbayBooks().get(0).getTitle();
        } else {
            return "unknown";
        }
    }

    public String getAuthor() {
        if (foundBookstoreBook()) {
            return bookstoreBook.author;
        } else if (foundAmazonBook()) {
            return amazonBook.getAuthor();
        } else {
            return "unknown";
        }
    }
     
    public String getISBN() {
        return isbn;
    }

    public String getPublisher() {
        if (foundBookstoreBook()) {
            return bookstoreBook.publisher;
        } else if (foundAmazonBook()) {
            return amazonBook.getPublisher();
        } else {
            return "unknown";
        }
    }

    public String getCopyrightYear() {
        if (foundBookstoreBook()) {
            return bookstoreBook.getCopyrightYear();
        } else {
            return "unknown";
        }
    }

    public String getEdition() {
        if (foundBookstoreBook()) {
            return bookstoreBook.edition;
        } else if (foundAmazonBook()) {
            return amazonBook.getEdition();
        } else {
            return "unknown";
        }
    }

    public String getImageUrl() {
        if (foundAmazonBook()) {
            return amazonBook.getImageUrl();
        } else {
            return "";
        }
    }

    public boolean hasImage() {
        return !getImageUrl().equals("");
    }

    public ArrayList<EbayBook> getEbayBooks() {
        return ebayResult.getBooks();
    }

    public String getEbayUrl() {
        return ebayResult.getUrl();
    }

    public AmazonBook getAmazonBook() {
        return amazonBook;
    }

    public Book getBookstoreBook() {
        return bookstoreBook;
    }

    public List<Listing> getLocalResults() { return localResults; }

    public Boolean foundAmazonBook() { return amazonBook != null; }
    public Boolean foundBookstoreBook() { return bookstoreBook != null; }
    public Boolean foundEbayBook() { return ebayResult != null && getEbayBooks().size() > 0; }
    public Boolean foundLocalResult() { return localResults != null && localResults.size() > 0; }

}
