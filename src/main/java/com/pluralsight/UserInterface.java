package com.pluralsight;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    // Palette: one accent color, one muted gray, and red/green reserved for money
    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String DIM    = "\u001B[90m";
    public static final String ACCENT = "\u001B[38;5;179m";
    public static final String GREEN  = "\u001B[32m";
    public static final String RED    = "\u001B[31m";

    private static final String APP_NAME = "Melly Bank";
    private static final int WIDTH = 44;          // inner width of menu boxes
    private static final int TABLE_WIDTH = 74;    // width of transaction tables

    private UserInterface() {}

    // Call once at the start of main so box characters display correctly on Windows
    public static void init() {
        // Makes sure the characters being printed are in UTF_8 and not the default.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    // Launch screen: spaced-out bank name between two gold rules, then waits for Enter
    public static void splash(Scanner scanner) {
        String rule = ACCENT + "─".repeat(WIDTH + 2) + RESET;
        System.out.println();
        System.out.println(rule);
        System.out.println();
        System.out.println(BOLD + ACCENT + centered(spaced(APP_NAME.toUpperCase())) + RESET);
        System.out.println(DIM + centered(spaced("ACCOUNTING LEDGER")) + RESET);
        System.out.println();
        System.out.println(rule);
        System.out.println();
        System.out.println(DIM + centered("Press Enter to launch") + RESET);
        scanner.nextLine();
    }

    // Menu box. Each option is written as "KEY|Label", for example "D|Add deposit"
    public static void menu(String title, String... options) {
        String bar = "─".repeat(WIDTH);
        System.out.println();
        System.out.println(DIM + "┌" + bar + "┐" + RESET);

        String plainTitle = " " + APP_NAME + "  ·  " + title;
        System.out.println(row(" " + BOLD + ACCENT + APP_NAME + RESET + DIM + "  ·  " + RESET + BOLD + title + RESET,
                plainTitle.length()));

        System.out.println(DIM + "├" + bar + "┤" + RESET);
        for (String option : options) {
            String[] parts = option.split("\\|", 2);
            String colored = "  " + DIM + "[" + ACCENT + parts[0] + DIM + "]" + RESET + "  " + parts[1];
            int plainLength = 2 + 1 + parts[0].length() + 1 + 2 + parts[1].length();
            System.out.println(row(colored, plainLength));
        }
        System.out.println(DIM + "└" + bar + "┘" + RESET);
    }

    // Aligned transaction table with green deposits and red payments
    public static void transactions(String title, List<Transaction> list) {
        System.out.println();
        System.out.println(BOLD + title + RESET);
        System.out.println(DIM + "─".repeat(TABLE_WIDTH) + RESET);

        if (list.isEmpty()) {
            System.out.println(DIM + "No transactions found." + RESET);
            return;
        }

        System.out.println(DIM + String.format("%-10s  %-8s  %-22s  %-16s  %10s",
                "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT") + RESET);
        for (Transaction t : list) {
            String color = t.getAmount() >= 0 ? GREEN : RED;
            String amount = String.format("%+,10.2f", t.getAmount());
            System.out.println(String.format("%-10s  %-8s  %-22s  %-16s  ",
                    t.getDate(), t.getTime(), cut(t.getDesc(), 22), cut(t.getVendor(), 16))
                    + color + amount + RESET);
        }
        System.out.println(DIM + "─".repeat(TABLE_WIDTH) + RESET);
        System.out.println(DIM + list.size() + (list.size() == 1 ? " transaction" : " transactions") + RESET);
    }

    // Section title for screens that are not tables, such as the add and search screens
    public static void section(String title) {
        System.out.println();
        System.out.println(BOLD + title + RESET);
        System.out.println(DIM + "─".repeat(WIDTH) + RESET);
    }

    public static void prompt(String text) {
        System.out.print(ACCENT + "› " + RESET + text + " ");
    }

    // Dim helper text, such as tips
    public static void hint(String text) {
        System.out.println(DIM + text + RESET);
    }

    public static void success(String message) {
        System.out.println(GREEN + "✔ " + message + RESET);
    }

    public static void error(String message) {
        System.out.println(RED + "✖ " + message + RESET);
    }

    // One box row: colored content, padded using its plain (uncolored) length
    private static String row(String colored, int plainLength) {
        return DIM + "│" + RESET + colored + " ".repeat(Math.max(0, WIDTH - plainLength)) + DIM + "│" + RESET;
    }

    // "MELLY BANK" becomes "M E L L Y   B A N K" (one space between letters, three between words)
    private static String spaced(String text) {
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray()) {
            out.append(c == ' ' ? "  " : c + " ");
        }
        return out.toString().stripTrailing();
    }

    // Pads the left side so the text sits in the middle of the menu width
    private static String centered(String text) {
        return " ".repeat(Math.max(0, (WIDTH + 2 - text.length()) / 2)) + text;
    }

    private static String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "…";
    }
}