package Location;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import Guide.Guide;

public class RouteMapViewer extends JFrame {
    private List<RouteMapGenerator.Place> route;
    private Place placeFrame;
    private int userId;
    private String username;

    public RouteMapViewer(List<RouteMapGenerator.Place> route, Place placeFrame, int userId, String username) {
        this.route = route;
        this.placeFrame = placeFrame;
        this.userId = userId;
        this.username = username;
        initializeUI();
    }

    public RouteMapViewer(List<RouteMapGenerator.Place> route) {
        this.route = route;
        this.placeFrame = placeFrame;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Route Map");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (route != null && !route.isEmpty())
                    drawRoute((Graphics2D) g, getWidth(), getHeight());
            }
        };

        mapPanel.setPreferredSize(new Dimension(600, 400));
        mapPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        add(mapPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (placeFrame != null) {
                    placeFrame.setVisible(true);
                }
                dispose();
            }
        });

        JButton bookGuideButton = new JButton("Book a Guide");
        bookGuideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Guide(userId, username).setVisible(true);
            }
        });


        buttonPanel.add(backButton);
        buttonPanel.add(bookGuideButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void drawRoute(Graphics2D g2d, int panelWidth, int panelHeight) {
        ImageIcon backgroundImage = new ImageIcon("images/6FB1E7FF-E305-4ADF-9A16-14A7CA6D790B_4_5005_c.jpeg");
        Image img = backgroundImage.getImage();

        int paddingTop = 50;
        int paddingSides = 50;
        int paddingBottom = 80; 

        g2d.drawImage(img, 0, 0, panelWidth, panelHeight, null);

        g2d.setStroke(new BasicStroke(3.5f));
        g2d.setColor(Color.BLUE);

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (RouteMapGenerator.Place place : route) {
            minX = Math.min(minX, place.x);
            minY = Math.min(minY, place.y);
            maxX = Math.max(maxX, place.x);
            maxY = Math.max(maxY, place.y);
        }

        double scaleX = (panelWidth - 2 * paddingSides) / (maxX - minX);
        double scaleY = (panelHeight - paddingTop - paddingBottom) / (maxY - minY);

        double scale = Math.min(scaleX, scaleY);

        int paddingX = (int) ((panelWidth - (maxX - minX) * scale) / 2);
        int paddingY = paddingTop + (int) ((panelHeight - paddingTop - paddingBottom - (maxY - minY) * scale) / 2);

        RouteMapGenerator.Place start = route.get(0);
        int startX = (int) ((start.x - minX) * scale) + paddingX;
        int startY = (int) ((start.y - minY) * scale) + paddingY;
        for (int i = 1; i < route.size(); i++) {
            RouteMapGenerator.Place place = route.get(i);
            int x = (int) ((place.x - minX) * scale) + paddingX;
            int y = (int) ((place.y - minY) * scale) + paddingY;
            g2d.drawLine(startX, startY, x, y);
            startX = x;
            startY = y;
        }

        FontMetrics fm = g2d.getFontMetrics();
        for (RouteMapGenerator.Place place : route) {
            int x = (int) ((place.x - minX) * scale) + paddingX;
            int y = (int) ((place.y - minY) * scale) + paddingY;

            ImageIcon locationIcon = new ImageIcon("images/86A88723-F574-42AE-A87F-0F5DDEF828FB_4_5005_c.jpeg");
            Image img1 = locationIcon.getImage();

            int iconWidth = 18; 
            int iconHeight = 23; 
            g2d.drawImage(img1, x - iconWidth / 2, y - iconHeight / 2, iconWidth, iconHeight, null);

            g2d.setColor(Color.BLACK);

            int nameWidth = fm.stringWidth(place.name);
            if (x + nameWidth + 8 > panelWidth) {
                g2d.drawString(place.name, x - nameWidth - 8, y + 4);
            } else {
                g2d.drawString(place.name, x + 8, y + 4);
            }
        }
    }
}