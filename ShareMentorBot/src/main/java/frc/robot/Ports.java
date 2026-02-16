package frc.robot;

import com.ctre.phoenix6.CANBus;
import frc.robot.generated.TunerConstants;
public final class Ports {
    // Controllers
    public static final int kDriverControllerPort = 0;
    
    // CAN Buses
    public static final CANBus kRoboRioCANBus = new CANBus("rio");
    public static final CANBus kCANivoreCANBus = new CANBus(TunerConstants.kCANBusName);

}
