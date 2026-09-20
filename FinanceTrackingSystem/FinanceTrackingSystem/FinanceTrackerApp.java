package FinanceTrackingSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. TRANSACTION LOGIC MODEL
// ==========================================
class Transaction {
    private String id;
    private LocalDate date;
    private String description;
    private String category;
    private double amount;
    private String type;

    public Transaction(String id, LocalDate date, String description, String category, double amount, String type) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.type = type.toUpperCase();
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}

// ==========================================
// 2. FINANCE MANAGER LOGIC CLASS
// ==========================================
class FinanceManager {
    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(LocalDate date, String category, String description, double amount, String type) {
        int i = transactions.size();
        String uniqueId = "TXN" + (i + 1);
        Transaction t = new Transaction(uniqueId, date, description, category, amount, type);
        transactions.add(t);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getTotalIncome() {
        return transactions.stream().filter(t -> t.getType().equals("INCOME")).mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return transactions.stream().filter(t -> t.getType().equals("EXPENSE")).mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getNetBalance() {
        return getTotalIncome() - getTotalExpenses();
    }
}

// ==========================================
// 3. MAIN RUNTIME NATIVE DESKTOP UI APP
// ==========================================
public class FinanceTrackerApp extends JFrame {
    private static final FinanceManager manager = new FinanceManager();

    // UI Elements
    private JLabel lblBalance, lblIncome, lblExpense;
    private JTextField txtDescription, txtAmount, txtCategory;
    private JComboBox<String> cmbType;
    private DefaultTableModel tableModel;

    public FinanceTrackerApp() {
        // Window Configuration
        setTitle("Personal Finance Tracking System");
        setSize(550, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Dashboard Metrics Summary Panel
        JPanel pnlSummary = new JPanel(new GridLayout(1, 3, 10, 10));
        pnlSummary.setBorder(BorderFactory.createTitledBorder("Financial Summary"));

        lblBalance = new JLabel("$0.00", SwingConstants.CENTER);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIncome = new JLabel("$0.00", SwingConstants.CENTER);
        lblIncome.setForeground(new Color(22, 163, 74));
        lblExpense = new JLabel("$0.00", SwingConstants.CENTER);
        lblExpense.setForeground(new Color(220, 38, 38));

        pnlSummary.add(createMetricCard("Net Balance", lblBalance));
        pnlSummary.add(createMetricCard("Total Income", lblIncome));
        pnlSummary.add(createMetricCard("Total Expenses", lblExpense));

        // 2. Form Input Panel
        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 8, 8));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Add New Transaction"));

        txtDescription = new JTextField();
        txtAmount = new JTextField();
        txtCategory = new JTextField();
        cmbType = new JComboBox<>(new String[] { "INCOME", "EXPENSE" });
        JButton btnSubmit = new JButton("Execute Transaction");
        btnSubmit.setBackground(new Color(15, 23, 42));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));

        pnlForm.add(new JLabel(" Description:"));
        pnlForm.add(txtDescription);
        pnlForm.add(new JLabel(" Amount ($):"));
        pnlForm.add(txtAmount);
        pnlForm.add(new JLabel(" Category:"));
        pnlForm.add(txtCategory);
        pnlForm.add(new JLabel(" Type:"));
        pnlForm.add(cmbType);
        pnlForm.add(new JLabel(""));
        pnlForm.add(btnSubmit);

        // 3. Ledger History Table Panel
        String[] columns = { "ID", "Date", "Description", "Category", "Amount", "Type" };
        tableModel = new DefaultTableModel(columns, 0);
        JTable tblLedger = new JTable(tableModel);
        JScrollPane pnlTable = new JScrollPane(tblLedger);
        pnlTable.setBorder(BorderFactory.createTitledBorder("Transaction History Ledger"));

        // Action Listener Event for Submitting Inputs
        btnSubmit.addActionListener(e -> {
            try {
                String desc = txtDescription.getText().trim();
                String cat = txtCategory.getText().trim();
                String type = cmbType.getSelectedItem().toString();
                double amt = Double.parseDouble(txtAmount.getText().trim());

                if (desc.isEmpty() || cat.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All input fields must be filled out!", "Validation Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Call your exact manager logic inside memory
                manager.addTransaction(LocalDate.now(), cat, desc, amt, type);

                // Reset fields and recalculate values
                txtDescription.setText("");
                txtAmount.setText("");
                txtCategory.setText("");
                refreshDashboard();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for the Amount.",
                        "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Assemble Layout Parts
        JPanel pnlTop = new JPanel(new BorderLayout(5, 5));
        pnlTop.add(pnlSummary, BorderLayout.NORTH);
        pnlTop.add(pnlForm, BorderLayout.CENTER);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlTable, BorderLayout.CENTER);

        // Seed with baseline dataset
        manager.addTransaction(LocalDate.of(2026, 9, 1), "Salary", "Monthly Paycheck", 4500.00, "INCOME");
        manager.addTransaction(LocalDate.of(2026, 9, 3), "Housing", "Apartment Rent", 1200.00, "EXPENSE");
        manager.addTransaction(LocalDate.of(2026, 9, 5), "Groceries", "Weekly Food Run", 154.20, "EXPENSE");

        refreshDashboard();
    }

    private JPanel createMetricCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void refreshDashboard() {
        // Hydrate Metrics
        lblIncome.setText(String.format("$%.2f", manager.getTotalIncome()));
        lblExpense.setText(String.format("$%.2f", manager.getTotalExpenses()));

        double net = manager.getNetBalance();
        lblBalance.setText(String.format("$%.2f", net));
        if (net >= 0) {
            lblBalance.setForeground(new Color(22, 163, 74));
        } else {
            lblBalance.setForeground(new Color(220, 38, 38));
        }

        // Hydrate Table List Rows
        tableModel.setRowCount(0);
        for (Transaction t : manager.getTransactions()) {
            tableModel.addRow(new Object[] {
                    t.getId(), t.getDate(), t.getDescription(), t.getCategory(),
                    String.format("$%.2f", t.getAmount()), t.getType()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FinanceTrackerApp().setVisible(true);
        });
    }
}
