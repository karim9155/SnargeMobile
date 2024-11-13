package com.example.snargemobile.models;

public class Event {
    private long id;
    private String name;
    private String description;
    private String date;
    private double price;
    private boolean paymentStatus; // New field to track payment status

    public Event(long id, String name, String description, String date, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.price = price;
        this.paymentStatus = paymentStatus;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public double getPrice() { return price; }
    public boolean isPaymentStatus() { return paymentStatus; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setPrice(double price) { this.price = price; }
    public void setPaymentStatus(boolean paymentStatus) { this.paymentStatus = paymentStatus; }
}
