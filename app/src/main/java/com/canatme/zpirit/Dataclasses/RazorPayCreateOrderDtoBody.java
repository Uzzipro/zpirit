package com.canatme.zpirit.Dataclasses;

public class RazorPayCreateOrderDtoBody {
    private int amount;
    private String currency;
    private String receipt;
    private int payment_capture;


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public int getPayment_capture() {
        return payment_capture;
    }

    public void setPayment_capture(int payment_capture) {
        this.payment_capture = payment_capture;
    }

    public RazorPayCreateOrderDtoBody(int amount, String currency, String receipt, int payment_capture) {
        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
        this.payment_capture = payment_capture;
    }

    public RazorPayCreateOrderDtoBody()
    {

    }

}
