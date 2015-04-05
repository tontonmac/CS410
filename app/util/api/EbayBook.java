package util.api;

import util.api.Book;

public class EbayBook extends Book {
    private boolean isAuction;
    private float shippingPrice;
    private String condition;

    public boolean getIsAuction() { return isAuction; }
    public float getShippingPrice() { return shippingPrice; }
    public String getCondition() { return condition; }

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
}
