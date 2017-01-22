package com.grabble.customclasses;

/**
 * Created by mihaivisuian on 22/01/2017.
 */

public class BundleOffer {
    private int tokenPrice;
    private int gemPrice;
    private int imageId;
    private int quantity;
    private double cashPrice;

    public double getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(double cashPrice) {
        this.cashPrice = cashPrice;
    }

    public int getTokenPrice() {
        return tokenPrice;
    }

    public void setTokenPrice(int tokenPrice) {
        this.tokenPrice = tokenPrice;
    }

    public int getGemPrice() {
        return gemPrice;
    }

    public void setGemPrice(int gemPrice) {
        this.gemPrice = gemPrice;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BundleOffer(int quantity, int imageId, int tokenPrice, int gemPrice, double cashPrice) {
        this.tokenPrice = tokenPrice;
        this.gemPrice = gemPrice;
        this.imageId = imageId;
        this.quantity = quantity;
        this.cashPrice = cashPrice;
    }
}
