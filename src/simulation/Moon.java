package simulation;

/**
 * The Moon class models the physical properties of the Moon.
 * It defines the Moon's radius, gravitational acceleration, and
 * provides utility methods related to gravity.
 *
 * Units:
 * - Distance: meters
 * - Time: seconds
 * - Acceleration: m/s²
 */
public class Moon {
    public static final double RADIUS = 3475 * 1000; // Radius in meters
    public static final double GRAVITY = 1.622;        // Gravitational acceleration (m/s²)
    public static final double EQ_SPEED = 1700;        // Reference horizontal speed (m/s)

    /**
     * Returns the effective gravitational acceleration based on horizontal speed.
     * As horizontal speed increases toward EQ_SPEED, the effective gravity decreases.
     *
     * @param horizontalSpeed The horizontal speed of the spacecraft.
     * @return The effective gravitational acceleration.
     */
    public static double getEffectiveGravity(double horizontalSpeed) {
        double n = Math.abs(horizontalSpeed) / EQ_SPEED;
        return (1 - n) * GRAVITY;
    }
}
