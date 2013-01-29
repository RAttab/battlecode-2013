package bobot;

import battlecode.common.*;


public class RobotPlayer
{

    public static void run(RobotController rc)
    {
        Communication.setup(rc);

        while (true) {
            try {
                if (rc.getType() == RobotType.SOLDIER) Soldier.run(rc);
                if (rc.getType() == RobotType.HQ) Headquarter.run(rc);
                else
                    Bases.run(rc);
            }
            catch(Exception e) {
                e.printStackTrace();
                rc.breakpoint();
            }
            rc.yield();
        }
    }

}
