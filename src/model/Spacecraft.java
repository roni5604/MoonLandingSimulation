package model;

/**
 * The Spacecraft class represents the dynamic model of a spacecraft during a Moon landing.
 *
 * It maintains state variables such as mass (which decreases as fuel is consumed),
 * vertical and horizontal speeds, altitude, horizontal distance, and orientation.
 *
 * Additionally, it simulates simple rotational dynamics with a rotational inertia.
 *
 * Units:
 * - Mass: kilograms (kg)
 * - Distance: meters (m)
 * - Speed: m/s
 * - Angle: degrees (0 = vertical)
 * - Rotational speed: degrees per second
 */
public class Spacecraft {
    // Constant parameters
    public static final double DRY_MASS = 165;         // Dry mass (kg)
    public static final double INITIAL_FUEL = 420;       // Initial fuel mass (kg)
    public static final double MAIN_ENGINE_THRUST = 430;  // Main engine thrust (N)
    public static final double SIDE_ENGINE_THRUST = 25;   // Thrust per side engine (N)
    public static final double MAIN_BURN_RATE = 0.15;      // Fuel consumption rate for main engine (kg/s)

    // Dynamic state variables
    public double fuel;              // Remaining fuel (kg)
    public double mass;              // Current mass = DRY_MASS + fuel (kg)
    public double verticalSpeed;     // Vertical speed (m/s, positive = downward)
    public double horizontalSpeed;   // Horizontal speed (m/s)
    public double altitude;          // Altitude above the Moon's surface (m)
    public double horizontalDistance; // Horizontal distance traveled (m)
    public double angle;             // Orientation angle (degrees; 0 = vertical)
    public double rotationalSpeed;   // Rotational speed (degrees per second)
    public double rotationalInertia; // Rotational inertia (kg*m²)
    public double time;              // Simulation time (s)

    /**
     * Constructs a spacecraft with specified initial conditions.
     *
     * @param altitude Initial altitude (m)
     * @param horizontalDistance Initial horizontal distance (m)
     * @param verticalSpeed Initial vertical speed (m/s)
     * @param horizontalSpeed Initial horizontal speed (m/s)
     * @param angle Initial orientation angle (degrees)
     */
    public Spacecraft(double altitude, double horizontalDistance, double verticalSpeed, double horizontalSpeed, double angle) {
        this.fuel = INITIAL_FUEL;
        this.mass = DRY_MASS + fuel;
        this.altitude = altitude;
        this.horizontalDistance = horizontalDistance;
        this.verticalSpeed = verticalSpeed;
        this.horizontalSpeed = horizontalSpeed;
        this.angle = angle;
        this.time = 0;
        this.rotationalSpeed = 0;
        this.rotationalInertia = 100; // Chosen arbitrarily for simulation
    }

    /**
     * Updates the spacecraft's mass based on the fuel consumed.
     *
     * @param fuelConsumed The amount of fuel burned (kg).
     */
    public void updateMass(double fuelConsumed) {
        fuel -= fuelConsumed;
        if (fuel < 0) {
            fuel = 0;
        }
        mass = DRY_MASS + fuel;
    }

    /**
     * Updates the spacecraft's attitude using a simplified rotational dynamics model.
     *
     * @param appliedTorque The torque applied for attitude control (N*m).
     * @param dt The time step (seconds).
     */
    public void updateAttitude(double appliedTorque, double dt) {
        // Angular acceleration in rad/s² = torque / rotationalInertia.
        // Convert to degrees/s² (1 rad ≈ 57.2958°).
        double angularAcceleration = (appliedTorque / rotationalInertia) * 57.2958;
        rotationalSpeed += angularAcceleration * dt;
        angle += rotationalSpeed * dt;
    }

    /**
     * Resets the spacecraft to new initial conditions.
     *
     * @param altitude Initial altitude (m)
     * @param horizontalDistance Initial horizontal distance (m)
     * @param verticalSpeed Initial vertical speed (m/s)
     * @param horizontalSpeed Initial horizontal speed (m/s)
     * @param angle Initial orientation angle (degrees)
     */
    public void reset(double altitude, double horizontalDistance, double verticalSpeed, double horizontalSpeed, double angle) {
        this.fuel = INITIAL_FUEL;
        this.mass = DRY_MASS + fuel;
        this.altitude = altitude;
        this.horizontalDistance = horizontalDistance;
        this.verticalSpeed = verticalSpeed;
        this.horizontalSpeed = horizontalSpeed;
        this.angle = angle;
        this.time = 0;
        this.rotationalSpeed = 0;
    }
}
