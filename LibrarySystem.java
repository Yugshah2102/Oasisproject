import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibrarySystem extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int loginAttempts = 0;

    // JDBC credentials
    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "yug", "yug@123");

    private static final String USER = "yug";
    private static final String PASS = "yug@123";

    public LibrarySystem() {
        setTitle("Smart Library Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 240, 255));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        JLabel title = new JLabel("Welcome to Smart Library");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.BLUE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 10);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> handleLogin());

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String query = "SELECT role FROM members WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                showDashboard(username, role);
            } else {
                loginAttempts++;
                if (loginAttempts >= 3) {
                    JOptionPane.showMessageDialog(this, "Too many failed attempts. Application will close.");
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Attempt: " + loginAttempts + "/3");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showDashboard(String user, String role) {
        JFrame dash = new JFrame(role.toUpperCase() + " Dashboard");
        dash.setSize(400, 200);
        dash.setLocationRelativeTo(null);
        dash.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel welcome = new JLabel("Welcome " + user + " (" + role + ")", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));

        dash.add(welcome);
        dash.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "JDBC Driver load error.");
        }

        SwingUtilities.invokeLater(LibrarySystem::new);
    }
}
		