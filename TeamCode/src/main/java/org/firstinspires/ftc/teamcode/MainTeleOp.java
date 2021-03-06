package org.firstinspires.ftc.teamcode;

import com.andoverrobotics.core.drivetrain.MecanumDrive;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@TeleOp(name = "Main TeleOp", group = "ARC Thunder")
public class MainTeleOp extends OpMode {
    //The distance between the front wheels, the back wheels, and the front and the back wheels, in inches. Currently unset because measuring is hard.
    private static final double FRONT_WHEEL_DISTANCE = 14.8, BACK_WHEEL_DISTANCE = 14.8, FRONT_BACK_DISTANCE = 12.25, ROBOT_DIAMETER = 2 * Math.sqrt(Math.pow(1.0 / 2 * (FRONT_WHEEL_DISTANCE + BACK_WHEEL_DISTANCE) / 2, 2) + Math.pow(1 / 2 * FRONT_BACK_DISTANCE, 2));
    //TICKS_PER_WHEEL_360: how many ticks of a motor to make a wheel turn 360
    //ticksPer360: how many encoder ticks required to cause a full rotation for the robot, when this amount is applied to the left and right motors in opposite directions
    //ticksPer360 is currently calculated by multiplying ticksPerInch by the circumference of the circle with the rear axle as a diameter, as those are the wheels that are moving
    //ticksPerInch and ticksPer360 are rounded to the nearest integer
    private static final int WHEEL_DIAMETER_IN = 4, TICKS_PER_WHEEL = 1440, TICKS_PER_INCH = (int) (TICKS_PER_WHEEL / (Math.PI * WHEEL_DIAMETER_IN) + 0.5), TICKS_PER_360 = (int) (TICKS_PER_INCH * Math.PI * ROBOT_DIAMETER + 0.5);
    // KNOWN MOTOR TICKS (TICKS_PER_WHEEL_360):
    //     Tetrix DC Motors: 1440
    //     AndyMark NeveRest Motors: 1120 (Not 100% sure)
    private final double LIFT_HEIGHT_IN = 6.375;
    private final double PULLEY_DIAMETER_MM = 25;
    private final double SLOW_MODE_DIVISOR = 5;

    protected MecanumDrive mecanumDrive;
    protected DcMotor motorLatch, motorExtend;

    protected CRServo crServoSweep, crServoBox;
    protected boolean isInSlowMode = false;

    public void init() {
        DcMotor motorFL = hardwareMap.dcMotor.get("motorFL");
        DcMotor motorFR = hardwareMap.dcMotor.get("motorFR");
        DcMotor motorBL = hardwareMap.dcMotor.get("motorBL");
        DcMotor motorBR = hardwareMap.dcMotor.get("motorBR");

        crServoSweep = hardwareMap.crservo.get("crServoSweep");
        crServoSweep.setDirection(DcMotorSimple.Direction.REVERSE);

        motorFR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBR.setDirection(DcMotorSimple.Direction.REVERSE);

        motorLatch = hardwareMap.dcMotor.get("motorLatch");
        motorLatch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLatch.setDirection(DcMotorSimple.Direction.REVERSE);

        motorExtend = hardwareMap.dcMotor.get("motorExtend");
        motorExtend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorExtend.setDirection(DcMotorSimple.Direction.REVERSE);

        crServoSweep = hardwareMap.crservo.get("crServoSweep");
        crServoBox = hardwareMap.crservo.get("crServoBox");

        mecanumDrive = MecanumDrive.fromCrossedMotors(motorFL, motorFR, motorBL, motorBR, this, TICKS_PER_INCH, TICKS_PER_360);
        mecanumDrive.setDefaultDrivePower(0.5);
    }

    public void loop() {
        double sweepPower = 0, liftPower = 0, extendPower = 0, boxPower = 0;

        if (gamepad1.left_bumper)
            sweepPower = -1;
        else if (gamepad1.right_bumper)
            sweepPower = 1;

        if (gamepad1.start)
            isInSlowMode = !isInSlowMode;

        if (gamepad1.left_trigger >= 0.25)
            liftPower = -gamepad1.left_trigger;
        else if (gamepad1.right_trigger >= 0.25)
            liftPower = gamepad1.right_trigger;

        if(gamepad1.dpad_right)
            extendPower = 0.8; // Extend
        else if (gamepad1.dpad_left)
            extendPower = -0.8; // Retract

        if(gamepad1.dpad_up)
            boxPower = 1; // Up
        else if (gamepad1.dpad_down)
            boxPower = -1; // Down

        telemetry.addData("Slow Mode", isInSlowMode ? "ACTIVE" : "INACTIVE");
        telemetry.update();

        motorLatch.setPower(liftPower / (isInSlowMode ? SLOW_MODE_DIVISOR : 1));
        motorExtend.setPower(extendPower / (isInSlowMode ? SLOW_MODE_DIVISOR : 1));
        crServoSweep.setPower(sweepPower / (isInSlowMode ? SLOW_MODE_DIVISOR : 1));
        crServoBox.setPower(boxPower / (isInSlowMode ? SLOW_MODE_DIVISOR : 1));

        if (Math.abs(gamepad1.right_stick_x) > 0.1) {
            mecanumDrive.setRotationPower(gamepad1.right_stick_x / (isInSlowMode ? SLOW_MODE_DIVISOR : 1));
        } else {
            mecanumDrive.setStrafe(gamepad1.left_stick_x, gamepad1.left_stick_y, 1 / (isInSlowMode ? SLOW_MODE_DIVISOR : 1));
        }


    }

    @Override
    public void stop() {
        super.stop();
    }

    protected void checkForInterrupt() throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
    }
}