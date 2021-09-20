package com.aumento.onlinecabdriver.ModelClass;

public class TripHistoryModelClass {

    String id;
    String user;
    String start_loc;
    String end_loc;
    String slat;
    String slon;
    String elat;
    String elon;
    String date;
    String amount;
    String stime;
    String etime;
    String payment;
    String vtype;

    public TripHistoryModelClass(String id, String user, String start_loc, String end_loc, String slat, String slon, String elat, String elon, String date, String amount, String stime, String etime, String payment, String vtype) {
        this.id = id;
        this.user = user;
        this.start_loc = start_loc;
        this.end_loc = end_loc;
        this.slat = slat;
        this.slon = slon;
        this.elat = elat;
        this.elon = elon;
        this.date = date;
        this.amount = amount;
        this.stime = stime;
        this.etime = etime;
        this.payment = payment;
        this.vtype = vtype;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getStart_loc() {
        return start_loc;
    }

    public String getEnd_loc() {
        return end_loc;
    }

    public String getSlat() {
        return slat;
    }

    public String getSlon() {
        return slon;
    }

    public String getElat() {
        return elat;
    }

    public String getElon() {
        return elon;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getStime() {
        return stime;
    }

    public String getEtime() {
        return etime;
    }

    public String getPayment() {
        return payment;
    }

    public String getVtype() {
        return vtype;
    }
}
