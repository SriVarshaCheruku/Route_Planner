package Guide;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.awt.event.MouseEvent;
import Location.RouteMapViewer;
import javax.swing.table.TableColumnModel;

public class Guide extends JFrame {
    private JComboBox<String> locationBox;
    private JButton selectButton;
    private JTable guideTable;
    private JScrollPane scrollPane;
    private JButton bookButton;
    private JButton backButton; 
    private RouteMapViewer routeMapViewer;
    
    private int userId;
    private String username;

    public Guide(int userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
    }
    
    public Guide(RouteMapViewer routeMapViewer) {
    	this.routeMapViewer = routeMapViewer;
        initComponents();
    }

    private void initComponents() {
        setTitle("Select a Guide");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new FlowLayout());

        locationBox = new JComboBox<>();
        locationBox.setPreferredSize(new Dimension(200, 30));
        locationPanel.add(locationBox);

        try {
            populateLocationBox();
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLocation = (String) locationBox.getSelectedItem();
                if (selectedLocation != null && !selectedLocation.isEmpty()) {
                    populateGuideTable(selectedLocation);
                }
            }
        });
        locationPanel.add(selectButton);

        add(locationPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Contact", "Cost", "Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        guideTable = new JTable(model);
        
        guideTable.getColumnModel().getColumn(0).setMinWidth(0);
        guideTable.getColumnModel().getColumn(0).setMaxWidth(0);
        guideTable.getColumnModel().getColumn(0).setWidth(0);
        
        scrollPane = new JScrollPane(guideTable);
        add(scrollPane, BorderLayout.CENTER);

        bookButton = new JButton("Book");
        bookButton.setEnabled(false);
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = guideTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int guideId = Integer.parseInt(guideTable.getValueAt(selectedRow, 0).toString());
                    String guideName = (String) guideTable.getValueAt(selectedRow, 1);
                    String guideContact = (String) guideTable.getValueAt(selectedRow, 2);
                    BigDecimal guideCost = new BigDecimal(guideTable.getValueAt(selectedRow, 3).toString());
                    String guideDescription = (String) guideTable.getValueAt(selectedRow, 4);

                    BookingFrame b = new BookingFrame(userId,username,guideId, guideName, guideContact, guideCost, guideDescription);
                    b.setVisible(true);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(bookButton);

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (routeMapViewer != null) {
                    routeMapViewer.setVisible(true);
                }
                dispose();
            }
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        guideTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = guideTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        bookButton.setEnabled(true);
                    }
                }
            }
        });
    }

    private void populateLocationBox() throws Exception {
        String url = "jdbc:mysql://localhost:3306/RoutePlanner";
        String username = "root";
        String password = "V_sarayu6";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, username, password);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM locations");

        while (resultSet.next()) {
            locationBox.addItem(resultSet.getString("name"));
        }

        resultSet.close();
        statement.close();
        connection.close();
    }

    private void populateGuideTable(String location) {
        String url = "jdbc:mysql://localhost:3306/RoutePlanner";
        String username = "root";
        String password = "V_sarayu6";

        DefaultTableModel model = (DefaultTableModel) guideTable.getModel();
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT id, name, contact, cost, description FROM guides WHERE location_id = (SELECT id FROM locations WHERE name = '" + location + "')";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String[] row = {
                            resultSet.getString("id"),
                            resultSet.getString("name"),
                            resultSet.getString("contact"),
                            resultSet.getString("cost"),
                            resultSet.getString("description")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        TableColumnModel columnModel = guideTable.getColumnModel();
        columnModel.getColumn(1).setPreferredWidth(150); 
        columnModel.getColumn(2).setPreferredWidth(100); 
        columnModel.getColumn(3).setPreferredWidth(80); 
        columnModel.getColumn(4).setPreferredWidth(500); 
    }

    public void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	Guide guideFrame = new Guide(userId, username);
                guideFrame.setVisible(true);
            }
        });
    }
}