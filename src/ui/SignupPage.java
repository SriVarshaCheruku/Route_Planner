package ui;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Location.LocationFrame;

public class SignupPage extends JDialog {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton signupButton;
    private JButton cancelButton;

    public SignupPage(Frame parent) {
        super(parent, "Sign Up", true);
        JLabel firstNameLabel = new JLabel("  First Name:");
        firstNameField = new JTextField(14);

        JLabel lastNameLabel = new JLabel("  Last Name:");
        lastNameField = new JTextField(14);

        JLabel usernameLabel = new JLabel("  Username:");
        usernameField = new JTextField(14);

        JLabel passwordLabel = new JLabel("  Password:");
        passwordField = new JPasswordField(14);

        JLabel emailLabel = new JLabel("  Email:");
        emailField = new JTextField(14);

        JLabel phoneLabel = new JLabel("  Phone:");
        phoneField = new JTextField(14);

        signupButton = new JButton("Sign Up");
        cancelButton = new JButton("Cancel");

        setLayout(new GridLayout(5, 2));

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(emailLabel);
        add(emailField);
        add(phoneLabel);
        add(phoneField);
        add(signupButton);
        add(cancelButton);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String phone = phoneField.getText();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(SignupPage.this, "All fields are required.");
                    return;
                }
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
                    String sql = "INSERT INTO users (username, password, email, phonenumber) VALUES (?, ?, ?, ? )";
                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, email);
                    stmt.setString(4, phone);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            int userId = rs.getInt(1);
                            insertUserLog(conn, userId, username); 
                            openLocationsFrame(userId, username); 
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Error: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setSize(300, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void insertUserLog(Connection con, int userId, String username) throws SQLException {
        String sql = "INSERT INTO UserLog (user_id, username) VALUES (?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.setString(2, username);
        pstmt.executeUpdate();
    }

    private void openLocationsFrame(int userId, String username) {
        LocationFrame locationFrame = new LocationFrame(userId, username);
        locationFrame.setVisible(true);
        dispose();
    }
}