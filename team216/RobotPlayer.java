package team216;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{

    public static void run(RobotController rc)
    {
        while (true) {
            try {
                RobotType type = rc.getType();

                if (type == RobotType.SOLDIER) Soldier.run(rc);
                else if (type == RobotType.HQ) Headquarter.run(rc);
                else Bases.run(rc);

            }
            catch(Exception e) { e.printStackTrace(); }
            rc.yield();
        }
    }

}