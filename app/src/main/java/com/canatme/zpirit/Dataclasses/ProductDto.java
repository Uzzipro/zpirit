package com.canatme.zpirit.Dataclasses;

public class ProductDto {
    private String productID;
    private String productImg;
    private String productType;
    private String productName;
    private String productMeasurement;
    private String productPrice;


    public ProductDto(String productID, String productImg, String productType, String productName, String productMeasurement, String productPrice) {
        this.productID = productID;
        this.productImg = productImg;
        this.productType = productType;
        this.productName = productName;
        this.productMeasurement = productMeasurement;
        this.productPrice = productPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductMeasurement() {
        return productMeasurement;
    }

    public void setProductMeasurement(String productMeasurement) {
        this.productMeasurement = productMeasurement;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public ProductDto() {
    }
}
