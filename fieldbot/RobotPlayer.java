package fieldbot;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{


    private static void encampment(RobotController rc) throws GameActionException
    {
        while (true) {

            rc.yield();
        }
    }


    public static void run(RobotController rc)
    {
        while (true) {
            try {
                RobotType type = rc.getType();

                if (type == RobotType.SOLDIER) Soldier.run(rc);
                else if (type == RobotType.HQ) Headquarter.run(rc);
                else encampment(rc);
            }
            catch(Exception e) { e.printStackTrace(); }
            rc.yield();
        }
    }

}