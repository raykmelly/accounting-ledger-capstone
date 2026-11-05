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
        UserInterface.init();
        Scanner scanner = new Scanner(System.in);

        // Load existing transactions from .csv file and create a list
        ArrayList<Transaction> transactions = loadTransactions();

        // Launch screen, waits for the user to press Enter
        UserInterface.splash(scanner);

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
        UserInterface.transactions("All transactions", transactions);
    }

    // Displays all the deposit entries in transactions.csv
    public static void displayDeposits(ArrayList<Transaction> transactions) {
        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) { // Amount is positive, MUST be a deposit
                matches.add(t);
            }
        }
        UserInterface.transactions("Deposits", matches);
    }

    // Displays all the payments / debit entries in transactions.csv
    public static void displayPayments(ArrayList<Transaction> transactions) {
        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) { // Amount is negative, MUST be a debit / payment
                matches.add(t);
            }
        }
        UserInterface.transactions("Payments", matches);
    }

    // Display every transaction that is from the 1st of the current month
    public static void displayMonthToDate(ArrayList<Transaction> transactions) {
        LocalDate today = LocalDate.now();
        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();

        for (Transaction t : transactions) {
            LocalDate date = t.getDate();
            if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth()) {
                matches.add(t);
            }
        }
        UserInterface.transactions("Month to date", matches);
    }

    // Display every transaction from the previous month
    public static void displayPrevMonth(ArrayList<Transaction> transactions) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();

        for (Transaction t : transactions) {
            LocalDate date = t.getDate();
            if (date.getYear() == lastMonth.getYear() && date.getMonth() == lastMonth.getMonth()) {
                matches.add(t);
            }
        }
        UserInterface.transactions("Previous month", matches);
    }

    // Display every transaction from the current year
    public static void displayYearToDate(ArrayList<Transaction> transactions) {
        int currentYear = LocalDate.now().getYear();
        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();

        for (Transaction t : transactions) {
            if (t.getDate().getYear() == currentYear) {
                matches.add(t);
            }
        }
        UserInterface.transactions("Year to date", matches);
    }

    // Display every transaction from the previous year
    public static void displayPrevYear(ArrayList<Transaction> transactions) {
        int prevYear = LocalDate.now().minusYears(1).getYear();
        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();

        for (Transaction t : transactions) {
            if (t.getDate().getYear() == prevYear) {
                matches.add(t);
            }
        }
        UserInterface.transactions("Previous year", matches);
    }

    // Prompt user to enter vendor name, displays all transactions from them
    public static void searchByVendor(Scanner scanner, ArrayList<Transaction> transactions) {
        UserInterface.section("Vendor search");
        UserInterface.prompt("Enter vendor name:");
        String response = scanner.nextLine().trim();

        // Create a list of all the matches so UserInterface can display them as one page
        ArrayList<Transaction> matches = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getVendor().equalsIgnoreCase(response)) {
                // Found vendor in search
                matches.add(t);
            }
        }
        UserInterface.transactions("Vendor: " + response, matches);
    }

    // Does a custom search with filters the user can choose. You can also skip them by pressing enter.
    public static void customSearch(Scanner scanner, ArrayList<Transaction> transactions) {
        boolean running = true;
        while (running) {
            UserInterface.section("Custom search");
            UserInterface.hint("Press Enter to skip any filter");

            // Prompt user for start date
            LocalDate startDate = null;
            while (true) {
                UserInterface.prompt("Start date (YYYY-MM-DD):");
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
                    UserInterface.error("Invalid date format. Must be like (YYYY-MM-DD)");
                }
            }

            // Prompt user for end date
            LocalDate endDate = null;
            while (true) {
                UserInterface.prompt("End date (YYYY-MM-DD):");
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
                    UserInterface.error("Invalid date format. Must be like (YYYY-MM-DD)");
                }
            }

            // Prompt user for description
            UserInterface.prompt("Description:");
            String desc = scanner.nextLine().trim();

            // Prompt user for vendor
            UserInterface.prompt("Vendor:");
            String vendor = scanner.nextLine().trim();

            // Prompt user for amount
            Double amount = null;
            while (true) {
                UserInterface.prompt("Amount:");
                String response = scanner.nextLine().trim();

                if (response.isEmpty()) {
                    // No response to this, skip in search
                    break;
                }

                try {
                    amount = Double.parseDouble(response);
                    break;
                } catch (NumberFormatException e) {
                    UserInterface.error("Please enter a number");
                }
            }

            // Run the search with the filters.
            // If the transaction doesn't meet the filters standard we continue to the
            // next transaction and don't keep it.
            ArrayList<Transaction> matches = new ArrayList<>();
            for (Transaction t : transactions) {
                // Skip if the transaction date is before the start date
                if (startDate != null && t.getDate().isBefore(startDate)) continue;
                // Skip if the transaction date is after the end date
                if (endDate != null && t.getDate().isAfter(endDate)) continue;
                /*
                    If the description isn't empty and the transaction desc has it inside.
                    This allows the user to not have to type the entire desc, just stuff
                    that can be found inside of it.
                */
                if (!desc.isEmpty() && !t.getDesc().toLowerCase().contains(desc.toLowerCase())) continue;
                // If the vendor isn't empty and the transaction vendor is the same
                if (!vendor.isEmpty() && !t.getVendor().equalsIgnoreCase(vendor)) continue;
                // If amount isn't empty and the transaction value isn't the same
                if (amount != null && t.getAmount() != amount) continue;

                // Made it through every filter, keep it (congrats, you made it transaction!)
                matches.add(t);
            }
            UserInterface.transactions("Search results", matches);

            // Loop to ask the user if they want to search again, or go back.
            while (true) {
                System.out.println();
                UserInterface.prompt("Search again? (Y/N):");
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
                    UserInterface.error("Invalid response, try again");
                }
            }
        }
    }

    // The reports screen, where the user can run reports or custom search
    public static void reportsScreen(Scanner scanner, ArrayList<Transaction> transactions) {
        boolean running = true;
        while (running) {
            UserInterface.menu("Reports",
                    "1|Month to date",
                    "2|Previous month",
                    "3|Year to date",
                    "4|Previous year",
                    "5|Search by vendor",
                    "6|Custom search",
                    "0|Back");
            UserInterface.prompt("Choose an option:");

            int response;
            try {
                // Response is an int, we can continue
                response = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // Response is not an int, try again
                UserInterface.error("Please enter a number");
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
                UserInterface.error("Invalid input, try again");
            }
        }
    }

    // Loads the ledger screen until you choose to go to the home screen
    public static void ledgerScreen(Scanner scanner, ArrayList<Transaction> transactions) {
        boolean running = true;
        while (running) {
            UserInterface.menu("Ledger",
                    "A|All entries",
                    "D|Deposits (+)",
                    "P|Payments (-)",
                    "R|Reports",
                    "H|Home");
            UserInterface.prompt("Choose an option:");

            String response = scanner.nextLine().trim();

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
                UserInterface.error("Invalid input, try again (you entered '" + response + "')");
            }
        }
    }

    // Adds a debit (-) to the transactions.csv
    public static void addDebit(Scanner scanner, ArrayList<Transaction> transactions) {
        // Prompt user to create debit entry in transactions.csv
        UserInterface.section("Make a payment");

        UserInterface.prompt("Payment description:");
        String desc = scanner.nextLine().trim();

        UserInterface.prompt("Vendor:");
        String vendor = scanner.nextLine().trim();

        double amount;
        while (true) {
            UserInterface.prompt("Amount (negative, e.g. -25.00):");
            // Verify amount is actually a number, try again if not
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                UserInterface.error("Please enter a number");
                continue;
            }

            // Re-prompt if the user tries to have a positive value
            // because debits MUST be negative
            if (amount > 0) {
                UserInterface.error("Payment amount must be negative");
            } else {
                break;
            }
        }

        // Write debit into transactions.csv
        writeTransaction(transactions,desc,vendor,amount);
    }

    // Adds a deposit (+) to transactions.csv
    public static void addDeposit(Scanner scanner, ArrayList<Transaction> transactions) {
        // Prompt user to create deposit entry in transactions.csv
        UserInterface.section("Add a deposit");

        UserInterface.prompt("Deposit description:");
        String desc = scanner.nextLine().trim();

        UserInterface.prompt("Vendor:");
        String vendor = scanner.nextLine().trim();

        double amount;
        while (true) {
            UserInterface.prompt("Amount (positive, e.g. 100.00):");
            // Verify amount is actually a number, try again if not
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                UserInterface.error("Please enter a number");
                continue;
            }

            // Re-prompt if the user tries to have a negative value
            // because deposits MUST be positive
            if (amount < 0) {
                UserInterface.error("Deposit amount must be positive");
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
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String transactionLine = formattedDateTime + "|" +
                desc + "|" +
                vendor + "|" +
                amount;

        // Add to the front of the transactions ArrayList<>(); so it stays newest first
        transactions.add(0, Transaction.fromCsvLine(transactionLine));

        // Append to the transactions.csv file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv",true))) {
            writer.write(transactionLine);
            writer.newLine();
            UserInterface.success("Saved to transactions.csv");
        } catch (IOException e) {
            UserInterface.error("Could not write to file: " + e.getMessage());
        }
    }

    // Displays home screen info & get response
    public static void homeScreen(Scanner scanner, ArrayList<Transaction> transactions) {
        // Main app loop, once it breaks the app is done
        boolean appRunning = true;
        while (appRunning) {
            // Display the home screen options and get a response
            UserInterface.menu("Home",
                    "D|Add deposit",
                    "P|Make payment (debit)",
                    "L|Display ledger",
                    "X|Exit");
            UserInterface.prompt("Choose an option:");

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
                UserInterface.error("Invalid input, please try again");
            }
        }
    }
}