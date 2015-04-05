package models;

import util.api.AmazonApi;
import util.api.EbayApi;
import util.api.EbayBook;
import util.api.AmazonBook;
import models.Book;

import java.util.ArrayList;

public class FederatedBook {
    private models.Book bookstoreBook;
    private AmazonBook amazonBook;
    private String isbn;
    private ArrayList<EbayBook> ebayBooks = new ArrayList<>();

    public FederatedBook(String isbn) {
        this.isbn = isbn;
        bookstoreBook = models.Book.findUnique(isbn);

        AmazonApi az = new AmazonApi();
        try {
            ArrayList<AmazonBook> amazonBooks = az.find(isbn);

            // TODO: we always take the first book for now.  Secondary
            // are likely to be just kindle books (is this true?)
            if (amazonBooks.size() > 0) {
                amazonBook = amazonBooks.get(0);
            }
        } catch (Exception ex) {
            System.out.println("Failed to make amazon API query: " + ex.toString());
        }

        EbayApi ebay = new EbayApi();
        try {
            ebayBooks = ebay.find(isbn);
        } catch (Exception ex) {
            System.out.println("Failed to make eBay API query: " + ex.toString());
        }

    }

    public boolean foundBook() {
        return bookstoreBook != null || amazonBook != null || ebayBooks.size() > 0;
    }

    public String getTitle() {
        if (bookstoreBook != null) {
            return bookstoreBook.title;
        } else if (amazonBook != null) {
            return amazonBook.getTitle();
        } else if (ebayBooks.size() > 0) {
            return ebayBooks.get(0).getTitle();
        } else {
            return "unknown";
        }
    }

    public String getAuthor() {
        if (bookstoreBook != null) {
            return bookstoreBook.author;
        } else if (amazonBook != null) {
            return amazonBook.getAuthor();
        } else {
            return "unknown";
        }
    }
     
    public String getISBN() {
        return isbn;
    }

    public String getPublisher() {
        if (bookstoreBook != null) {
            return bookstoreBook.publisher;
        } else if (amazonBook != null) {
            return amazonBook.getPublisher();
        } else {
            return "unknown";
        }
    }

    public String getCopyrightYear() {
        if (bookstoreBook != null) {
            return bookstoreBook.getCopyrightYear();
        } else {
            return "unknown";
        }
    }

    public String getEdition() {
        if (bookstoreBook != null) {
            return bookstoreBook.edition;
        } else if (amazonBook != null) {
            return amazonBook.getEdition();
        } else {
            return "unknown";
        }
    }

    public String getImageUrl() {
        if (amazonBook != null) {
            return amazonBook.getImageUrl();
        } else {
            return "";
        }
    }

    public boolean hasImage() {
        return !getImageUrl().equals("");
    }

    public ArrayList<EbayBook> getEbayBooks() {
        return ebayBooks;
    }

    public AmazonBook getAmazonBook() {
        return amazonBook;
    }

    public Book getBookstoreBook() {
        return bookstoreBook;
    }

}
