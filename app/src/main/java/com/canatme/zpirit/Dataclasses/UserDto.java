package com.canatme.zpirit.Dataclasses;

public class UserDto {
    /*Essential fields*/
    private String fName;
    private String lName;
    private String phNumber;
    private String emailAddress;
    private String password;
    private String gender;

    /*Non essential fiels*/
    private String totalOrders;
    private String deliveryAddress;
    private String specificOffers;
    private String verifiedAadhaar;
    private String profilePicture;
    private String bio;


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(String totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getSpecificOffers() {
        return specificOffers;
    }

    public void setSpecificOffers(String specificOffers) {
        this.specificOffers = specificOffers;
    }

    public String getVerifiedAadhaar() {
        return verifiedAadhaar;
    }

    public void setVerifiedAadhaar(String verifiedAadhaar) {
        this.verifiedAadhaar = verifiedAadhaar;
    }


    public UserDto(String fName, String lName, String phNumber, String emailAddress, String password, String gender, String totalOrders, String deliveryAddress, String specificOffers, String verifiedAadhaar, String profilePicture, String bio) {
        this.fName = fName;
        this.lName = lName;
        this.phNumber = phNumber;
        this.emailAddress = emailAddress;
        this.password = password;
        this.gender = gender;
        this.totalOrders = totalOrders;
        this.deliveryAddress = deliveryAddress;
        this.specificOffers = specificOffers;
        this.verifiedAadhaar = verifiedAadhaar;
        this.profilePicture = profilePicture;
        this.bio = bio;
    }

    public UserDto()
    {

    }
}
