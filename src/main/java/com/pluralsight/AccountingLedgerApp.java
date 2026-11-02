package com.pluralsight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AccountingLedgerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean appRunning = true;
        while (appRunning) {
            String response = homeScreen(scanner);
            if (response.equalsIgnoreCase("D")) {
                // Add a deposit
                addDeposit(scanner);
            } else if (response.equalsIgnoreCase("P")) {
                // Make payment (debit)

            } else if (response.equalsIgnoreCase("L")) {
                // Display ledger

            } else if (response.equalsIgnoreCase("X")) {
                // Exit program
                appRunning = false;
            } else {
                // Invalid input
                System.out.println("-> Invalid input, please try again");
            }
        }
    }

    public static void addDeposit(Scanner scanner) {
        // Prompt for input to create deposit entry in transactions.csv
        System.out.println();
        System.out.println("[ADD DEPOSIT]");

        System.out.println("-> Enter deposit description: ");
        String desc = scanner.nextLine().trim();

        System.out.println("-> Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        System.out.println("-> Enter amount: ");
        double amount = scanner.nextDouble();

        scanner.nextLine();
        // Write deposit into transactions.csv
        writeTransaction(desc,vendor,amount);
    }

    public static void writeTransaction(String desc, String vendor, double amount) {
        // Create the string that will be appended to the transactions.csv file
        System.out.println("-> Adding to transactions.csv");
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String transactionLine = formattedDateTime + "|" +
                desc + "|" +
                vendor + "|" +
                String.valueOf(amount);

        // Append to the transactions.csv file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv",true))) {
            writer.write(transactionLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static String homeScreen(Scanner scanner) {
        // Display the home screen options and get a response
        System.out.println("[HOME SCREEN]");
        System.out.println("D.) Add deposit");
        System.out.println("P.) Make payment (debit)");
        System.out.println("L.) Display ledger");
        System.out.println("X.) Exit");
        System.out.println("-> Choose an option: ");

        return scanner.nextLine().trim();
    }
}