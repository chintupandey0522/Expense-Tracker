import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │           PERSONAL EXPENSE TRACKER  – Module 9             │
 * │           Lab Class 12 : Innovative Idea Development        │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Features:
 *   a) Accept and store daily expense details
 *   b) Display all recorded expenses
 *   c) Generate a monthly expense report
 *   d) Identify the highest expense category
 *   e) Save / Load data using file handling
 *   f) Tested with multiple pre-loaded entries
 */
public class ExpenseTrackerApp {

    private static final Scanner sc      = new Scanner(System.in);
    private static final ExpenseManager mgr = new ExpenseManager();
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ── Entry Point ────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        printBanner();

        // f) Pre-load demo data so the app can be tested immediately
        loadDemoData();

        // Attempt to restore any previously saved expenses
        System.out.println("\n  Checking for saved data...");
        mgr.loadFromFile();

        // Main menu loop
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("  Enter your choice: ");

            switch (choice) {
                case 1 -> addExpenseInteractive();
                case 2 -> mgr.displayAllExpenses();
                case 3 -> generateMonthlyReportInteractive();
                case 4 -> mgr.displayHighestExpenseCategory();
                case 5 -> mgr.saveToFile();
                case 6 -> mgr.loadFromFile();
                case 7 -> { running = false; exitGracefully(); }
                default -> System.out.println("  [!] Invalid option. Please choose 1–7.\n");
            }
        }
    }

    // ── Menu Helpers ───────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("""
                
                ╔══════════════════════════════════════════════════════════╗
                ║          PERSONAL EXPENSE TRACKER  v1.0                 ║
                ║          Module 9 | Lab Class 12                        ║
                ╚══════════════════════════════════════════════════════════╝
                """);
    }

    private static void printMenu() {
        System.out.println("""
                  ┌───────────────────────────────────────┐
                  │            MAIN MENU                  │
                  ├───────────────────────────────────────┤
                  │  1. Add New Expense                   │
                  │  2. Display All Expenses              │
                  │  3. Generate Monthly Report           │
                  │  4. Highest Expense Category          │
                  │  5. Save Expenses to File             │
                  │  6. Load Expenses from File           │
                  │  7. Exit                              │
                  └───────────────────────────────────────┘""");
    }

    // ── a) Add Expense Interactively ───────────────────────────────────────────

    private static void addExpenseInteractive() {
        System.out.println("\n  ── Add New Expense ─────────────────────────────");

        // Amount
        double amount = 0;
        while (amount <= 0) {
            amount = readDouble("  Enter amount (Rs.): ");
            if (amount <= 0) System.out.println("  [!] Amount must be greater than zero.");
        }

        // Category
        System.out.println("  Available categories:");
        for (int i = 0; i < Expense.VALID_CATEGORIES.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, Expense.VALID_CATEGORIES[i]);
        }
        String category = null;
        while (category == null) {
            System.out.print("  Enter category name: ");
            String input = sc.nextLine().trim();
            if (Expense.isValidCategory(input)) {
                // Normalise to Title Case from the canonical list
                for (String c : Expense.VALID_CATEGORIES) {
                    if (c.equalsIgnoreCase(input)) { category = c; break; }
                }
            } else {
                System.out.println("  [!] Invalid category. Choose from the list above.");
            }
        }

        // Date
        LocalDate date = null;
        while (date == null) {
            System.out.print("  Enter date (dd-MM-yyyy) [press Enter for today]: ");
            String raw = sc.nextLine().trim();
            if (raw.isEmpty()) {
                date = LocalDate.now();
            } else {
                try {
                    date = LocalDate.parse(raw, DATE_FMT);
                } catch (DateTimeParseException e) {
                    System.out.println("  [!] Invalid date format. Use dd-MM-yyyy.");
                }
            }
        }

        // Description
        System.out.print("  Enter description: ");
        String desc = sc.nextLine().trim();
        if (desc.isEmpty()) desc = "(no description)";

        // Commit
        boolean added = mgr.addExpense(amount, category, date, desc);
        if (added) {
            System.out.printf("  [✓] Expense of Rs.%.2f added under '%s'.%n%n",
                              amount, category);
        }
    }

    // ── c) Monthly Report Interactive ─────────────────────────────────────────

    private static void generateMonthlyReportInteractive() {
        System.out.println("\n  ── Monthly Report ──────────────────────────────");

        int month = 0;
        while (month < 1 || month > 12) {
            month = readInt("  Enter month (1-12): ");
            if (month < 1 || month > 12)
                System.out.println("  [!] Month must be between 1 and 12.");
        }

        int year = 0;
        while (year < 2000 || year > 2100) {
            year = readInt("  Enter year (e.g. 2024): ");
            if (year < 2000 || year > 2100)
                System.out.println("  [!] Please enter a valid year (2000-2100).");
        }

        mgr.generateMonthlyReport(month, year);
    }

    // ── f) Demo Data (pre-loaded for testing) ──────────────────────────────────

    /**
     * Inserts 12 sample expenses covering two months and multiple categories
     * so every feature can be demonstrated right after launch.
     */
    private static void loadDemoData() {
        System.out.println("  Loading demo data for testing...");

        Object[][] data = {
            // {amount, category, "dd-MM-yyyy", description}
            {450.00,  "Food",          "01-11-2024", "Restaurant dinner"},
            {120.00,  "Transport",     "02-11-2024", "Bus & auto fare"},
            {1800.00, "Shopping",      "05-11-2024", "New shirt & jeans"},
            {600.00,  "Health",        "08-11-2024", "Doctor consultation"},
            {250.00,  "Entertainment", "10-11-2024", "Movie tickets"},
            {300.00,  "Food",          "15-11-2024", "Groceries"},
            {90.00,   "Transport",     "18-11-2024", "Cab ride"},
            {500.00,  "Education",     "20-11-2024", "Online course book"},
            {1200.00, "Utilities",     "25-11-2024", "Electricity bill"},
            {350.00,  "Food",          "03-12-2024", "Lunch & dinner"},
            {2500.00, "Shopping",      "10-12-2024", "Winter jacket"},
            {400.00,  "Entertainment", "20-12-2024", "OTT subscription"},
        };

        int count = 0;
        for (Object[] row : data) {
            try {
                LocalDate date = LocalDate.parse((String) row[2], DATE_FMT);
                boolean ok = mgr.addExpense(
                        (double) row[0], (String) row[1], date, (String) row[3]);
                if (ok) count++;
            } catch (Exception ignored) {}
        }
        System.out.printf("  [✓] %d demo expense(s) loaded.%n", count);
    }

    // ── Exit ───────────────────────────────────────────────────────────────────

    private static void exitGracefully() {
        System.out.print("\n  Save expenses before exiting? (y/n): ");
        String ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("y")) mgr.saveToFile();
        System.out.println("\n  Thank you for using Personal Expense Tracker. Goodbye!\n");
    }

    // ── Input Utilities ────────────────────────────────────────────────────────

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number.");
            }
        }
    }
}
