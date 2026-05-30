package expensetracker;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;

    private double amount;
    private String category;
    private LocalDate date;
    private String description;

    public static final String[] CATEGORIES = {
        "Food & Dining", "Transportation", "Shopping", "Entertainment",
        "Health & Medical", "Bills & Utilities", "Education", "Travel", "Other"
    };

    public Expense(double amount, String category, LocalDate date, String description) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public double getAmount()      { return amount; }
    public String getCategory()    { return category; }
    public LocalDate getDate()     { return date; }
    public String getDescription() { return description; }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public String getMonthYear() {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    @Override
    public String toString() {
        return String.format("₹%.2f | %s | %s | %s", amount, category, getFormattedDate(), description);
    }
}
