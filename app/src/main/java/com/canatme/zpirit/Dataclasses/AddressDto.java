package com.canatme.zpirit.Dataclasses;

public class AddressDto {
    private String addressID;
    private String houseNumber;
    private String floor;
    private String towerBlock;
    private String howToReachOptional;
    private String tag;


    public AddressDto(String addressID, String houseNumber, String floor, String towerBlock, String howToReachOptional, String tag) {
        this.addressID = addressID;
        this.houseNumber = houseNumber;
        this.floor = floor;
        this.towerBlock = towerBlock;
        this.howToReachOptional = howToReachOptional;
        this.tag = tag;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getTowerBlock() {
        return towerBlock;
    }

    public void setTowerBlock(String towerBlock) {
        this.towerBlock = towerBlock;
    }

    public String getHowToReachOptional() {
        return howToReachOptional;
    }

    public void setHowToReachOptional(String howToReachOptional) {
        this.howToReachOptional = howToReachOptional;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public AddressDto()
    {

    }
}
