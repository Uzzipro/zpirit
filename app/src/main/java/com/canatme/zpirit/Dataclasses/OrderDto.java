package com.canatme.zpirit.Dataclasses;

import java.util.ArrayList;

public class OrderDto {
    private String orderID;
    private String phNumber;
    private String grandTotal;
    private ArrayList<CartDto> cdc;
    private String orderTime;
    private String paymentStatus;
    private String paymentID;


    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public ArrayList<CartDto> getCdc() {
        return cdc;
    }

    public void setCdc(ArrayList<CartDto> cdc) {
        this.cdc = cdc;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderDto(String orderID, String phNumber, String grandTotal, ArrayList<CartDto> cdc, String orderTime, String paymentStatus, String paymentID) {
        this.orderID = orderID;
        this.phNumber = phNumber;
        this.grandTotal = grandTotal;
        this.cdc = cdc;
        this.orderTime = orderTime;
        this.paymentStatus = paymentStatus;
        this.paymentID = paymentID;
    }

    public OrderDto()
    {

    }
}
