package bobot;

import battlecode.common.*;

/** Controls the all important hat wearing logic.

    Wearing hat sure is hard work now though...
 */
public class Hat
{
    // I know this sounds like heresy but we can't afford 40 in the early game.
    private static double HAT_POWER_THRESHOLD = 500.0;

    private static boolean hatless = true;

    public static void wearHat(RobotController rc)
        throws GameActionException
    {
        if (!hatless) return;
        if (Clock.getBytecodeNum() >= 4500) return;
        if (rc.getTeamPower() < HAT_POWER_THRESHOLD) return;

        System.err.println("I have a hat. Your victory is meaningless.");
        rc.wearHat();
        hatless = false;

    }

}