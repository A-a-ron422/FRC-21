package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotMap;
import frc.robot.subsystems.*;

/**
 * Toggles whether the intake is extended (in the down position) or retracted (in the up position).
 */
public class IntakeToggle extends CommandBase {

    public IntakeToggle() {
        setName("IntakeToggle");
        addRequirements(Subsystems.intake);
    }

    public void initialize() {
        if (RobotMap.isIntakeDown) {
            Subsystems.intake.intakeRetract();
        } else {
            Subsystems.intake.intakeExtend();
        }
        RobotMap.isIntakeDown = !RobotMap.isIntakeDown;
    }

    public boolean isFinished() {
        return true;
    }

}