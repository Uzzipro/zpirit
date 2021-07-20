package com.canatme.zpirit.Dataclasses;

public class FeedbackDto {
    private String orderID;
    private String starRating;
    private String textFeedback;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStarRating() {
        return starRating;
    }

    public void setStarRating(String starRating) {
        this.starRating = starRating;
    }

    public String getTextFeedback() {
        return textFeedback;
    }

    public void setTextFeedback(String textFeedback) {
        this.textFeedback = textFeedback;
    }

    public FeedbackDto(String orderID, String starRating, String textFeedback) {
        this.orderID = orderID;
        this.starRating = starRating;
        this.textFeedback = textFeedback;
    }
}
