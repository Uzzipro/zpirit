package com.canatme.zpirit.Dataclasses;

public class OrderCreatedResponseDto {
    private String id;
    private String entity;
    private String amount;
    private String amount_paid;
    private String amount_due;
    private String currency;
    private String receipt;
    private String offer_id;
    private String status;
    private String attempts;
    private String created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(String amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getAmount_due() {
        return amount_due;
    }

    public void setAmount_due(String amount_due) {
        this.amount_due = amount_due;
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

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttempts() {
        return attempts;
    }

    public void setAttempts(String attempts) {
        this.attempts = attempts;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public OrderCreatedResponseDto(String id, String entity, String amount, String amount_paid, String amount_due, String currency, String receipt, String offer_id, String status, String attempts, String created_at) {
        this.id = id;
        this.entity = entity;
        this.amount = amount;
        this.amount_paid = amount_paid;
        this.amount_due = amount_due;
        this.currency = currency;
        this.receipt = receipt;
        this.offer_id = offer_id;
        this.status = status;
        this.attempts = attempts;
        this.created_at = created_at;
    }
}
