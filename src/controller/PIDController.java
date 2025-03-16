package controller;

/**
 * The PIDController class implements a PID controller with output clamping and anti-windup.
 * It computes the control output based on the error between the measured value and the target setpoint.
 *
 * Parameters:
 * - kp: Proportional gain
 * - ki: Integral gain
 * - kd: Derivative gain
 * - setpoint: Desired target value
 * - outputMin, outputMax: Limits for the control output.
 */
public class PIDController {
    private double kp, ki, kd;
    private double setpoint;
    private double prevError;
    private double integral;
    private double outputMin, outputMax;

    /**
     * Constructs a PIDController with specified gains, setpoint, and output limits.
     *
     * @param kp Proportional gain.
     * @param ki Integral gain.
     * @param kd Derivative gain.
     * @param setpoint The target value.
     * @param outputMin Minimum output limit.
     * @param outputMax Maximum output limit.
     */
    public PIDController(double kp, double ki, double kd, double setpoint, double outputMin, double outputMax) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.setpoint = setpoint;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
        this.prevError = 0;
        this.integral = 0;
    }

    /**
     * Sets a new setpoint.
     *
     * @param setpoint The new target value.
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    /**
     * Resets the internal state (integral and previous error) of the controller.
     */
    public void reset() {
        this.integral = 0;
        this.prevError = 0;
    }

    /**
     * Updates the PID controller with the current measurement.
     * Implements anti-windup by clamping the integral term.
     *
     * @param measurement The current measured value.
     * @param dt The time step (seconds).
     * @return The control output.
     */
    public double update(double measurement, double dt) {
        double error = setpoint - measurement;
        integral += error * dt;
        // Anti-windup: Clamp the integral term.
        double integralMax = outputMax / (ki != 0 ? ki : 1);
        if (integral > integralMax) integral = integralMax;
        if (integral < -integralMax) integral = -integralMax;

        double derivative = (error - prevError) / dt;
        double output = kp * error + ki * integral + kd * derivative;
        // Clamp output.
        if (output > outputMax) output = outputMax;
        if (output < outputMin) output = outputMin;
        prevError = error;
        return output;
    }
}
