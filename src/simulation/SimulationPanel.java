package simulation;

import controller.PIDController;
import model.Spacecraft;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * SimulationPanel manages and visualizes the spacecraft landing simulation.
 * It draws the Moon's surface, the planned trajectory, the spacecraft icon,
 * and displays real-time simulation data (time, altitude, speeds, angle, and fuel).
 *
 * It also provides user controls for pausing/resuming, resetting, and adjusting the simulation speed.
 * The simulation uses PID controllers to adjust vertical speed, horizontal speed, and orientation.
 */
public class SimulationPanel extends JPanel {
    // Simulation constants
    private static final double dt = 0.1;                     // Base time step (seconds)
    private static final double SIMULATION_TIME_LIMIT = 1000;  // Maximum simulation time (seconds)

    private Spacecraft sc;
    private PIDController verticalPID;
    private PIDController horizontalPID;
    private PIDController orientationPID;
    private List<Point.Double> trajectory;
    private Timer timer;
    private double simulationTime;
    private double simulationSpeedMultiplier = 1.0; // 1.0 = real-time speed
    private boolean isPaused = false;

    // UI controls
    private JButton pauseButton;
    private JButton resetButton;
    private JSlider speedSlider;

    // Mission description for display
    private String missionDesc = "Mission: Soft Landing on the Moon\n" +
            "Initial Conditions: Altitude = 30,000 m, Horizontal Speed = 1700 m/s\n" +
            "Objective: Land with vertical and horizontal speeds < 2.5 m/s while conserving fuel.";

    public SimulationPanel(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        trajectory = new ArrayList<>();
        initializeSimulation();
        createControlPanel();

        // Create a Swing Timer to update simulation state.
        timer = new Timer((int)(dt * 1000 / simulationSpeedMultiplier), (ActionEvent e) -> updateSimulation());
        timer.start();
    }

    /**
     * Initializes the simulation state and PID controllers.
     */
    private void initializeSimulation() {
        // Initial conditions (in meters and m/s)
        double initialAltitude = 30000;                // 30,000 m above the Moon
        double initialHorizontalDistance = 0;          // Starting horizontal distance (0 m)
        double initialVerticalSpeed = 0;               // 0 m/s vertical speed
        double initialHorizontalSpeed = 1700;          // 1700 m/s horizontal speed
        double initialAngle = 0;                       // Initially vertical (0°)

        sc = new Spacecraft(initialAltitude, initialHorizontalDistance, initialVerticalSpeed, initialHorizontalSpeed, initialAngle);
        simulationTime = 0;
        trajectory.clear();
        trajectory.add(new Point.Double(sc.horizontalDistance, sc.altitude));

        // Adjusted PID controllers with more aggressive gains.
        verticalPID = new PIDController(0.02, 0.0005, 0.005, 2.0, -1.0, 1.0); // Target vertical speed = 2 m/s
        horizontalPID = new PIDController(0.02, 0.0005, 0.005, 0.0, -1.0, 1.0); // Target horizontal speed = 0 m/s
        orientationPID = new PIDController(1.0, 0.001, 0.2, 0.0, -30.0, 30.0);   // Target angle set dynamically
    }

    /**
     * Creates the control panel with Pause/Resume, Reset buttons, and a Speed slider.
     */
    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Color.DARK_GRAY);

        // Pause/Resume button
        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 16));
        pauseButton.addActionListener((ActionEvent e) -> {
            if (isPaused) {
                timer.start();
                pauseButton.setText("Pause");
                isPaused = false;
            } else {
                timer.stop();
                pauseButton.setText("Resume");
                isPaused = true;
            }
        });
        controlPanel.add(pauseButton);

        // Reset button
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.addActionListener((ActionEvent e) -> {
            timer.stop();
            initializeSimulation();
            pauseButton.setText("Pause");
            isPaused = false;
            timer.start();
            repaint();
        });
        controlPanel.add(resetButton);

        // Speed slider: from 0.5x to 5x simulation speed
        speedSlider = new JSlider(JSlider.HORIZONTAL, 50, 500, 100);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener((ChangeEvent e) -> {
            simulationSpeedMultiplier = speedSlider.getValue() / 100.0;
            timer.setDelay((int)(dt * 1000 / simulationSpeedMultiplier));
        });
        controlPanel.add(new JLabel("Speed:"));
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the simulation state and refreshes the visualization.
     */
    private void updateSimulation() {
        // Update PID controllers for vertical, horizontal, and orientation control.
        double verticalCorrection = verticalPID.update(sc.verticalSpeed, dt * simulationSpeedMultiplier);
        // Compute throttle based on vertical PID output.
        double throttleFromPID = Math.max(0.0, Math.min(1.0, 0.5 + verticalCorrection));
        double mainThrottle;
        // Force full throttle when close to the surface for a safe landing.
        if (sc.altitude < 2000) {
            mainThrottle = 1.0;
        } else {
            mainThrottle = throttleFromPID;
        }

        double horizontalCorrection = horizontalPID.update(sc.horizontalSpeed, dt * simulationSpeedMultiplier);
        double desiredAngle = Math.max(-20.0, Math.min(20.0, horizontalCorrection));

        orientationPID.setSetpoint(desiredAngle);
        double angleCorrection = orientationPID.update(sc.angle, dt * simulationSpeedMultiplier);
        sc.angle = Math.max(-30.0, Math.min(30.0, sc.angle + angleCorrection));

        // Calculate main engine thrust based on throttle and current orientation.
        double mainThrust = mainThrottle * Spacecraft.MAIN_ENGINE_THRUST;
        double angleRad = Math.toRadians(sc.angle);
        double thrustVertical = mainThrust * Math.cos(angleRad);
        double thrustHorizontal = mainThrust * Math.sin(angleRad);

        // Update fuel consumption and spacecraft mass.
        double fuelConsumed = Spacecraft.MAIN_BURN_RATE * mainThrottle * dt * simulationSpeedMultiplier;
        if (sc.fuel < fuelConsumed) {
            fuelConsumed = sc.fuel;
        }
        sc.updateMass(fuelConsumed);

        // Check if fuel is exhausted.
        if (sc.fuel <= 0) {
            timer.stop();
            System.out.println("Fuel exhausted! Simulation aborted.");
            return;
        }

        // Calculate accelerations.
        double a_vertical = Moon.GRAVITY - (thrustVertical / sc.mass);
        double a_horizontal = - (thrustHorizontal / sc.mass);

        // Update velocities (Euler integration).
        sc.verticalSpeed += a_vertical * dt * simulationSpeedMultiplier;
        sc.horizontalSpeed += a_horizontal * dt * simulationSpeedMultiplier;

        // Update positions.
        sc.altitude -= sc.verticalSpeed * dt * simulationSpeedMultiplier;
        sc.horizontalDistance += sc.horizontalSpeed * dt * simulationSpeedMultiplier;
        if (sc.altitude < 0) {
            sc.altitude = 0;
        }

        simulationTime += dt * simulationSpeedMultiplier;
        sc.time = simulationTime;
        trajectory.add(new Point.Double(sc.horizontalDistance, sc.altitude));

        repaint();

        // Stop simulation if landing is complete.
        if (sc.altitude <= 0 || simulationTime >= SIMULATION_TIME_LIMIT) {
            timer.stop();
            System.out.println("Landing complete. Final conditions:");
            System.out.printf("Time: %.1f s, Altitude: %.2f m, Vertical Speed: %.2f m/s, Horizontal Speed: %.2f m/s, Fuel remaining: %.2f kg\n",
                    simulationTime, sc.altitude, sc.verticalSpeed, sc.horizontalSpeed, sc.fuel);
        }
    }

    /**
     * Custom paintComponent method to draw the simulation.
     * It draws the Moon's surface, the planned trajectory, the spacecraft icon,
     * and displays current simulation data.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int groundY = panelHeight - 50;

        // Draw Moon surface as a curved arc.
        g2.setColor(new Color(100, 100, 100));
        g2.fillArc(0, groundY - 100, panelWidth, 200, 0, 180);

        // Draw planned trajectory (dashed yellow line) – for illustration, a simple straight line.
        g2.setColor(Color.YELLOW);
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
        int startX = 0;
        int startY = groundY - (int)(30000 * 0.005);
        int endX = panelWidth;
        int endY = groundY;
        g2.drawLine(startX, startY, endX, endY);
        g2.setStroke(originalStroke);

        // Define scaling factors for visualization.
        double scaleX = 0.0005;  // Horizontal scale factor
        double scaleY = 0.005;   // Vertical scale factor

        // Draw trajectory (cyan polyline)
        g2.setColor(Color.CYAN);
        for (int i = 1; i < trajectory.size(); i++) {
            int x1 = (int)(trajectory.get(i - 1).x * scaleX);
            int y1 = groundY - (int)(trajectory.get(i - 1).y * scaleY);
            int x2 = (int)(trajectory.get(i).x * scaleX);
            int y2 = groundY - (int)(trajectory.get(i).y * scaleY);
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw spacecraft as a rotated red triangle.
        int scX = (int)(sc.horizontalDistance * scaleX);
        int scY = groundY - (int)(sc.altitude * scaleY);
        Polygon spacecraftShape = new Polygon();
        int size = 12;
        // Define triangle points centered at (0,0)
        spacecraftShape.addPoint(0, -size);
        spacecraftShape.addPoint(-size / 2, size / 2);
        spacecraftShape.addPoint(size / 2, size / 2);

        Graphics2D g2Copy = (Graphics2D) g2.create();
        g2Copy.translate(scX, scY);
        g2Copy.rotate(Math.toRadians(sc.angle));
        g2Copy.setColor(Color.RED);
        g2Copy.fillPolygon(spacecraftShape);
        g2Copy.dispose();

        // Display current simulation info at top-left.
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String info = String.format("Time: %.1f s   Altitude: %.2f m   Vertical Speed: %.2f m/s   Horizontal Speed: %.2f m/s   Angle: %.2f°   Fuel: %.2f kg",
                sc.time, sc.altitude, sc.verticalSpeed, sc.horizontalSpeed, sc.angle, sc.fuel);
        g2.drawString(info, 20, 30);
    }
}
