package util.api;

public abstract class Book {
    protected String imageUrl;
    protected String title;
    protected String url;
    protected float price;

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public float getPrice() { return price; }

    public String toString() {
        return title + ": " + "$" + price;
    }
}
