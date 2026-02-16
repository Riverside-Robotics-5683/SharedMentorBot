FRC Swerve Project – Code Architecture Summary
High-Level Architecture
Robot
 └── RobotContainer
      ├── Swerve (Subsystem)
      ├── ManualDriveCommand (Teleop)
      └── SwerveTelemetry
And:
	TunerConstants = hardware configuration
	Constants = robot-wide tuning values
	Ports = controller ports only
	util = small helper classes
________________________________________

Main (class)
What it does:
Entry point for the robot program.
Method:
main()
Starts the WPILib framework and creates your Robot class.
You never really touch this.
________________________________________
Robot (class)
What it does:
Controls the robot lifecycle (init, periodic, disabled, etc.)
Key Methods:

Robot()
	Starts logging
	Sets brownout voltage
	Creates RobotContainer (this builds everything else)

robotPeriodic()
	Runs CommandScheduler
	This is what makes your drive command execute every 20ms
	Without this → nothing moves.
________________________________________
RobotContainer (Class)
What it does:
Wires everything together.
Creates:
	Swerve subsystem
	ManualDriveCommand
	Xbox controller

Methods:
RobotContainer()
	Registers telemetry callback
	Calls:
	configureDrive()
	configureButtons()

configureDrive()
	Creates the teleop drive command:
	ManualDriveCommand drive = new ManualDriveCommand(...)
	Maps:
		Left Y → forward/back
		Left X → strafe
		Right X → rotate
	Then sets it as the default command:
		swerve.setDefaultCommand(drive);
		That means:
		Whenever nothing else is using drivetrain, it drives.
Also binds:
	Back button → reseed field-centric
	configureButtons()
	Currently empty.
	Future location for:
		X-lock
		Slow mode
		Auto align
		etc.
________________________________________

Swerve [Subsystem] (class)
What it does:
	Represents the drivetrain.
Extends:
	TunerSwerveDrivetrain (which is CTRE’s generated swerve class)
	So most motor math is handled by CTRE. 
Constructor

Swerve()
Passes:
	Drivetrain constants
	Module constants
	Odometry tuning
	Vision tuning
Builds the entire drivetrain:
	4 modules
	8 motors
	4 CANCoders
	1 Pigeon

periodic()
	Handles:
	Alliance perspective (Blue forward = 0°, Red forward = 180°)
	Seeds field-centric once
	Reapplies perspective when disabled
	This keeps field-centric consistent across matches.

applyRequest(...)
	Wraps a CTRE SwerveRequest into a WPILib Command.
	This is how auto and teleop send movement commands.

followPath(SwerveSample sample)
	This is the path follower. We might change this later.
	Used for autonomous (Choreo): 
	Gets desired speeds from trajectory
		Adds PID correction to:
		X
		Y
		Heading
	Sends final command to drivetrain
________________________________________

ManualDriveCommand (class)
What it does:
Teleop driving logic.
Implements:
	Field-centric driving
	Manual rotation
	Automatic heading lock

Internal State Machine
IDLING
DRIVING_WITH_MANUAL_ROTATION
DRIVING_WITH_LOCKED_HEADING

initialize()
	Resets:
		State
		Locked heading
		Stopwatch
execute()
	Runs every 20ms.
	Steps:
	1.	Read smoothed joystick input
	2.	Determine state:
		No input → IDLE
		Rotation input → Manual rotate
		Rotation stopped for 0.25s → Lock heading
	3.	Send correct CTRE request:
		Idle
		FieldCentric
		FieldCentricFacingAngle
________________________________________

Heading Lock Logic (class)
If driver stops rotating:
	Start timer
	After 0.25 seconds
	Lock heading to current angle
This gives:
	Smooth driving
	No snap when rotation stops
	Better aiming feel
________________________________________

DriveInputSmoother (class)
What it does:
Makes joystick feel better.
Applies:
	0.15 deadband
	Exponential curve (1.5 power)
Prevents twitchy control.
________________________________________

ManualDriveInput (class)
Simple container for:
	forward
	left
	rotation
Also has:
	hasTranslation()
	hasRotation()
Used by drive command.
________________________________________
Stopwatch (class)
Simple timer utility.
Used for:
	0.25s heading lock delay
________________________________________

SwerveTelemetry (class)
What it does:
Publishes drivetrain state to:
	NetworkTables
	SmartDashboard
	Field2d
	Mechanism2d widgets
Shows:
	Robot pose
	Module angles
	Module speeds
	Odometry frequency
Purely diagnostic.
Does NOT control anything.
________________________________________

TunerConstants (class)
What it does:
Defines ALL hardware configuration.
Contains:
	CAN bus name (kCANBusName)
	Pigeon ID
	Motor IDs
	CANCoder IDs
	Encoder offsets
	Gear ratios
	Wheel radius
	Module positions
	Current limits
	Speed at 12V

TunerConstants (class) Cont’d
Also builds:
	DrivetrainConstants
	4 SwerveModuleConstants
This file tells CTRE how your physical robot is built.
If something hardware-related is wrong → look here first.
________________________________________
Constants (class)
Robot-wide tuning values:
Driving
	Max speed
	Max rotational rate
	Deadband for heading lock
________________________________________
Ports (class)
Clean and minimal:
	Driver controller port
	CAN bus references (linked to TunerConstants)
________________________________________
Big Picture Flow
When driver moves joystick:
Xbox Controller
    ↓
ManualDriveCommand
    ↓
SwerveRequest (CTRE)
    ↓
Swerve Subsystem
    ↓
TunerSwerveDrivetrain
    ↓
TalonFX motors + CANCoders + Pigeon
________________________________________
Overall Assessment
Your structure is:
	Clean
 	Modular
	Industry-style separation
	Student-friendly
	Ready for autos
	Ready for vision integration

