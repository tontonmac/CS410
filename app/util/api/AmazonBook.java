package util.api;

import util.api.Book;

public class AmazonBook extends Book {
    private float usedPrice;
    private float newPrice;
    private String author;
    private String publisher;
    private String edition;

    public float getUsedPrice() { return usedPrice; }
    public float getNewPrice() { return newPrice; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getImageUrl() { return imageUrl; }
    public String getEdition() { return edition; }

    public AmazonBook(String title, String publisher, String author, String edition, String url, float newPrice, float usedPrice, String imageUrl) {
        this.title = title;
        this.url = url;
        this.author = author;
        this.edition = edition;
        this.publisher = publisher;
        this.usedPrice = usedPrice;
        this.newPrice = newPrice;
        this.imageUrl = imageUrl;
        this.newPrice = newPrice;
        this.usedPrice = usedPrice;
    }
}
