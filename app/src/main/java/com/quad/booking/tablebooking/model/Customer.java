package com.quad.booking.tablebooking.model;

public class Customer {
    public String customerFirstName;
    public String customerLastName;
    public int id;
    public int tableNumber;
    public long timeOfBooking;

    public Customer() {
        tableNumber = -1;
    }

    public Customer(String customerFirstName, String customerLastName, int id, int tableNumber,long timeOfBooking) {
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.id = id;
        this.tableNumber=tableNumber;
        this.timeOfBooking=timeOfBooking;
    }
}
