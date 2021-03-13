package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.*;
import frc.robot.userinterface.UserInterface;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * The main Robot class whence all things come.
 */
public class Robot extends TimedRobot {
    private Command auto_command;

    private RobotContainer robotContainer;
    
    private boolean oldLeftTriggerOn = false;
    private boolean oldRightTriggerOn = false;

    public void robotInit() {
        // Setup bot choice, camera feed(s) - see FRC-20

        //Choose from AXIDRIVE (Axiom drivebase), TOASTER, and COMPETITION
        RobotMap.setBot(RobotMap.BotNames.AXIDRIVE);
        System.out.println("Initializing " + RobotMap.botName);

        //drive settings
        Subsystems.driveBase.setDefaultCommand(new TankDrive());
        Subsystems.driveBase.drive.setSafetyEnabled(false);

        //driver controls (buttons)
        UserInterface.driverController.RB.whenPressed(new SlowFast());

        //operator controls (buttons)
        UserInterface.operatorController.LB.whileHeld(new Vomit());
        UserInterface.operatorController.RB.whenPressed(new IntakeToggle());
        
        robotContainer = new RobotContainer();
    }

    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    public void disabledInit() {
        System.out.println("Disabled Initialized");
        CommandScheduler.getInstance().cancelAll();
    }

    public void disabledPeriodic() {}

    public void autonomousInit() {
        System.out.println("Autonomous Initalized");
        CommandScheduler.getInstance().cancelAll();

        // Schedule autonomous command to run
        auto_command = robotContainer.getAutonomousCommand();

        if (auto_command != null) {
            auto_command.schedule();
        }
    }

    public void autonomousPeriodic() {}

    public void teleopInit() { 
        System.out.println("TeleOp Initalized");
        CommandScheduler.getInstance().cancelAll();

        if (auto_command != null) {
            auto_command.cancel();
        }
    }

    public void teleopPeriodic() {

        // Left joystick - intake & transversal
        if (UserInterface.operatorController.getLeftJoystickY() >= 0.4) {
            Subsystems.intake.setIntakeMotors(0.5);
            Subsystems.transversal.setTransversalMotors(0.5);
        } else if (UserInterface.operatorController.getLeftJoystickY() <= -0.4) {
            Subsystems.intake.setIntakeMotors(-0.5);
            Subsystems.transversal.setTransversalMotors(-0.5);
        } else {
            Subsystems.intake.stopIntakeMotors();
            Subsystems.transversal.stopTransversalMotors();
        }

        // Left trigger - hold for warmup flywheel
        boolean isLeftTriggerOn = UserInterface.operatorController.getLeftTrigger() >= 0.4;
        if (isLeftTriggerOn && !oldLeftTriggerOn) {
            Subsystems.flyboi.spinWheel(0.5);
        } else if (!isLeftTriggerOn && oldLeftTriggerOn) {
            Subsystems.flyboi.stopWheel();
        }
        oldLeftTriggerOn = isLeftTriggerOn;
        
        // Right trigger - hold for shoot sequence
        boolean isRightTriggerOn = UserInterface.operatorController.getRightTrigger() >= 0.4;
        if (isRightTriggerOn && !oldRightTriggerOn) {
            new ShootSequence().schedule();
        } else if (!isRightTriggerOn && oldRightTriggerOn) {
            new ShootStop().schedule();
        }
        oldRightTriggerOn = isRightTriggerOn;

        // D-Pad - climb
        int direction = UserInterface.operatorController.getPOVAngle();
        if (direction == 0) { //DPad up
            Subsystems.climber.extendClimber(0.5);
        } else if (direction == 180) { // DPad down
            Subsystems.climber.retractClimber(0.5);
        } else {
            Subsystems.climber.stopClimber();
        }
    }
}