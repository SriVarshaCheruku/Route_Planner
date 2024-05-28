package ui;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPage(Frame parent) {
        super(parent, "Login", true);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6");
                    String sql = "SELECT * FROM users WHERE username=? AND password=?";
                    PreparedStatement pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        String userType = rs.getString("user_type");
                        if (userType.equals("admin")) {
                            openAdminDashboard();
                        } else {
                            insertUserLog(con, userId, username);
                            openLocationsFrame(userId, username);
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect username and password");
                    }
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(loginButton, constraints);

        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(panel);
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
        MainFrame mainFrame = (MainFrame) getParent();
        mainFrame.handleSuccessfulLogin(userId, username);
        dispose();
    }

    private void openAdminDashboard() {
    	MainFrame mainFrame = (MainFrame) getParent();
        new AdminDashboard().setVisible(true);
        mainFrame.dispose(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Frame parent = new JFrame();
            new LoginPage(parent);
        });
    }
}
