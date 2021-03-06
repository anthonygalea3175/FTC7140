/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Autonomous 1", group="7140")
public class Autonomous_10546_Red_Corner extends Code10546 {

    /* Declare OpMode members. */
    Hardware7140         robot   = new Hardware7140();   // Use 7140's hardware
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 7 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 20;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;
    static final double     SHOOTER_SPEED           = 1;        //sets shooter speed
    static final double     LIFT_SPEED              = 1;


    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.leftMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        robot.leftMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.shooter1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.shooter2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                          robot.leftMotor1.getCurrentPosition(),
                          robot.leftMotor2.getCurrentPosition(),
                          robot.rightMotor1.getCurrentPosition(),
                          robot.rightMotor2.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        shootBalls(SHOOTER_SPEED, LIFT_SPEED);      //S1: Shoot Balls (7 seconds)
        encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S2: Forward 47 Inches with 5 Sec timeout
        encoderDrive(TURN_SPEED,   -18.8, 18.8, 4.0);  // S3: Turn Right 12 Inches with 4 Sec timeout
        encoderDrive(DRIVE_SPEED,  48, 48, 5.0);  // S4: Drive 47 Inches with 5 Sec timeout
        //21 second autonomous

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void shootBalls(double shootspeed,
                              double liftspeed) {
        robot.shooter1.setPower(shootspeed);
        robot.shooter2.setPower(shootspeed);
        sleep(2000);
        robot.lift.setPower(liftspeed);
        sleep(5000);
        robot.shooter1.setPower(0);
        robot.shooter2.setPower(0);
        robot.lift.setPower(0);

    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget1;
        int newLeftTarget2;
        int newRightTarget1;
        int newRightTarget2;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget1 = robot.leftMotor1.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newLeftTarget2 = robot.leftMotor2.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget1 = robot.rightMotor1.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newRightTarget2 = robot.rightMotor2.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            robot.leftMotor1.setTargetPosition(newLeftTarget1);
            robot.leftMotor2.setTargetPosition(newLeftTarget2);
            robot.rightMotor1.setTargetPosition(newRightTarget1);
            robot.rightMotor2.setTargetPosition(newRightTarget2);

            // Turn On RUN_TO_POSITION
            robot.leftMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftMotor1.setPower(Math.abs(speed));
            robot.leftMotor2.setPower(Math.abs(speed));
            robot.rightMotor1.setPower(Math.abs(speed));
            robot.rightMotor2.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (robot.leftMotor1.isBusy() && robot.rightMotor1.isBusy() && robot.leftMotor2.isBusy() && robot.rightMotor2.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget1, newLeftTarget2, newRightTarget1, newRightTarget2);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                                            robot.leftMotor1.getCurrentPosition(),
                                            robot.leftMotor2.getCurrentPosition(),
                                            robot.rightMotor1.getCurrentPosition(),
                                            robot.rightMotor2.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.leftMotor1.setPower(0);
            robot.leftMotor2.setPower(0);
            robot.rightMotor1.setPower(0);
            robot.rightMotor2.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.leftMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.leftMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }
}
