package bobot;

import battlecode.common.*;

/** Controls all the important hat wearing logistics.

    Wearing hat sure is hard work now though...
 */
public class Hat
{
    // I know this sounds like heresy but we can't afford 40 in the early game.
    private static double POWER_THRESHOLD = 500.0;
    private static int BYTECODE_THRESHOLD = 4500;

    public static boolean hatless = true;

    public static void wearHat(RobotController rc)
        throws GameActionException
    {
        if (!hatless) return;
        if (Clock.getBytecodeNum() >= BYTECODE_THRESHOLD) return;
        if (rc.getTeamPower() < POWER_THRESHOLD) return;

        System.err.println("I have a hat. This battle is now meaningless.");
        rc.wearHat();
        hatless = false;
    }

}