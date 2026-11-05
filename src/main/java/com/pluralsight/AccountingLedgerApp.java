package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
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

    // Display every transaction that is from the 1st of the current month
    public static void displayMonthToDate(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[CURRENT MONTH]");

        LocalDate today = LocalDate.now();

        for (Transaction t : transactions) {
            LocalDate date = t.getDate();
            if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth()) {
                System.out.println(t);
            }
        }
    }

    // Display every transaction from the previous month
    public static void displayPrevMonth(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[PREVIOUS MONTH]");

        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        for (Transaction t : transactions) {
            LocalDate date = t.getDate();
            if (date.getYear() == lastMonth.getYear() && date.getMonth() == lastMonth.getMonth()) {
                System.out.println(t);
            }
        }
    }

    // Display every transaction from the current year
    public static void displayYearToDate(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[YEAR TO DATE]");

        int currentYear = LocalDate.now().getYear();

        for (Transaction t : transactions) {
            int transactionYear = t.getDate().getYear();
            if (transactionYear == currentYear) {
                System.out.println(t);
            }
        }
    }

    // Display every transaction from the previous year
    public static void displayPrevYear(ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[PREVIOUS YEAR]");

        int prevYear = LocalDate.now().minusYears(1).getYear();

        for (Transaction t : transactions) {
            int transactionYear = t.getDate().getYear();
            if (transactionYear == prevYear) {
                System.out.println(t);
            }
        }
    }

    // Prompt user to enter vendor name, displays all transactions from them
    public static void searchByVendor(Scanner scanner, ArrayList<Transaction> transactions) {
        System.out.println();
        System.out.println("[VENDOR SEARCH]");
        System.out.println("-> Enter vendor name to display all their transactions: ");
        String response = scanner.nextLine();

        for (Transaction t : transactions) {
            String vendor = t.getVendor();
            if (vendor.equalsIgnoreCase(response)) {
                // Found vendor in search
                System.out.println(t);
            }
        }
    }

    // Does a custom search with filters the user can choose. You can also skip them by pressing enter.
    public static void customSearch(Scanner scanner, ArrayList<Transaction> transactions) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("[CUSTOM SEARCH SCREEN]");
            System.out.println("TIP: Press [Enter] to skip the search filter");

            // Prompt user for start date
            LocalDate startDate = null;
            while (true) {
                System.out.println("-> Enter start date (YYYY-MM-DD): ");
                String response = scanner.nextLine().trim();

                if (response.isEmpty()) {
                    // No response to this, skip in search
                    break;
                }

                try {
                    // Valid date format used
                    startDate = LocalDate.parse(response);
                    break;
                } catch (DateTimeParseException e) {
                    // Invalid date format used, try again.
                    System.out.println("Error: invalid date format. Must be like (YYYY-MM-DD)");
                }
            }

            // Prompt user for end date
            LocalDate endDate = null;
            while (true) {
                System.out.println("-> Enter end date (YYYY-MM-DD): ");
                String response = scanner.nextLine().trim();

                if (response.isEmpty()) {
                    // No response to this, skip in search
                    break;
                }

                try {
                    // Valid date format used
                    endDate = LocalDate.parse(response);
                    break;
                } catch (DateTimeParseException e) {
                    // Invalid date format used, try again.
                    System.out.println("Error: invalid date format. Must be like (YYYY-MM-DD)");
                }
            }

            // Prompt user for description
            System.out.println("-> Enter description: ");
            String desc = scanner.nextLine().trim();

            // Prompt user for vendor
            System.out.println("-> Enter the vendor: ");
            String vendor = scanner.nextLine().trim();

            // Prompt user for amount
            Double amount = null;
            while (true) {
                System.out.println("-> Enter the amount: ");
                String response = scanner.nextLine().trim();

                if (response.isEmpty()) {
                    // No response to this, skip in search
                    break;
                }

                try {
                    amount = Double.parseDouble(response);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a number");
                }
            }

            // Run the search with the filters.
            // If the transaction doesn't meet the filters standard we continue to the
            // next transaction and don't print it.
            for (Transaction t : transactions) {
                // Check if there is a valid start date, and the transaction date is before it
                if (startDate != null && t.getDate().isBefore(startDate)) continue;
                // Check if there is an end date, and the transaction date is after it
                if (endDate != null && t.getDate().isAfter(endDate)) continue;
                /*
                    If the description isn't empty and the transaction desc has it inside.
                    This allows the user to not have to type the entire desc, just stuff
                    that can be found inside of it.
                */
                if (!desc.isEmpty() && !t.getDesc().toLowerCase().contains(desc.toLowerCase())) continue;
                // If the vendor isn't empty and the transaction vendor is the same
                if (!vendor.isEmpty() && !t.getVendor().equalsIgnoreCase(vendor)) continue;
                if (amount != null && t.getAmount() != amount) continue;

                // Display the filtered result (congrats, you made it transaction!)
                System.out.println(t);
            }

            // Loop to ask the user if they want to search again, or go back.
            while (true) {
                System.out.println("\n-> Do you want to search again? (Y/N):");
                String response = scanner.nextLine().trim();

                if (response.equalsIgnoreCase("Y") ) {
                    // Accept, go through loop again
                    break;
                } else if (response.equalsIgnoreCase("N")) {
                    // Decline, stop running loop and go back a screen
                    running = false;
                    break;
                } else {
                    // Invalid response
                    System.out.println("Error: invalid response, try again");
                }
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
            System.out.println("6.) Custom search");
            System.out.println("0.) Back");

            System.out.println("-> Choose an option: ");
            int response;
            try {
                // Response is an int, we can continue
                response = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // Response is not an int, try again
                System.out.println("Error: Please enter a number");
                continue;
            }

            if (response == 1) {
                // Month to date
                displayMonthToDate(transactions);
            } else if (response == 2) {
                // Previous month
                displayPrevMonth(transactions);
            } else if (response == 3) {
                // Year to date
                displayYearToDate(transactions);
            } else if (response == 4) {
                // Previous year
                displayPrevYear(transactions);
            } else if (response == 5) {
                // Search by vendor name
                searchByVendor(scanner,transactions);
            } else if (response == 6) {
                // Custom search
                customSearch(scanner,transactions);
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
            // Verify amount is actually a number, try again if not
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a number");
                continue;
            }

            // Re-prompt if the user tries to have a positive value
            // because deposits MUST be negative
            if (amount > 0) {
                System.out.println("Error: debit amount must be negative\n");
            } else {
                break;
            }
        }

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
            // Verify amount is actually a number, try again if not
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a number");
                continue;
            }

            // Re-prompt if the user tries to have a negative value
            // because deposits MUST be positive
            if (amount < 0) {
                System.out.println("Error: deposit amount must be positive");
            } else {
                break;
            }
        }

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
                amount;

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