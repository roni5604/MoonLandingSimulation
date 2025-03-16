package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MenuPanel displays an introductory screen with mission details and instructions.
 * It includes a "Start Simulation" button to switch to the simulation view.
 */
public class MenuPanel extends JPanel {
    public MenuPanel(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // Title label
        JLabel titleLabel = new JLabel("Moon Landing Simulation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Mission description text area
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(Color.DARK_GRAY);
        descriptionArea.setForeground(Color.LIGHT_GRAY);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setText(
                "Mission:\n" +
                        "Design and simulate a safe landing of a spacecraft on the Moon.\n\n" +
                        "Objectives:\n" +
                        "- Start at an altitude of 30,000 meters above the Moon's surface.\n" +
                        "- Approach the landing site with an initial horizontal speed of approximately 1700 m/s.\n" +
                        "- Achieve a soft landing with vertical and horizontal speeds below 2.5 m/s.\n" +
                        "- Conserve as much fuel as possible (targeting 50 liters remaining at touchdown).\n\n" +
                        "The simulation models the Moon's gravity (1.622 m/s²) with no atmospheric effects, using:\n" +
                        "• A main engine with 430 N thrust\n" +
                        "• 8 side engines with 25 N each for attitude control\n\n" +
                        "Press 'Start Simulation' to begin the mission!"
        );
        add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // Start button
        JButton startButton = new JButton("Start Simulation");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Simulation");
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
