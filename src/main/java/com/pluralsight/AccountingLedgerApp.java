package com.pluralsight;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;

public class AccountingLedgerApp {
    // Application main loop
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load existing transactions from .csv file and create a list
        ArrayList<Transaction> transactions = loadTransactions();

        // Starts the app loop. Goes on until you exit from the home screen
        homeScreen(scanner,transactions);

        scanner.close();
    }

    // Loads all the existing transactions from transactions.csv into ArrayList<>();
    public static ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Catch any blank lines that would error the code
                if (line.isBlank()) continue;
                // Create transaction object from the .csv line
                // and add to the list
                list.add(Transaction.fromCsvLine(line));
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        // Sort the list from newest to oldest
        list.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());
        return list;
    }

    // Displays every entry inside of transactions.csv
    public static void displayAllEntries(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[ALL TRANSACTION INFO]");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    // Displays all the deposit entries in transactions.csv
    public static void displayDeposits(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[DEPOSIT TRANSACTION INFO]");
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) { // Amount is positive, MUST be a deposit
                System.out.println(t);
            }
        }
    }

    // Displays all the payments / debit entries in transactions.csv
    public static void displayPayments(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[PAYMENT TRANSACTION INFO]");
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) { // Amount is negative, MUST be a debit / payment
                System.out.println(t);
            }
        }
    }

    // The reports screen, where the user can run reports or custom search
    public static void reportsScreen(Scanner scanner, ArrayList<Transaction> transactions) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("[REPORTS SCREEN]");
            System.out.println("1.) Month to date");
            System.out.println("2.) Previous month");
            System.out.println("3.) Year to date");
            System.out.println("4.) Previous year");
            System.out.println("5.) Search by vendor name");
            System.out.println("0.) Back");

            System.out.println("-> Choose an option: ");
            int response = scanner.nextInt();
            scanner.nextLine();

            if (response == 1) {
                // Month to date

            } else if (response == 2) {
                // Previous month

            } else if (response == 3) {
                // Year to date

            } else if (response == 4) {
                // Previous year

            } else if (response == 5) {
                // Vendor name

            } else if (response == 0) {
                // Go back to ledger screen
                running = false;
            } else {
                // Invalid input, try again
                System.out.println("Error: Invalid input, try again");
            }
        }
    }

    // Loads the ledger screen until you choose to go to the home screen
    public static void ledgerScreen(Scanner scanner, ArrayList<Transaction> transactions) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("[LEDGER SCREEN]");
            System.out.println("A.) Display all entries");
            System.out.println("D.) Display deposits (+)");
            System.out.println("P.) Display payments (-)");
            System.out.println("R.) Reports");
            System.out.println("H.) Home");

            System.out.println("-> Choose an option: ");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("A")) {
                // Display all entries
                displayAllEntries(transactions);
            } else if (response.equalsIgnoreCase("D")) {
                // Display deposit entries
                displayDeposits(transactions);
            } else if (response.equalsIgnoreCase("P")) {
                // Display payment entries
                displayPayments(transactions);
            } else if (response.equalsIgnoreCase("R")) {
                // Go to reports screen
                reportsScreen(scanner,transactions);
            } else if (response.equalsIgnoreCase("H")) {
                // Go back to home screen
                running = false;
            } else {
                // Invalid input, try again
                System.out.println("Error: Invalid input, try again");
                System.out.println("You entered: '" + response + "'");
            }
        }
    }

    // Adds a debit (-) to the transactions.csv
    public static void addDebit(Scanner scanner, ArrayList<Transaction> transactions) {
        // Prompt user to create debit entry in transactions.csv
        System.out.println();
        System.out.println("[ADD DEBIT]");

        System.out.println("-> Enter deposit description: ");
        String desc = scanner.nextLine().trim();

        System.out.println("-> Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        double amount;
        while (true) {
            System.out.println("-> Enter amount (-): ");
            amount = scanner.nextDouble();

            // Re-prompt if the user tries to have a negative value
            // because deposits MUST be positive
            if (amount > 0) {
                System.out.println("Error: debit amount must be negative\n");
            } else {
                break;
            }
        }

        scanner.nextLine();
        // Write deposit into transactions.csv
        writeTransaction(transactions,desc,vendor,amount);
    }

    // Adds a deposit (+) to transactions.csv
    public static void addDeposit(Scanner scanner, ArrayList<Transaction> transactions) {
        // Prompt user to create deposit entry in transactions.csv
        System.out.println();
        System.out.println("[ADD DEPOSIT]");

        System.out.println("-> Enter deposit description: ");
        String desc = scanner.nextLine().trim();

        System.out.println("-> Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        double amount;
        while (true) {
            System.out.println("-> Enter amount (+): ");
            amount = scanner.nextDouble();

            // Re-prompt if the user tries to have a negative value
            // because deposits MUST be positive
            if (amount < 0) {
                System.out.println("Error: deposit amount must be positive");
            } else {
                break;
            }
        }

        scanner.nextLine();
        // Write deposit into transactions.csv
        writeTransaction(transactions,desc,vendor,amount);
    }

    // Writes to the transactions.csv file
    public static void writeTransaction(ArrayList<Transaction> transactions, String desc, String vendor, double amount) {
        // Create the string that will be appended to the transactions.csv file
        System.out.println("-> Adding to transactions.csv");
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String transactionLine = formattedDateTime + "|" +
                desc + "|" +
                vendor + "|" +
                String.valueOf(amount);

        // Add to transactions ArrayList<>();
        transactions.add(Transaction.fromCsvLine(transactionLine));

        // Append to the transactions.csv file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv",true))) {
            writer.write(transactionLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        System.out.println();
    }

    // Displays home screen info & get response
    public static void homeScreen(Scanner scanner, ArrayList<Transaction> transactions) {
        // Main app loop, once it breaks the app is done
        boolean appRunning = true;
        while (appRunning) {
            // Display the home screen options and get a response
            System.out.println();
            System.out.println("[HOME SCREEN]");
            System.out.println("D.) Add deposit");
            System.out.println("P.) Make payment (debit)");
            System.out.println("L.) Display ledger");
            System.out.println("X.) Exit");
            System.out.println("-> Choose an option: ");

            String response = scanner.nextLine().trim();
            if (response.equalsIgnoreCase("D")) {
                // Add a deposit
                addDeposit(scanner,transactions);
            } else if (response.equalsIgnoreCase("P")) {
                // Make payment (debit)
                addDebit(scanner,transactions);
            } else if (response.equalsIgnoreCase("L")) {
                // Display ledger
                ledgerScreen(scanner,transactions);
            } else if (response.equalsIgnoreCase("X")) {
                // Exit program
                appRunning = false;
            } else {
                // Invalid input
                System.out.println("-> Invalid input, please try again");
            }
        }
    }
}