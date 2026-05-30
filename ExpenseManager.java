import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all expense operations:
 *  - Add / display expenses
 *  - Monthly reports
 *  - Highest-spending category
 *  - File save & load (Java object serialization)
 */
public class ExpenseManager {

    private List<Expense> expenses;
    private static final String FILE_PATH = "expenses.dat";
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ── Constructor ────────────────────────────────────────────────────────────

    public ExpenseManager() {
        expenses = new ArrayList<>();
    }

    // ── a) Add Expense ─────────────────────────────────────────────────────────

    /**
     * Validates input then appends a new Expense to the in-memory list.
     *
     * @return true if the expense was added successfully, false on validation error
     */
    public boolean addExpense(double amount, String category,
                              LocalDate date, String description) {
        try {
            Expense e = new Expense(amount, category, date, description);
            expenses.add(e);
            return true;
        } catch (IllegalArgumentException ex) {
            System.out.println("  [ERROR] " + ex.getMessage());
            return false;
        }
    }

    // ── b) Display All Expenses ────────────────────────────────────────────────

    /** Prints every recorded expense in a formatted table. */
    public void displayAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("\n  No expenses recorded yet.");
            return;
        }

        printTableHeader();
        for (Expense e : expenses) {
            System.out.println("  " + e);
        }
        printTableFooter();

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        System.out.printf("  %-40s Total: Rs.%.2f%n", "", total);
        System.out.println();
    }

    // ── c) Monthly Expense Report ──────────────────────────────────────────────

    /**
     * Filters expenses by month/year, groups them by category,
     * and prints a summary report.
     *
     * @param month 1-12
     * @param year  e.g. 2024
     */
    public void generateMonthlyReport(int month, int year) {
        List<Expense> monthly = expenses.stream()
                .filter(e -> e.getDate().getMonthValue() == month
                          && e.getDate().getYear()       == year)
                .collect(Collectors.toList());

        String monthName = Month.of(month).name();
        System.out.println("\n  ══════════════════════════════════════════════════");
        System.out.printf ("  Monthly Report: %s %d%n", monthName, year);
        System.out.println("  ══════════════════════════════════════════════════");

        if (monthly.isEmpty()) {
            System.out.println("  No expenses found for this month.");
            return;
        }

        // Group by category
        Map<String, Double> byCategory = new LinkedHashMap<>();
        for (String cat : Expense.VALID_CATEGORIES) {
            byCategory.put(cat, 0.0);
        }
        for (Expense e : monthly) {
            byCategory.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        System.out.printf("  %-16s %s%n", "Category", "Amount (Rs.)");
        System.out.println("  ──────────────────────────────────────────────────");

        double total = 0;
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.printf("  %-16s Rs.%.2f%n",
                        entry.getKey(), entry.getValue());
                total += entry.getValue();
            }
        }

        System.out.println("  ──────────────────────────────────────────────────");
        System.out.printf ("  %-16s Rs.%.2f%n", "TOTAL", total);
        System.out.printf ("  Total Transactions: %d%n", monthly.size());
        System.out.println("  ══════════════════════════════════════════════════\n");
    }

    // ── d) Highest Expense Category ────────────────────────────────────────────

    /** Computes and displays the category with the highest total spending. */
    public void displayHighestExpenseCategory() {
        if (expenses.isEmpty()) {
            System.out.println("\n  No expenses recorded yet.");
            return;
        }

        // Aggregate totals per category
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : expenses) {
            totals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        // Find the max entry
        Map.Entry<String, Double> highest = Collections.max(
                totals.entrySet(), Map.Entry.comparingByValue());

        System.out.println("\n  ── Spending Analysis ────────────────────────────");
        System.out.printf ("  %-16s %s%n", "Category", "Total (Rs.)");
        System.out.println("  ─────────────────────────────────────────────────");

        // Sort descending for a leaderboard view
        totals.entrySet().stream()
              .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
              .forEach(e -> System.out.printf("  %-16s Rs.%.2f%n",
                                              e.getKey(), e.getValue()));

        System.out.println("  ─────────────────────────────────────────────────");
        System.out.printf ("  Highest Spending Category: %s (Rs.%.2f)%n",
                           highest.getKey(), highest.getValue());
        System.out.println("  ─────────────────────────────────────────────────\n");
    }

    // ── e) File Handling: Save ─────────────────────────────────────────────────

    /**
     * Serializes the expense list to a binary file.
     * Uses Java object serialization (ObjectOutputStream).
     */
    public void saveToFile() {
        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(expenses);
            System.out.println("  [OK] Expenses saved to '" + FILE_PATH + "'.");
        } catch (IOException ex) {
            System.out.println("  [ERROR] Could not save file: " + ex.getMessage());
        }
    }

    // ── e) File Handling: Load ─────────────────────────────────────────────────

    /**
     * Deserializes the expense list from the binary file.
     * Merges loaded data with any in-memory entries.
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            System.out.println("  [INFO] No saved data found ('" + FILE_PATH + "').");
            return;
        }
        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<Expense> loaded = (List<Expense>) ois.readObject();
            expenses.addAll(loaded);
            System.out.println("  [OK] Loaded " + loaded.size()
                             + " expense(s) from '" + FILE_PATH + "'.");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("  [ERROR] Could not load file: " + ex.getMessage());
        }
    }

    // ── Helper: Total Count ────────────────────────────────────────────────────

    public int getTotalExpenses() {
        return expenses.size();
    }

    // ── Private Helpers ────────────────────────────────────────────────────────

    private void printTableHeader() {
        System.out.println("\n  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║               ALL RECORDED EXPENSES                         ║");
        System.out.println("  ╠════════════╦════════════════╦════════════════╦═══════════════╣");
        System.out.printf ("  ║ %-10s ║ %-14s ║ %-14s ║ %-13s ║%n",
                           "Date", "Category", "Amount (Rs.)", "Description");
        System.out.println("  ╠════════════╬════════════════╬════════════════╬═══════════════╣");
    }

    private void printTableFooter() {
        System.out.println("  ╚════════════╩════════════════╩════════════════╩═══════════════╝");
    }
}
