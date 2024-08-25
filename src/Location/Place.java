package Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Place extends JFrame {
    private Connection connection;
    private JPanel placePanel;
    private JButton generateButton;
    private JButton backButton;
    private String selectedLocation;
    private LocationFrame locationFrame;
    private List<RouteMapGenerator.Place> places;

    private int userId;
    private String username;

    public Place(String selectedLocation, LocationFrame locationFrame, int userId, String username) {
        this.selectedLocation = selectedLocation;
        this.locationFrame = locationFrame;
        this.userId = userId;
        this.username = username;

        setTitle("Route Map Generator - " + selectedLocation);
        setSize(800, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        placePanel = new JPanel();
        placePanel.setLayout(new BoxLayout(placePanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(placePanel);
        scrollPane.setPreferredSize(new Dimension(800, 400)); 
        add(scrollPane, BorderLayout.CENTER);

        generateButton = new JButton("Generate Route Map");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateRouteMap();
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToLocationFrame();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(generateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/RoutePlanner", "root", "V_sarayu6");
            loadPlaces();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database");
            System.exit(1);
        }

        pack(); 
        setSize(800, 600);
    }

    private void loadPlaces() {
        try {
            String query = "SELECT p.* FROM places p INNER JOIN locations l ON p.location_id = l.id WHERE l.name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedLocation);
            ResultSet resultSet = preparedStatement.executeQuery();

            placePanel.removeAll();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                
                //System.out.println("Retrieved place: " + name + ", Latitude: " + latitude + ", Longitude: " + longitude);
                
                String description = resultSet.getString("description");
                JPanel singlePlacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JCheckBox checkBox = new JCheckBox();
                checkBox.setActionCommand(Integer.toString(id));
                singlePlacePanel.add(checkBox);
                JLabel nameLabel = new JLabel(name);
                singlePlacePanel.add(nameLabel);
                JTextArea descriptionTextArea = new JTextArea(description);
                descriptionTextArea.setLineWrap(true);
                descriptionTextArea.setWrapStyleWord(true);
                descriptionTextArea.setEditable(false);
                descriptionTextArea.setColumns(40);
                descriptionTextArea.setRows(3);
                JScrollPane scrollPane = new JScrollPane(descriptionTextArea);
                singlePlacePanel.add(scrollPane);
                placePanel.add(singlePlacePanel);
            }
            placePanel.revalidate();
            placePanel.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading places from the database");
        }
    }

    private void generateRouteMap() {
        List<Integer> selectedPlaceIds = new ArrayList<>();
        List<RouteMapGenerator.Place> selectedPlaces = new ArrayList<>();
        try {
            for (Component component : placePanel.getComponents()) {
                if (component instanceof JPanel) {
                    JPanel placePanel = (JPanel) component;
                    for (Component innerComponent : placePanel.getComponents()) {
                        if (innerComponent instanceof JCheckBox) {
                            JCheckBox checkBox = (JCheckBox) innerComponent;
                            if (checkBox.isSelected()) {
                                int placeID = Integer.parseInt(checkBox.getActionCommand());
                                selectedPlaceIds.add(placeID);
                            }
                        }
                    }
                }
            }

            selectedPlaces = getSelectedPlaces(selectedPlaceIds);
          //System.out.println("Selected Places: " + selectedPlaces);
            if (selectedPlaces != null && !selectedPlaces.isEmpty()) {
                List<RouteMapGenerator.Place> shortestRoute = null;
                double shortestDistance = Double.MAX_VALUE;
                
                for (RouteMapGenerator.Place startPlace : selectedPlaces) {
                    List<RouteMapGenerator.Place> route = RouteMapGenerator.generateRoute(selectedPlaces, startPlace);
                    double totalDistance = RouteMapGenerator.calculateTotalDistance(route);
                    if (totalDistance < shortestDistance) {
                        shortestRoute = route;
                        shortestDistance = totalDistance;
                    }
                }
                
                shortestRoute = RouteMapGenerator.optimizeRoute(shortestRoute);
              //System.out.println("Generated Route: " + shortestRoute);
                RouteMapGenerator.createRouteMapViewer(shortestRoute, userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "Please select at least two places to generate the route map.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating route map");
        }
    }

    private List<RouteMapGenerator.Place> getSelectedPlaces(List<Integer> selectedPlaceIds) throws SQLException {
        List<RouteMapGenerator.Place> selectedPlaces = new ArrayList<>();
        String query = "SELECT name, latitude, longitude FROM places WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int placeId : selectedPlaceIds) {
                preparedStatement.setInt(1, placeId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        double latitude = resultSet.getDouble("latitude");
                        double longitude = resultSet.getDouble("longitude");
                        selectedPlaces.add(new RouteMapGenerator.Place(name, latitude, longitude));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving selected places from the database");
        }
      System.out.println("Retrieved Places: " + selectedPlaces);
        return selectedPlaces;
    }

    private void goBackToLocationFrame() {
        locationFrame.setVisible(true);
        dispose();
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	RouteMapGenerator.main(userId, username);
            }
        });
    } 
}