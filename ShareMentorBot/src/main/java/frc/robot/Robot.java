package frc.robot;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  

  public Robot() {
    // Start WPILib datalogging early (before subsystems init)
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog(), true);

    // Log NetworkTables entries (including your SwerveTelemetry topics)
    NetworkTableInstance.getDefault().startEntryDataLog(DataLogManager.getLog(), "/DriveState", "");

    new RobotContainer();
    RobotController.setBrownoutVoltage(Volts.of(6.1));
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }
}