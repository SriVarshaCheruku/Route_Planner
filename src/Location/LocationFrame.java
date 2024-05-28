package Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import ui.MainFrame;

public class LocationFrame extends JFrame {
    private JComboBox<String> locationBox;
    private JLabel background;
    private JButton submitButton;
    private JButton logoutButton;
    private BufferedImage image;
    private int userId;
    private String username;

    public LocationFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
    }

    private void initComponents() {
        setTitle("Select Location");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        try {
            image = ImageIO.read(new File("images/9C1F4F2E-332F-4A26-AAE6-3ABE3E06179F_4_5005_c.jpeg"));
            background = new JLabel(new ImageIcon(image));
            add(background);
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationBox = new JComboBox<>();
        locationBox.setPreferredSize(new Dimension(150, 30));

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLocation = (String) locationBox.getSelectedItem();
                if (selectedLocation != null && !selectedLocation.isEmpty()) {
                    Place places = new Place(selectedLocation, LocationFrame.this,userId,username);
                    places.setVisible(true);
                }
            }
        });

        logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        try {
            populateLocationBox();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        setVisible(true);
    }

    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(scaledImage));
        background.setBounds(0, 0, width, height);

        int offsetX = 240; 
        int offsetY = 60;
        int boxWidth = 150;
        int boxHeight = 45;
        int buttonWidth = 100;
        int buttonHeight = 30;

        int boxX = (width - boxWidth) / 2 + offsetX;
        int boxY = height / 3;
        locationBox.setBounds(boxX, boxY, boxWidth, boxHeight);
        background.add(locationBox);

        int submitX = boxX + 20;
        int submitY = boxY + boxHeight + 20;
        submitButton.setBounds(submitX, submitY, 100, buttonHeight); 
        background.add(submitButton);

        int logoutX = (width - buttonWidth) / 2;
        int logoutY = height - buttonHeight - 40; 
        logoutButton.setBounds(logoutX, logoutY, buttonWidth, buttonHeight);
        background.add(logoutButton);
    }

    private void populateLocationBox() throws Exception {
        String url = "jdbc:mysql://localhost:3306/RoutePlanner";
        String username = "root";
        String password = "V_sarayu6";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT name FROM locations";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    locationBox.addItem(resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error retrieving locations from the database", e);
        }
    }

    private void logout() {
        try {
            updateUserLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
        new MainFrame().setVisible(true);
    }

    private void updateUserLog() throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/RoutePlanner";
        String dbUsername = "root";
        String dbPassword = "V_sarayu6";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String sql = "UPDATE UserLog SET logout_time = CURRENT_TIMESTAMP WHERE user_id = ? AND username = ? AND logout_time IS NULL";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, this.userId);
            pstmt.setString(2, this.username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating logout time in UserLog", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LocationFrame frame = new LocationFrame(1, "defaultUser"); // Example values for testing
            frame.setVisible(true);
        });
    }
}