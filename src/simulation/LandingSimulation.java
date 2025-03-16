package simulation;

import controller.PIDController;
import model.Spacecraft;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * LandingSimulation class simulates the landing of a spacecraft on the Moon.
 * The mission is to perform a controlled landing from an initial altitude of 30 km
 * with an initial horizontal speed of about 1700 m/s. The objective is to achieve a soft landing,
 * with vertical and horizontal speeds below 2.5 m/s at touchdown, while maximizing remaining fuel.
 *
 * The simulation uses PID controllers to adjust vertical speed, horizontal speed, and orientation.
 * It also provides a graphical visualization of the trajectory and state parameters.
 *
 * Units:
 * - Distance: meters
 * - Time: seconds
 * - Mass: kilograms (kg)
 * - Speed: m/s
 */
public class LandingSimulation {
    public static final double dt = 0.1;                  // Time step (seconds)
    public static final double SIMULATION_TIME_LIMIT = 1000; // Maximum simulation time (seconds)

    // List to store trajectory points for drawing
    private List<Point.Double> trajectory = new ArrayList<>();

    /**
     * Helper method to clamp a value between min and max.
     *
     * @param value The value to clamp.
     * @param min Minimum allowed value.
     * @param max Maximum allowed value.
     * @return The clamped value.
     */
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * The SimulationPanel class is a custom JPanel that draws the spacecraft trajectory,
     * the current position of the spacecraft, and additional information on the screen.
     */
    private class SimulationPanel extends JPanel {
        private Spacecraft sc;
        private List<Point.Double> traj;
        private String missionDescription;

        /**
         * Constructs the simulation panel.
         *
         * @param sc The spacecraft object.
         * @param traj The trajectory points.
         * @param missionDescription A descriptive text of the mission.
         */
        public SimulationPanel(Spacecraft sc, List<Point.Double> traj, String missionDescription) {
            this.sc = sc;
            this.traj = traj;
            this.missionDescription = missionDescription;
            setBackground(Color.BLACK);
        }

        /**
         * Overrides the paintComponent method to draw the trajectory and spacecraft.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw mission description at the top (in white)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(missionDescription, 20, 20);

            // Define scale factors for drawing (adjust as needed)
            double scaleX = 0.0005;  // horizontal scaling
            double scaleY = 0.005;   // vertical scaling (altitude)

            int panelHeight = getHeight();
            // Draw ground line at altitude 0
            g2.setColor(Color.GREEN);
            int groundY = panelHeight - 50;
            g2.drawLine(0, groundY, getWidth(), groundY);

            // Draw trajectory as polyline
            g2.setColor(Color.CYAN);
            for (int i = 1; i < traj.size(); i++) {
                int x1 = (int) (traj.get(i - 1).x * scaleX);
                int y1 = groundY - (int) (traj.get(i - 1).y * scaleY);
                int x2 = (int) (traj.get(i).x * scaleX);
                int y2 = groundY - (int) (traj.get(i).y * scaleY);
                g2.drawLine(x1, y1, x2, y2);
            }

            // Draw spacecraft as a red circle at current position
            g2.setColor(Color.RED);
            int spacecraftX = (int) (sc.horizontalDistance * scaleX);
            int spacecraftY = groundY - (int) (sc.altitude * scaleY);
            int radius = 8;
            g2.fillOval(spacecraftX - radius, spacecraftY - radius, 2 * radius, 2 * radius);

            // Draw current simulation info at bottom
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            String info = String.format("Time: %.1f s, Altitude: %.2f m, Vertical Speed: %.2f m/s, Horizontal Speed: %.2f m/s, Angle: %.2f°, Fuel: %.2f kg",
                    sc.time, sc.altitude, sc.verticalSpeed, sc.horizontalSpeed, sc.angle, sc.fuel);
            g2.drawString(info, 20, getHeight() - 20);
        }
    }

    /**
     * The main simulation method. It initializes the spacecraft, PID controllers,
     * and creates a Swing Timer to update the simulation and refresh the visualization.
     */
    public void runSimulation() {
        // Mission description text
        String missionDesc = "Mission: Soft Landing on the Moon\n" +
                "Initial Conditions: Altitude = 30,000 m, Horizontal Speed = 1700 m/s\n" +
                "Objective: Land with vertical and horizontal speeds < 2.5 m/s while conserving fuel.";

        // Initial conditions
        double initialAltitude = 30000;                // 30,000 m above Moon
        double initialHorizontalDistance = 0;          // Starting horizontal distance (we consider 0 as start)
        double initialVerticalSpeed = 0;               // Initial vertical speed (m/s)
        double initialHorizontalSpeed = 1700;          // Initial horizontal speed (m/s)
        double initialAngle = 0;                       // Initially vertical (0°)

        // Create spacecraft object
        Spacecraft sc = new Spacecraft(initialAltitude, initialHorizontalDistance, initialVerticalSpeed, initialHorizontalSpeed, initialAngle);

        // Define PID controllers for vertical, horizontal and orientation control.
        double desiredVerticalSpeed = 2.0; // Desired vertical speed (m/s downward)
        PIDController verticalPID = new PIDController(0.005, 0.0001, 0.001, desiredVerticalSpeed);
        PIDController horizontalPID = new PIDController(0.01, 0.0001, 0.002, 0.0); // Target horizontal speed 0
        PIDController orientationPID = new PIDController(0.5, 0.001, 0.1, 0.0); // Target angle will be set dynamically

        // List to record trajectory points (for visualization)
        List<Point.Double> trajectory = new ArrayList<>();
        // Record initial position
        trajectory.add(new Point.Double(sc.horizontalDistance, sc.altitude));

        // Create main JFrame for simulation visualization
        JFrame frame = new JFrame("Moon Landing Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Create simulation panel that will animate the trajectory
        SimulationPanel simPanel = new SimulationPanel(sc, trajectory, missionDesc);
        frame.add(simPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Create a Swing Timer to update simulation state and refresh animation
        Timer timer = new Timer((int)(dt * 1000), (ActionEvent e) -> {
            // Update PID controllers for vertical, horizontal, and orientation control.
            double verticalCorrection = verticalPID.update(sc.verticalSpeed, dt);
            double mainThrottle = clamp(0.5 + verticalCorrection, 0, 1);

            double horizontalCorrection = horizontalPID.update(sc.horizontalSpeed, dt);
            double desiredAngle = clamp(horizontalCorrection, -20, 20);

            orientationPID.setSetpoint(desiredAngle);
            double angleCorrection = orientationPID.update(sc.angle, dt);
            sc.angle = clamp(sc.angle + angleCorrection, -30, 30);

            // Calculate main engine thrust based on throttle and current orientation.
            double mainThrust = mainThrottle * Spacecraft.MAIN_ENGINE_THRUST;
            double angleRad = Math.toRadians(sc.angle);
            double thrustVertical = mainThrust * Math.cos(angleRad);
            double thrustHorizontal = mainThrust * Math.sin(angleRad);

            // Update fuel consumption and mass.
            double fuelConsumed = Spacecraft.MAIN_BURN_RATE * mainThrottle * dt;
            if (sc.fuel < fuelConsumed) {
                fuelConsumed = sc.fuel;
            }
            sc.updateMass(fuelConsumed);

            // Calculate accelerations:
            // Vertical acceleration: Moon gravity minus thrust effect.
            double a_vertical = Moon.GRAVITY - (thrustVertical / sc.mass);
            // Horizontal acceleration: Opposing the horizontal speed.
            double a_horizontal = - (thrustHorizontal / sc.mass);

            // Update velocities using Euler integration.
            sc.verticalSpeed += a_vertical * dt;
            sc.horizontalSpeed += a_horizontal * dt;

            // Update positions.
            sc.altitude -= sc.verticalSpeed * dt;
            sc.horizontalDistance += sc.horizontalSpeed * dt; // Increase horizontal distance as spacecraft travels

            if (sc.altitude < 0) {
                sc.altitude = 0;
            }

            sc.time += dt;
            // Add current state to trajectory for visualization.
            trajectory.add(new Point.Double(sc.horizontalDistance, sc.altitude));

            // Repaint simulation panel.
            simPanel.repaint();

            // Stop simulation if landed (altitude == 0) or if time limit reached.
            if (sc.altitude <= 0 || sc.time >= SIMULATION_TIME_LIMIT) {
                ((Timer)e.getSource()).stop();
                System.out.println("Landing complete. Final conditions:");
                System.out.printf("Time: %.1f s, Altitude: %.2f m, Vertical Speed: %.2f m/s, Horizontal Speed: %.2f m/s, Fuel remaining: %.2f kg\n",
                        sc.time, sc.altitude, sc.verticalSpeed, sc.horizontalSpeed, sc.fuel);
            }
        });
        timer.start();
    }

    /**
     * Main method to run the simulation.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LandingSimulation simulation = new LandingSimulation();
            simulation.runSimulation();
        });
    }
}
