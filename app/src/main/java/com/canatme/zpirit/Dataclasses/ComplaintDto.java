package com.canatme.zpirit.Dataclasses;

public class ComplaintDto {
    private String complaintID;
    private String timeSubmitted;
    private String complaintText;
    private boolean solved;


    public String getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
    }

    public ComplaintDto(String complaintID, String timeSubmitted, String complaintText, boolean solved) {
        this.complaintID = complaintID;
        this.timeSubmitted = timeSubmitted;
        this.complaintText = complaintText;
        this.solved = solved;
    }

    public String getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(String timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }


    public ComplaintDto(){

    }

}
