
# MoonLandingSimulation

## Overview

**MoonLandingSimulation** is an autonomous landing simulator for a spacecraft inspired by the "Bereshit" mission. This project is developed as part of an introductory course in Space Engineering at Ariel University. The simulator focuses on modeling the landing dynamics of a small, fuel‐constrained spacecraft on the Moon, using realistic lunar parameters and advanced control techniques.

## Assignment Requirements

The assignment consists of two main parts:

### Part 1: Crash Analysis Report
- **Objective:**  
  Write a brief report explaining, in your own words, the technical reasons that led to the crash of a spacecraft.
- **Scope:**  
  Although some critical information may not be publicly available, you are expected to research and describe a chain of technical events—such as navigation errors, repeated system resets, and propulsion system failures—that could lead to a mission failure.
- **Sources:**  
  Use the background material provided (e.g., relevant articles, videos, and written reports) as a reference. Your report should describe the sequence of events and the underlying technical issues (e.g., issues with star trackers, inertial measurement errors, limited system redundancy, and the use of low-cost components).

### Part 2: Autonomous Landing Simulation
- **Objective:**  
  Design and develop a simulation for the autonomous navigation and landing of the spacecraft on the Moon.
- **Key Requirements:**
    - **Physical Modeling:**  
      Model the Moon’s gravitational force (1.622 m/s²) without atmospheric drag (the Moon has no atmosphere), ignoring Earth’s gravitational effects.
    - **Spacecraft Dynamics:**  
      The spacecraft should be modeled with a variable mass (decreasing with fuel consumption), vertical and horizontal velocities, and rotational dynamics. The craft uses:
        - A main engine with 430 N thrust for deceleration.
        - Eight attitude control engines (25 N each) for orientation.
    - **Trajectory Definition:**  
      Define a landing trajectory starting from an altitude of approximately 30 km with a horizontal speed of about 1700 m/s. The trajectory duration should be similar to the original mission’s design.
    - **Control System:**  
      Develop an optimal control system—using PID controllers—to manage:
        - Vertical descent (targeting about 2 m/s downward).
        - Horizontal deceleration (targeting near 0 m/s).
        - Attitude control (ensuring the proper orientation for efficient engine thrust).
    - **Landing Criteria:**  
      The final landing should achieve vertical and horizontal speeds below 2.5 m/s. The secondary objective is to conserve fuel so that ideally around 50 liters (or the equivalent amount in kg) of fuel remain at touchdown.
    - **Simulation Outputs:**  
      The simulator should provide a clear, graphical visualization of:
        - The Moon’s surface.
        - The planned landing trajectory (displayed as a dashed line).
        - The real-time position and movement of the spacecraft.
        - Key parameters (time, altitude, vertical/horizontal speeds, orientation angle, and remaining fuel).

## Project Structure

The project is organized as follows:

```
MoonLandingSimulation/
└── src/
    ├── controller/
    │   └── PIDController.java         // Implements a PID controller with output limits and anti-windup.
    ├── model/
    │   └── Spacecraft.java             // Models the spacecraft’s dynamics, including mass, speeds, and attitude.
    └── simulation/
        ├── Moon.java                  // Contains lunar parameters (radius, gravity, etc.).
        ├── LandingSimulationApp.java  // Main application entry point, managing the menu and simulation panels.
        ├── MenuPanel.java             // Provides an introductory screen with mission details and a Start button.
        └── SimulationPanel.java       // Runs the simulation, updates dynamics, and visualizes the trajectory and state data.
```

## Detailed Explanation

### Background and Crash Analysis
The crash of an autonomous spacecraft is rarely due to a single fault. Instead, it is often a chain of events that includes:
- **Navigation Errors:**  
  Inaccurate readings from star trackers and inertial sensors (possibly due to dust, stray light, or radiation) can lead to misalignment and incorrect trajectory estimates.
- **Control System Failures:**  
  Repeated resets of the onboard computer (caused by radiation or hardware limitations) can result in loss of critical software parameters and control commands.
- **Propulsion Issues:**  
  Inadequate engine thrust—especially when using low-cost, low-redundancy components—can prevent the craft from decelerating sufficiently during the final descent.
- **Design and Budget Constraints:**  
  A limited budget may force the use of lower-quality components and insufficient testing, which can compound the risk of failure.

### Simulation Modeling and Control
In our simulation:
- **Lunar Modeling:**  
  The Moon’s gravitational force (1.622 m/s²) is used as a constant value. The absence of an atmosphere simplifies the dynamic equations.
- **Spacecraft Dynamics:**  
  The spacecraft model includes a variable mass (as fuel is consumed), vertical and horizontal velocities, and an orientation angle.  
  Advanced modeling includes simulated rotational dynamics for attitude control.
- **Control System (PID Controllers):**  
  Three PID controllers are implemented:
    - **Vertical PID:** Targets a descent speed of approximately 2 m/s.
    - **Horizontal PID:** Aims to reduce the horizontal velocity to near 0 m/s.
    - **Orientation PID:** Adjusts the spacecraft’s attitude to ensure that the main engine’s thrust is optimally applied.

  Improvements in the PID controllers (output clamping and anti-windup) ensure a stable and responsive control response, even in dynamic and critical phases of the landing.

- **Fuel Consumption:**  
  Fuel consumption is modeled dynamically, and if fuel runs out before landing, the simulation stops and issues an error message. This is critical for ensuring that the simulation reflects the secondary objective of conserving fuel.

### Visualization and User Interface
The simulator provides:
- **A Menu Screen:**  
  An introductory screen that explains the mission, objectives, and requirements. It includes a “Start Simulation” button.
- **A Simulation Screen:**  
  Displays:
    - A graphical representation of the Moon’s surface (drawn as a curved arc).
    - A planned landing trajectory (displayed as a dashed line).
    - The spacecraft, represented by a rotated icon that reflects its current orientation.
    - Real-time data including time, altitude, vertical and horizontal speeds, orientation angle, and remaining fuel.
- **User Controls:**  
  The interface includes buttons to pause/resume and reset the simulation, along with a slider to adjust simulation speed (from 0.5× to 5× real-time).

## Installation and Execution Instructions

Follow these steps to compile and run the project:

1. **Clone or Download the Project:**  
   Ensure that your project directory is named `MoonLandingSimulation` and follows the structure described above.

2. **Navigate to the Project Directory:**  
   Open a terminal and change the directory to your project folder:
   ```bash
   cd /path/to/MoonLandingSimulation
   ```

3. **Compile the Project:**  
   Run the following command to compile the source files and place the compiled classes in the `bin` folder:
   ```bash
   javac -d bin src/simulation/Moon.java src/controller/PIDController.java src/model/Spacecraft.java src/simulation/LandingSimulationApp.java src/simulation/MenuPanel.java src/simulation/SimulationPanel.java
   ```

4. **Run the Application:**  
   Execute the application with the following command:
   ```bash
   java -cp bin simulation.LandingSimulationApp
   ```

5. **Using the Simulator:**
    - **Menu Screen:**  
      Upon starting, you will see a menu screen with the mission description. Click on "Start Simulation" to begin.
    - **Simulation Screen:**  
      The simulation window will display the Moon's surface, the planned trajectory, and the moving spacecraft along with real-time data.
    - **Controls:**  
      Use the Pause/Resume button to stop or continue the simulation. The Reset button restarts the simulation, and the Speed slider allows you to adjust the simulation speed.
    - **Output:**  
      Final landing conditions are printed to the console when the simulation ends (either upon successful landing or if fuel is exhausted).

## Summary of Reports

- **Crash Analysis Report:**  
  This report described the technical failures (navigation errors, control system resets, propulsion issues, and budget constraints) that could lead to a spacecraft crash. It emphasized that a chain of failures—rather than a single fault—is typically responsible for a mission failure.

- **Simulation Report:**  
  This report detailed the development of the autonomous landing simulator. It covered the physical modeling of the Moon and the spacecraft, the implementation of PID controllers for optimal control, and the visualization of the landing trajectory. The simulation results, including the optimal landing parameters and fuel consumption, are presented graphically for clear comparison.


## Written by
- **[Roni Michaeli](https://github.com/roni5604)