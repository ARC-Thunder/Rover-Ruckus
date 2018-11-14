package org.firstinspires.ftc.teamcode;

import com.andoverrobotics.core.drivetrain.TankDrive;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.detectgold.GoldDetection;

@Autonomous(name = "Main Autonomous", group = "Autonomous")
public class MainAutonomous extends LinearOpMode {
    //The distance between the front wheels, the back wheels, and the front and the back wheels, in inches. Currently unset because measuring is hard.
    private static final double FRONT_WHEEL_DISTANCE = 14.8, BACK_WHEEL_DISTANCE = 14.8, FRONT_BACK_DISTANCE = 12.25, ROBOT_DIAMETER = 2 * Math.sqrt(Math.pow(1 / 2 * (FRONT_WHEEL_DISTANCE + BACK_WHEEL_DISTANCE) / 2, 2) + Math.pow(1 / 2 * FRONT_BACK_DISTANCE, 2));
    private final double CAMERA_HEIGHT = 12.75; // How high off the ground the phone's camera is, in inches
    // On the test bot, this is 5.2 inches if the camera is at the bottom or 9 inches if the camera is at the top

    //TICKS_PER_WHEEL_360: how many ticks of a motor to make a wheel turn 360
    //ticksPer360: how many encoder ticks required to cause a full rotation for the robot, when this amount is applied to the left and right motors in opposite directions
    //ticksPer360 is currently calculated by multiplying ticksPerInch by the circumference of the circle with the rear axle as a diameter, as those are the wheels that are moving
    //ticksPerInch and ticksPer360 are rounded to the nearest integer
    private static final int WHEEL_DIAMETER_IN = 4, TICKS_PER_WHEEL = 1440, TICKS_PER_INCH = (int) (TICKS_PER_WHEEL / (Math.PI * WHEEL_DIAMETER_IN) + 0.5), TICKS_PER_360 = (int) (TICKS_PER_INCH * Math.PI * 10.55 + 0.5);
    // KNOWN MOTOR TICKS (TICKS_PER_WHEEL_360):
    //     Tetrix DC Motors: 1440
    //     AndyMark NeveRest Motors: 1120 (Not 100% sure)

    private final double CAM_FOCAL_LENGTH = 751.0, GOLD_WIDTH_IN = 2; // Approximate focal length of a Moto G (2nd gen): 637.5 Old focal length: 560
    private GoldDetection goldDetection;

    private final String VUFORIA_KEY = "AQRacK7/////AAAAGea1bsBsYEJvq6S3KuXK4PYTz4IZmGA7SV88bdM7l26beSEWkZTUb8H352Bo/ZMC6krwmfEuXiK7d7qdFkeBt8BaD0TZAYBMwHoBkb7IBgMuDF4fnx2KiQPOvwBdsIYSIFjiJgGlSj8pKZI+M5qiLb3DG3Ty884EmsqWQY0gjd6RNhtSR+6oiXazLhezm9msyHWZtX5hQFd9XoG5npm4HoGaZNdB3g5YCAQNHipjTm3Vkf71rG/Fffif8UTCI1frmKYtb4RvqiixDSPrD6OG6YmbsPOYUt2RZ6sSTreMzVL76CNfBTzmpo2V0E6KKP2y9N19hAum3GZu3G/1GEB5D+ckL/CXk4JM66sJw3PGucCs";
    private final double MAX_TRAVEL = Math.sqrt(Math.pow(24, 2) + Math.pow(24, 2));

    private DcMotor motorL, motorR;
    private TankDrive tankDrive;

    @Override
    public void runOpMode() {
        setup();

        while (opModeIsActive()) {
            // TODO: Move to location
            goldDetection = new GoldDetection(CAM_FOCAL_LENGTH, GOLD_WIDTH_IN, MAX_TRAVEL, CAMERA_HEIGHT, hardwareMap, VUFORIA_KEY);

            double[] goldOffset = goldDetection.getGoldOffset(); // Format: [distanceToTravel, roundedAngle]
            double distanceToTravel = goldOffset[0];
            int roundedAngle = (int)(goldOffset[1]);

            tankDrive.rotateClockwise(roundedAngle, 0.5);
            tankDrive.driveForwards(distanceToTravel, 0.5);

            tankDrive.driveBackwards(distanceToTravel, 0.5);
            tankDrive.rotateClockwise(-roundedAngle, 0.5);
        }
    }

    private void setup() {
        telemetry.addData("Status", "Gold Detection Test");

        motorR = hardwareMap.dcMotor.get("motorR");
        motorL = hardwareMap.dcMotor.get("motorL");
        motorL.setDirection(DcMotorSimple.Direction.REVERSE);

        motorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        tankDrive = InterruptableTankDrive.fromMotors(motorL, motorR, this, TICKS_PER_INCH, TICKS_PER_360);

        waitForStart();
    }
}