package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class AdminDashboard extends JFrame {
    private JComboBox<String> locationComboBox;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Log", createUserLogPanel());
        tabbedPane.addTab("Locations and Places", createLocationsPlacesPanel());
        tabbedPane.addTab("Guides", createGuidesPanel());
        tabbedPane.addTab("Bookings", createBookingsPanel());

        add(tabbedPane, BorderLayout.CENTER);
        JButton backButton = new JButton("SignOut");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                setVisible(false);

                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
        backButton.setPreferredSize(new Dimension(100, backButton.getPreferredSize().height));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createUserLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable userLogTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (row % 2 == 0) {
                    comp.setBackground(Color.WHITE);
                } else {
                    comp.setBackground(new Color(240, 240, 240));
                }
                return comp;
            }
        };

        tableModel.addColumn("Username");
        tableModel.addColumn("Login Time");
        tableModel.addColumn("Logout Time");

        userLogTable.getTableHeader().setReorderingAllowed(false);
        userLogTable.getTableHeader().setResizingAllowed(false);
        userLogTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        userLogTable.getTableHeader().setFont(userLogTable.getFont().deriveFont(Font.BOLD)); 
        userLogTable.getTableHeader().setForeground(Color.BLACK); 

        userLogTable.setDefaultEditor(Object.class, null); 

        userLogTable.setSelectionForeground(Color.BLACK);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
            String query = "SELECT username, login_time, logout_time FROM userLog";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getString("username"),
                        resultSet.getTimestamp("login_time"),
                        resultSet.getTimestamp("logout_time")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch user log data from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(userLogTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLocationsPlacesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel addLocationPanel = new JPanel(new GridBagLayout());
        addLocationPanel.setBorder(BorderFactory.createTitledBorder("Add Location"));

        JTextField locationNameField = new JTextField(10);
        JButton addLocationButton = new JButton("Add Location");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        addLocationPanel.add(new JLabel("Location Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addLocationPanel.add(locationNameField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        addLocationButton.setPreferredSize(new Dimension(120, addLocationButton.getPreferredSize().height)); // Increase button width
        addLocationPanel.add(addLocationButton, gbc);

        addLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String locationName = locationNameField.getText();
                if (locationName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a location name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
                    String query = "INSERT INTO locations (name) VALUES (?)";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, locationName);
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Location added: " + locationName);
                        locationNameField.setText(""); 
                        refreshLocationComboBox();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to add location: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel addPlacePanel = new JPanel(new GridBagLayout());
        addPlacePanel.setBorder(BorderFactory.createTitledBorder("Add Place"));

        locationComboBox = new JComboBox<>();
        refreshLocationComboBox();

        JTextField placeNameField = new JTextField(10);
        JTextField latitudeField = new JTextField(10);
        JTextField longitudeField = new JTextField(10);
        JTextArea descriptionArea = new JTextArea(3, 10);

        JButton addPlaceButton = new JButton("Add Place");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        addPlacePanel.add(new JLabel("Location Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPlacePanel.add(locationComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addPlacePanel.add(new JLabel("Place Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPlacePanel.add(placeNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addPlacePanel.add(new JLabel("Latitude:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPlacePanel.add(latitudeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addPlacePanel.add(new JLabel("Longitude:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPlacePanel.add(longitudeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addPlacePanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridheight = 2; 
        gbc.fill = GridBagConstraints.BOTH; 
        descriptionArea.setLineWrap(true); 
        descriptionArea.setWrapStyleWord(true); 
        addPlacePanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; 
        addPlaceButton.setPreferredSize(new Dimension(100, addPlaceButton.getPreferredSize().height));
        addPlacePanel.add(addPlaceButton, gbc);

        addPlaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String locationName = (String) locationComboBox.getSelectedItem();
                String placeName = placeNameField.getText();
                String latitude = latitudeField.getText();
                String longitude = longitudeField.getText();
                String description = descriptionArea.getText();
                if (locationName == null || locationName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a location.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                if (placeName.isEmpty() || latitude.isEmpty() || longitude.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
                    String locationQuery = "SELECT id FROM locations WHERE name = ?";
                    PreparedStatement locationStatement = conn.prepareStatement(locationQuery);
                    locationStatement.setString(1, locationName);
                    ResultSet locationResultSet = locationStatement.executeQuery();
                    int locationId = -1;
                    if (locationResultSet.next()) {
                        locationId = locationResultSet.getInt("id");
                    }

                    if (locationId != -1) {
                    	String placeQuery = "INSERT INTO places (location_id, name, latitude, longitude, description) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement placeStatement = conn.prepareStatement(placeQuery);
                        placeStatement.setInt(1, locationId);
                        placeStatement.setString(2, placeName);
                        placeStatement.setString(3, latitude);
                        placeStatement.setString(4, longitude);
                        placeStatement.setString(5, description);
                        int rowsInserted = placeStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(null, "Place added: " + placeName);
                            placeNameField.setText(""); 
                            latitudeField.setText("");
                            longitudeField.setText("");
                            descriptionArea.setText("");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add place. Location ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to add place: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(addLocationPanel, BorderLayout.NORTH);
        panel.add(addPlacePanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshLocationComboBox() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
            String query = "SELECT name FROM locations";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            locationComboBox.removeAllItems(); 

            while (resultSet.next()) {
                String locationName = resultSet.getString("name");
                locationComboBox.addItem(locationName); 
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch locations from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createGuidesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add Guide"));

        JTextField nameField = new JTextField(20);
        JTextField contactField = new JTextField(20);
        JTextField costField = new JTextField(10);
        JComboBox<String> locationComboBox = new JComboBox<>(); 
        JTextArea descriptionArea = new JTextArea(5, 20);

        JButton addButton = new JButton("Add Guide");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Cost:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        gbc.gridy++;
        panel.add(contactField, gbc);
        gbc.gridy++;
        panel.add(costField, gbc);
        gbc.gridy++;
        panel.add(locationComboBox, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descriptionArea), gbc);

        populateLocationComboBox(locationComboBox);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        addButton.setPreferredSize(new Dimension(120, addButton.getPreferredSize().height));
        panel.add(addButton, gbc);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String costStr = costField.getText().trim();
                String description = descriptionArea.getText().trim();
                String location = (String) locationComboBox.getSelectedItem();

                if (name.isEmpty() || contact.isEmpty() || costStr.isEmpty() || description.isEmpty() || location == null) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double cost = Double.parseDouble(costStr);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
                    String query = "INSERT INTO guides (name, contact, cost, location_id, description) VALUES (?, ?, ?, (SELECT id FROM locations WHERE name = ?), ?)";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, name);
                    statement.setString(2, contact);
                    statement.setDouble(3, cost);
                    statement.setString(4, location);
                    statement.setString(5, description);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Guide added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        nameField.setText("");
                        contactField.setText("");
                        costField.setText("");
                        descriptionArea.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add guide.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void populateLocationComboBox(JComboBox<String> comboBox) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
            String query = "SELECT name FROM locations";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String locationName = resultSet.getString("name");
                comboBox.addItem(locationName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch locations from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        JTable bookingsTable = new JTable(tableModel);
        bookingsTable.getTableHeader().setDefaultRenderer(new BookingTableHeaderRenderer());
        bookingsTable.setDefaultEditor(Object.class, null); 
        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        tableModel.addColumn("Username");
        tableModel.addColumn("Guide Name");
        tableModel.addColumn("Guide Cost");
        tableModel.addColumn("Booking Time");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6")) {
            String query = "SELECT username, guide_name, guide_cost, booking_time FROM booking";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getString("username"),
                        resultSet.getString("guide_name"),
                        resultSet.getDouble("guide_cost"),
                        resultSet.getTimestamp("booking_time")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch booking details from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private class BookingTableHeaderRenderer extends DefaultTableCellRenderer {
        private Border border = BorderFactory.createLineBorder(Color.BLACK);

        public BookingTableHeaderRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setBackground(Color.LIGHT_GRAY); 
            setForeground(Color.BLACK);
            setBorder(border); 
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private class BookingTableCellRenderer extends DefaultTableCellRenderer {
        private Border border;

        public BookingTableCellRenderer() {
            border = BorderFactory.createCompoundBorder(
                    new LineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5) 
            );
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 0 || column == 1 || column == 2 || column == 3) {
                ((JLabel) cellComponent).setBorder(border);
            } else {
                ((JLabel) cellComponent).setBorder(null); 
            }

            return cellComponent;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.setVisible(true);
        });
    }
}