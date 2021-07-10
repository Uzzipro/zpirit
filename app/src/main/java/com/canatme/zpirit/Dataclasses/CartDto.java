package com.canatme.zpirit.Dataclasses;

public class CartDto {
    private String productKey;
    private String productQuantity;
    private String productTotalPrice;
    private ProductDto pd;
    private String nodeKey;

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(String productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public ProductDto getPd() {
        return pd;
    }

    public void setPd(ProductDto pd) {
        this.pd = pd;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public CartDto(String productKey, String productQuantity, String productTotalPrice, ProductDto pd, String nodeKey) {
        this.productKey = productKey;
        this.productQuantity = productQuantity;
        this.productTotalPrice = productTotalPrice;
        this.pd = pd;
        this.nodeKey = nodeKey;
    }

    public CartDto()
    {

    }
}
