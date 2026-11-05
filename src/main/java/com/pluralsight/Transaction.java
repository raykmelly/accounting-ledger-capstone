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

    // Make it so when I print the object itself, the .csv line as a string is given
    @Override
    public String toString() {
        return toCsvLine();
    }

    // Getters (setters not needed)
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getVendor() {
        return vendor;
    }

    public double getAmount() {
        return amount;
    }

    public String getDesc() {
        return desc;
    }
}
