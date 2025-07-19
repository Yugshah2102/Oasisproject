import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ATMMachine extends JFrame {
    private JTextField cardField;
    private JPasswordField pinField;
    private JLabel statusLabel;
    private static Connection connection;
    private String loggedInCard;

    public ATMMachine() {
        setTitle("ATM Machine");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        cardField = new JTextField();
        pinField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        statusLabel = new JLabel("Enter your card number and PIN", SwingConstants.CENTER);

        add(new JLabel("Card Number:"));
        add(cardField);
        add(new JLabel("PIN:"));
        add(pinField);
        add(loginButton);
        add(statusLabel);

        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String card = cardField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();

        if (authenticateUser(card, pin)) {
            statusLabel.setText("Login successful!");
            loggedInCard = card;
            showDashboard();
        } else {
            statusLabel.setText("Invalid credentials. Try again.");
        }
    }

    private boolean authenticateUser(String card, String pin) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE card_number=? AND pin=?");
            stmt.setString(1, card);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showDashboard() {
        JFrame dashboard = new JFrame("Dashboard");
        dashboard.setSize(400, 300);
        dashboard.setLocationRelativeTo(null);
        dashboard.setLayout(new GridLayout(4, 1));

        JButton checkBalanceBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton logoutBtn = new JButton("Logout");

        checkBalanceBtn.addActionListener(e -> checkBalance());
        depositBtn.addActionListener(e -> depositAmount());
        withdrawBtn.addActionListener(e -> withdrawAmount());
        logoutBtn.addActionListener(e -> {
            dashboard.dispose();
            setVisible(true);
        });

        dashboard.add(checkBalanceBtn);
        dashboard.add(depositBtn);
        dashboard.add(withdrawBtn);
        dashboard.add(logoutBtn);

        setVisible(false);
        dashboard.setVisible(true);
    }

    private void checkBalance() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT balance FROM users WHERE card_number=?");
            stmt.setString(1, loggedInCard);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double bal = rs.getDouble("balance");
                JOptionPane.showMessageDialog(this, "Your Balance: ₹" + bal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void depositAmount() {
        String amtStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        try {
            double amt = Double.parseDouble(amtStr);
            PreparedStatement stmt = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE card_number=?");
            stmt.setDouble(1, amt);
            stmt.setString(2, loggedInCard);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "₹" + amt + " deposited successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        }
    }

    private void withdrawAmount() {
        String amtStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        try {
            double amt = Double.parseDouble(amtStr);
            PreparedStatement check = connection.prepareStatement("SELECT balance FROM users WHERE card_number=?");
            check.setString(1, loggedInCard);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                double current = rs.getDouble("balance");
                if (amt <= current) {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE card_number=?");
                    stmt.setDouble(1, amt);
                    stmt.setString(2, loggedInCard);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "₹" + amt + " withdrawn.");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    private static void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/atm_db", "root", "your_mysql_password"
            );
            System.out.println("Connected to DB.");
        } catch (Exception e) {
            System.err.println("Failed to connect to DB.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connectToDB();
        SwingUtilities.invokeLater(() -> new ATMMachine());
    }
}
