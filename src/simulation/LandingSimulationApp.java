package simulation;

import javax.swing.*;
import java.awt.*;

/**
 * LandingSimulationApp is the main entry point for the application.
 * It uses a CardLayout to switch between the MenuPanel and the SimulationPanel.
 */
public class LandingSimulationApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Moon Landing Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);
            frame.setLocationRelativeTo(null);

            // Use CardLayout for switching screens
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Create the menu and simulation panels
            MenuPanel menuPanel = new MenuPanel(mainPanel, cardLayout);
            SimulationPanel simulationPanel = new SimulationPanel(mainPanel, cardLayout);

            mainPanel.add(menuPanel, "Menu");
            mainPanel.add(simulationPanel, "Simulation");

            frame.getContentPane().add(mainPanel);
            frame.setVisible(true);
        });
    }
}
