package Guide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingFrame extends JFrame {
    private JLabel nameLabel;
    private JLabel contactLabel;
    private JLabel costLabel;
    private JLabel descriptionLabel;
    private JButton bookButton;
    
    private int guideId;
    private String guideName;
    private String guideContact;
    private BigDecimal guideCost;
    private String guideDescription;
    
    private int userId;
    private String username;

    public BookingFrame(int userId, String username, int guideId, String guideName, String guideContact, BigDecimal guideCost, String guideDescription) {
        this.userId = userId;
        this.username = username;
        this.guideId = guideId;
        this.guideName = guideName;
        this.guideContact = guideContact;
        this.guideCost = guideCost;
        this.guideDescription = guideDescription;
        initComponents();
    }


    private void initComponents() {
        setTitle("Book Guide");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel(new GridLayout(4, 2));
        nameLabel = new JLabel("Name: " + guideName);
        detailsPanel.add(nameLabel);
        contactLabel = new JLabel("Contact: " + guideContact);
        detailsPanel.add(contactLabel);
        costLabel = new JLabel("Cost: " + guideCost);
        detailsPanel.add(costLabel);
        descriptionLabel = new JLabel("Description: " + guideDescription);
        detailsPanel.add(descriptionLabel);
        add(detailsPanel, BorderLayout.CENTER);

        bookButton = new JButton("Book Guide");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FakePaymentDialog paymentDialog = new FakePaymentDialog(BookingFrame.this, guideCost);
                paymentDialog.setVisible(true);
                if (paymentDialog.isPaymentSuccessful()) {
                    if (bookGuide()) {
                        JOptionPane.showMessageDialog(BookingFrame.this, "Guide booked successfully!");
                        dispose(); 
                    } else {
                        JOptionPane.showMessageDialog(BookingFrame.this, "Booking failed. Please try again later.");
                    }
                } else {
                    JOptionPane.showMessageDialog(BookingFrame.this, "Payment failed. Booking not completed.");
                }
            }
        });
        add(bookButton, BorderLayout.SOUTH);
    }

    private boolean bookGuide() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/routeplanner", "root", "V_sarayu6");
            String sql = "INSERT INTO Booking (user_id, username, guide_id, guide_name, guide_cost) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setInt(3, guideId);
            pstmt.setString(4, guideName);
            pstmt.setBigDecimal(5, guideCost);
            int rowsAffected = pstmt.executeUpdate();
            conn.close();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void openBookingFrame(int userId, String username, int guideId, String guideName, String guideContact, BigDecimal guideCost, String guideDescription) {
        BookingFrame bookingFrame = new BookingFrame(userId, username, guideId, guideName, guideContact, guideCost, guideDescription);
        bookingFrame.setVisible(true);
    }

}

class FakePaymentDialog extends JDialog {
    private boolean paymentSuccessful;
    private BigDecimal amount;

    public FakePaymentDialog(Frame owner, BigDecimal amount) {
        super(owner, "Fake Payment Processor", true);
        this.amount = amount;
        initComponents();
    }

    private void initComponents() {
        setSize(300, 150);
        setLocationRelativeTo(getOwner());

        JLabel amountLabel = new JLabel("Amount: " + amount);
        JButton payButton = new JButton("Pay");
        JButton cancelButton = new JButton("Cancel");

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paymentSuccessful = true;
                JOptionPane.showMessageDialog(FakePaymentDialog.this, "Payment successful!");
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paymentSuccessful = false;
                JOptionPane.showMessageDialog(FakePaymentDialog.this, "Payment cancelled.");
                dispose();
            }
        });

        setLayout(new GridLayout(3, 1));
        add(amountLabel);
        add(payButton);
        add(cancelButton);
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }
}

class Booking {
    private int guideId;
    private String guideName;
    private String guideContact;
    private BigDecimal guideCost;
    private String guideDescription;

    public Booking(int guideId, String guideName, String guideContact, BigDecimal guideCost, String guideDescription) {
        this.guideId = guideId;
        this.guideName = guideName;
        this.guideContact = guideContact;
        this.guideCost = guideCost;
        this.guideDescription = guideDescription;
    }
}