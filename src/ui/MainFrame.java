package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import Location.LocationFrame;

public class MainFrame extends JFrame implements ActionListener {
    private JButton loginButton;
    private JButton signupButton;
    private JLabel background;
    private BufferedImage image;

    public MainFrame() {
        super("Route Planner");
        setLayout(null);

        try {
            image = ImageIO.read(new File("images/BD3B8378-0605-42A7-AA07-4789393D7AE7_4_5005_c.jpeg"));
            background = new JLabel(new ImageIcon(image));
            add(background);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        loginButton.addActionListener(this);
        signupButton.addActionListener(this);

        background.add(loginButton);
        background.add(signupButton);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(scaledImage));
        background.setBounds(0, 0, width, height);

        int buttonWidth = 120;
        int buttonHeight = 42;
        int horizontalGap = 25;

        int totalButtonWidth = buttonWidth * 2 + horizontalGap;
        int offsetX = 50; 
        int buttonStartX = (width - totalButtonWidth) / 2 + offsetX;
        int buttonStartY = (height - buttonHeight) / 2;

        loginButton.setBounds(buttonStartX, buttonStartY, buttonWidth, buttonHeight);
        signupButton.setBounds(buttonStartX + buttonWidth + horizontalGap, buttonStartY, buttonWidth, buttonHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            new LoginPage(this);
        } else if (e.getSource() == signupButton) {
            new SignupPage(this);
        }
    }

    public void handleSuccessfulLogin(int userId, String username) {
        LocationFrame l = new LocationFrame(userId, username);
        l.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}