package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    // Instance variables
    private LocalDate date;
    private LocalTime time;
    private String desc;
    private String vendor;
    private double amount;

    // Constructor
    public Transaction(LocalDate date, LocalTime time, String desc, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.vendor = vendor;
        this.amount = amount;
    }

    // Methods
    public static Transaction fromCsvLine(String line) {
        String[] p = line.split("\\|");
        return new Transaction(
                LocalDate.parse(p[0]),
                LocalTime.parse(p[1]),
                p[2], p[3],
                Double.parseDouble(p[4]));
    }

    public String toCsvLine() {
        return date + "|" + time + "|" + desc + "|" + vendor + "|" + amount;
    }

    // Make it so when I print the object itself, the .csv line is given
    @Override
    public String toString() {
        return toCsvLine();
    }

    // Getters & setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
