package expensetracker;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private List<Expense> expenses = new ArrayList<>();
    private static final String DATA_FILE = "expenses.dat";

    public void addExpense(Expense e) {
        expenses.add(e);
    }

    public void removeExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
        }
    }

    public List<Expense> getAllExpenses() {
        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return sorted;
    }

    public List<Expense> getExpensesByMonth(int year, int month) {
        return expenses.stream()
            .filter(e -> e.getDate().getYear() == year && e.getDate().getMonthValue() == month)
            .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
            .collect(Collectors.toList());
    }

    public double getTotalAmount() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public double getMonthlyTotal(int year, int month) {
        return getExpensesByMonth(year, month).stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getCategoryTotals() {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (String cat : Expense.CATEGORIES) totals.put(cat, 0.0);
        for (Expense e : expenses) {
            totals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return totals;
    }

    public Map<String, Double> getMonthlyCategoryTotals(int year, int month) {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (String cat : Expense.CATEGORIES) totals.put(cat, 0.0);
        for (Expense e : getExpensesByMonth(year, month)) {
            totals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return totals;
    }

    public String getHighestCategory() {
        return getCategoryTotals().entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }

    public String getHighestCategoryForMonth(int year, int month) {
        return getMonthlyCategoryTotals(year, month).entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }

    public List<String> getAvailableMonths() {
        return expenses.stream()
            .map(Expense::getMonthYear)
            .distinct()
            .collect(Collectors.toList());
    }

    public int getCount() { return expenses.size(); }

    // File handling
    public void saveToFile() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(expenses);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() throws IOException, ClassNotFoundException {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            expenses = (List<Expense>) ois.readObject();
        }
    }

    public boolean dataFileExists() {
        return new File(DATA_FILE).exists();
    }
}
