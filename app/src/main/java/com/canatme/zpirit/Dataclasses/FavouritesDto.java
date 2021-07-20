package com.canatme.zpirit.Dataclasses;

public class FavouritesDto {
    private String productID;
    private String pushKey;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public FavouritesDto(String productID, String pushKey) {
        this.productID = productID;
        this.pushKey = pushKey;
    }

    public FavouritesDto()
    {

    }
}
