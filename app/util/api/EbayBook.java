package util.api;

import util.api.Book;

public class EbayBook extends Book {
    private boolean isAuction;
    private float shippingPrice;
    private String condition;
    private String location;

    public boolean getIsAuction() { return isAuction; }
    public float getShippingPrice() { return shippingPrice; }
    public String getCondition() { return condition; }
    public String getLocation() { return location; }
    public double getShippedPrice() {
        // it's odd that we get fractional cents from ebay at times...
        // convert to 2 digits of precision, including price & shipping price
        return Math.round((price + shippingPrice) * 100) / 100.0;
    }
    public String getListingType() {
        if (isAuction) {
            return "Auction";
        } else {
            return "Fixed Price";
        }
    }

    public EbayBook(String title, String url, float price, float shippingPrice, String condition,
                String imageUrl, String location, Boolean isAuction) {
        this.title = title;
        this.url = url;
        this.price = price;
        this.shippingPrice = shippingPrice;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.isAuction = isAuction;
        this.location = location;
    }
}
