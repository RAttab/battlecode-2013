package fieldo;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{

    public static void run(RobotController rc) {
        Storage.calculateValues(rc);

        while (true) {
            try {

                if (rc.getType() == RobotType.SOLDIER)
                    Soldier.run(rc);
                else if (rc.getType() == RobotType.HQ)
                    Headquarter.run(rc);
                else
                    Bases.run(rc);
                rc.yield();
            }
            catch(Exception e) { e.printStackTrace(); rc.breakpoint();}
        }
    }

}
