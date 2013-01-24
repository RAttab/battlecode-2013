package bobot;

import battlecode.common.*;


public class RobotPlayer
{

    private static void doSoldier(RobotController rc)
        throws GameActionException
    {

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }
            if (!rc.hasUpgrade(Upgrade.VISION)) { rc.yield(); continue; }

            int sight = RobotType.SOLDIER.sensorRadiusSquared;
            if (rc.hasUpgrade(Upgrade.VISION))
                sight += GameConstants.VISION_UPGRADE_BONUS;

            Robot enemies[] = rc.senseNearbyGameObjects(
                    Robot.class, sight, rc.getTeam().opponent());

            if (enemies.length <= 0) rc.move(Direction.WEST);

            rc.yield();
        }
    }


    public static void run(RobotController rc)
    {
        while (true) {
            try {
                if (rc.getType() == RobotType.SOLDIER) doSoldier(rc);
                if (rc.getType() == RobotType.HQ) {
                    if (!rc.hasUpgrade(Upgrade.VISION))
                        rc.researchUpgrade(Upgrade.VISION);
                }
            }
            catch(Exception e) { e.printStackTrace(); }
            rc.yield();
        }
    }

}
