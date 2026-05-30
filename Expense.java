import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single expense entry.
 * Implements Serializable for file-based persistence.
 */
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    private double amount;
    private String category;
    private LocalDate date;
    private String description;

    // Valid expense categories
    public static final String[] VALID_CATEGORIES = {
        "Food", "Transport", "Entertainment", "Health",
        "Shopping", "Education", "Utilities", "Others"
    };

    /**
     * Constructs an Expense with all fields.
     */
    public Expense(double amount, String category, LocalDate date, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (!isValidCategory(category)) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        this.amount    = amount;
        this.category  = category;
        this.date      = date;
        this.description = description;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public double    getAmount()      { return amount; }
    public String    getCategory()    { return category; }
    public LocalDate getDate()        { return date; }
    public String    getDescription() { return description; }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /** Returns true if the given category string matches one of the valid ones. */
    public static boolean isValidCategory(String category) {
        for (String c : VALID_CATEGORIES) {
            if (c.equalsIgnoreCase(category)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("%-12s | %-14s | Rs.%-10.2f | %s",
                date.format(fmt), category, amount, description);
    }
}
