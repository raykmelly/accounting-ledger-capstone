package com.pluralsight;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AccountingLedgerApp {
    // Application main loop
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Starts the app loop. Goes on until you exit from the home screen
        homeScreen(scanner);

        scanner.close();
    }

    // Displays every entry inside of transactions.csv
    public static void displayAllEntries() {
        System.out.println();
        System.out.println("[ALL TRANSACTION INFO]");
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    // Displays all the deposit entries in transactions.csv
    public static void displayDeposits() {
        System.out.println();
        System.out.println("[DEPOSIT TRANSACTION INFO]");
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\|");
                double amount = Double.parseDouble(split[4]);
                if (amount > 0) {
                    // Amount is positive, MUST be a deposit
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    // Displays all the payments / debit entries in transactions.csv
    public static void displayPayments() {
        System.out.println();
        System.out.println("[PAYMENT TRANSACTION INFO]");
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\|");
                double amount = Double.parseDouble(split[4]);
                if (amount < 0) {
                    // Amount is negative, MUST be a debit / payment
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    // The reports screen, where the user can run reports or custom search
    public static void reportsScreen(Scanner scanner) {
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
    public static void ledgerScreen(Scanner scanner) {
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
                displayAllEntries();
            } else if (response.equalsIgnoreCase("D")) {
                // Display deposit entries
                displayDeposits();
            } else if (response.equalsIgnoreCase("P")) {
                // Display payment entries
                displayPayments();
            } else if (response.equalsIgnoreCase("R")) {
                // Go to reports screen
                reportsScreen(scanner);
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
    public static void addDebit(Scanner scanner) {
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
        writeTransaction(desc,vendor,amount);
    }

    // Adds a deposit (+) to transactions.csv
    public static void addDeposit(Scanner scanner) {
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
        writeTransaction(desc,vendor,amount);
    }

    // Writes to the transactions.csv file
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
        System.out.println();
    }

    // Displays home screen info & get response
    public static void homeScreen(Scanner scanner) {
        // Display the home screen options and get a response
        boolean appRunning = true;
        while (appRunning) {
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
                addDeposit(scanner);
            } else if (response.equalsIgnoreCase("P")) {
                // Make payment (debit)
                addDebit(scanner);
            } else if (response.equalsIgnoreCase("L")) {
                // Display ledger
                ledgerScreen(scanner);
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