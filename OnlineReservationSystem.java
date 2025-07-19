// 📦 Complete Online Reservation System with Admin Panel and Email Confirmation (Simplified)

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;
// import javax.mail.*;
// import javax.mail.internet.*;

public class OnlineReservationSystem {
    public static void main(String[] args) {
        new LoginForm();
    }
}

class LoginForm extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn;

    public LoginForm() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(100, 20, 160, 25);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 50, 80, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(100, 50, 160, 25);
        add(passField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(100, 80, 80, 25);
        add(loginBtn);

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("admin") && pass.equals("admin123")) {
                new AdminPanel();
                dispose();
            } else if (authenticate(user, pass)) {
                new ReservationForm(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        setVisible(true);
    }

    private boolean authenticate(String user, String pass) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/reservation_system", "root", "");
             PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

class ReservationForm extends JFrame {
    JTextField name, trainNo, trainName, classType, date, from, to;
    JButton insertBtn, cancelBtn;
    String username;

    public ReservationForm(String username) {
        this.username = username;
        setTitle("Reservation");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel[] labels = {
            new JLabel("Name:"), new JLabel("Train No:"), new JLabel("Train Name:"),
            new JLabel("Class Type:"), new JLabel("Date (YYYY-MM-DD):"),
            new JLabel("From:"), new JLabel("To:")
        };
        JTextField[] fields = {
            name = new JTextField(), trainNo = new JTextField(), trainName = new JTextField(),
            classType = new JTextField(), date = new JTextField(),
            from = new JTextField(), to = new JTextField()
        };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(20, 20 + i * 30, 150, 25);
            fields[i].setBounds(180, 20 + i * 30, 160, 25);
            add(labels[i]); add(fields[i]);
        }

        insertBtn = new JButton("Insert");
        insertBtn.setBounds(80, 240, 100, 25);
        add(insertBtn);

        cancelBtn = new JButton("Cancel Ticket");
        cancelBtn.setBounds(200, 240, 150, 25);
        add(cancelBtn);

        insertBtn.addActionListener(e -> insertReservation());
        cancelBtn.addActionListener(e -> new CancellationForm());
    }

    private void insertReservation() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/reservation_system", "root", "");
             PreparedStatement ps = con.prepareStatement("INSERT INTO reservations (name, train_no, train_name, class_type, journey_date, from_place, to_place) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.getText());
            ps.setString(2, trainNo.getText());
            ps.setString(3, trainName.getText());
            ps.setString(4, classType.getText());
            ps.setString(5, date.getText());
            ps.setString(6, from.getText());
            ps.setString(7, to.getText());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int pnr = rs.getInt(1);
                sendEmail(username, pnr);
                JOptionPane.showMessageDialog(this, "Reservation Successful! PNR: " + pnr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String toUser, int pnr) {
        final String from = "youremail@gmail.com";
        final String pass = "yourpassword";
        String to = toUser + "@example.com"; // Fake mapping

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Reservation Confirmed");
            message.setText("Your reservation is successful. PNR: " + pnr);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class CancellationForm extends JFrame {
    JTextField pnrField;
    JTextArea details;
    JButton searchBtn, confirmBtn;

    public CancellationForm() {
        setTitle("Cancel Ticket");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Enter PNR:");
        label.setBounds(20, 20, 100, 25);
        add(label);

        pnrField = new JTextField();
        pnrField.setBounds(120, 20, 150, 25);
        add(pnrField);

        searchBtn = new JButton("Search");
        searchBtn.setBounds(280, 20, 80, 25);
        add(searchBtn);

        details = new JTextArea();
        details.setBounds(20, 60, 340, 120);
        details.setEditable(false);
        add(details);

        confirmBtn = new JButton("Confirm Cancel");
        confirmBtn.setBounds(120, 200, 150, 25);
        add(confirmBtn);

        searchBtn.addActionListener(e -> searchPNR());
        confirmBtn.addActionListener(e -> cancelPNR());

        setVisible(true);
    }

    private void searchPNR() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/reservation_system", "root", "");
             PreparedStatement ps = con.prepareStatement("SELECT * FROM reservations WHERE pnr = ?")) {
            ps.setInt(1, Integer.parseInt(pnrField.getText()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                details.setText("PNR: " + rs.getInt("pnr") +
                    "\nName: " + rs.getString("name") +
                    "\nTrain No: " + rs.getString("train_no") +
                    "\nTrain Name: " + rs.getString("train_name") +
                    "\nClass Type: " + rs.getString("class_type") +
                    "\nJourney Date: " + rs.getDate("journey_date") +
                    "\nFrom: " + rs.getString("from_place") +
                    "\nTo: " + rs.getString("to_place"));
            } else {
                details.setText("No reservation found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelPNR() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/reservation_system", "root", "");
             PreparedStatement ps = con.prepareStatement("DELETE FROM reservations WHERE pnr = ?")) {
            ps.setInt(1, Integer.parseInt(pnrField.getText()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Ticket Cancelled Successfully");
                details.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Cancellation Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class AdminPanel extends JFrame {
    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextArea area = new JTextArea();
        area.setBounds(20, 20, 340, 200);
        add(area);

        JButton refresh = new JButton("View All Reservations");
        refresh.setBounds(100, 230, 180, 25);
        add(refresh);

        refresh.addActionListener(e -> {
            area.setText("");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/reservation_system", "root", "");
                 Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM reservations")) {
                while (rs.next()) {
                    area.append("PNR: " + rs.getInt("pnr") + ", Name: " + rs.getString("name") + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }
}
