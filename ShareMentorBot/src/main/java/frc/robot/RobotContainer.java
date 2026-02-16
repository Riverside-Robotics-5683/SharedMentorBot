// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static frc.robot.Ports.kDriverControllerPort;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.Driving;
import frc.robot.commands.ManualDriveCommand;
import frc.robot.subsystems.Swerve;
import frc.util.SwerveTelemetry;

public class RobotContainer {
  private final Swerve swerve = new Swerve();

  private final SwerveTelemetry swerveTelemetry =
      new SwerveTelemetry(Driving.kMaxSpeed.in(MetersPerSecond));

  private final CommandXboxController driver = new CommandXboxController(kDriverControllerPort);

  public RobotContainer() {
    // Push CTRE drivetrain state to NetworkTables (pose, module states, speeds, etc.)
    swerve.registerTelemetry(swerveTelemetry::telemeterize);

    configureDrive();
    configureButtons();
  }

  private void configureDrive() {
    var drive = new ManualDriveCommand(
        swerve,
        () -> -driver.getLeftY(),   // forward
        () -> -driver.getLeftX(),   // left
        () -> -driver.getRightX()   // rotation
    );
    swerve.setDefaultCommand(drive);

    // Re-seed field-centric when driver presses BACK
    driver.back().onTrue(Commands.runOnce(drive::seedFieldCentric));
  }

  private void configureButtons() {
    // Optional: add a quick “stop” or “x-lock” later if you want.
  }
}