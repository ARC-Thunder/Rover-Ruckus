package org.firstinspires.ftc.teamcode.detectgold;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverruckus.GoldAlignDetector;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class GoldAlignDetection {
    private GoldAlignDetector detector;

    public GoldAlignDetection(HardwareMap hardwareMap) {
        detector = new GoldAlignDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        detector.useDefaults();

        // Optional Tuning
        detector.alignSize = 100; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        detector.alignPosOffset = 0; // How far from center frame to offset this alignment zone.
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005;

        detector.ratioScorer.weight = 5;
        detector.ratioScorer.perfectRatio = 1.0;

        detector.enable();
    }

    /**
     * Returns if the gold element is aligned
     * @return if the gold element is aligned
     */
    public boolean isAligned() {
        return detector.getAligned();
    }

    /**
     * Returns gold element last x-position
     * @return last x-position in screen pixels of gold element
     */
    public double getGoldXPos() {
        return detector.getXPosition();
    }

    public void disable() {
        detector.disable();
    }
}
