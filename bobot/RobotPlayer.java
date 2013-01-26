package bobot;

import battlecode.common.*;


public class RobotPlayer
{

    private static void experiment(RobotController rc)
        throws GameActionException
    {

        while (true) {

            int sight = RobotType.SOLDIER.sensorRadiusSquared;
            if (rc.hasUpgrade(Upgrade.VISION))
                sight += GameConstants.VISION_UPGRADE_BONUS;


            Robot[] enemies = rc.senseNearbyGameObjects(
                    Robot.class, sight, rc.getTeam().opponent());

            if (enemies.length > 0) {
                RobotInfo info = rc.senseRobotInfo(enemies[0]);
                rc.setIndicatorString(0,
                        "loc=" + info.location + ", distSq=" +
                        info.location.distanceSquaredTo(rc.getLocation()));
            }


            if (!rc.isActive()) { rc.yield(); continue; }
            if (!rc.hasUpgrade(Upgrade.DEFUSION)) { rc.yield(); continue; }
            // if (!rc.hasUpgrade(Upgrade.VISION)) { rc.yield(); continue; }


            MapLocation myLoc = rc.getLocation();
            MapLocation one = myLoc.add(Direction.WEST);
            MapLocation two = myLoc.add(Direction.WEST, 2);
            MapLocation three = myLoc.add(Direction.WEST, 3);

            RobotInfo frontAlly = null;
            boolean frontIsDefusing = false;
            {
                Robot r = (Robot) rc.senseObjectAtLocation(one);
                if (r != null) {
                    frontAlly = rc.senseRobotInfo(r);
                    frontIsDefusing = frontAlly.roundsUntilMovementIdle > 0;
                }
            }

            RobotInfo backAlly = null;
            boolean backIsDefusing = false;
            {
                Robot r = (Robot) rc.senseObjectAtLocation(myLoc.add(Direction.EAST));
                if (r != null) {
                    backAlly = rc.senseRobotInfo(r);
                    backIsDefusing = backAlly.roundsUntilMovementIdle > 0;
                }
            }

            rc.setIndicatorString(1,
                    "front=" + (frontAlly != null) + ", frontDefusing=" + frontIsDefusing +
                    ", back=" + (backAlly != null) + ", backDefusing=" + backIsDefusing);


            if (rc.senseMine(one) != null && (backAlly == null || !backIsDefusing)) {
                rc.setIndicatorString(2, "mine=1");
                rc.defuseMine(one);
            }

            else if (rc.senseMine(two) != null && frontAlly != null && !frontIsDefusing) {
                rc.setIndicatorString(2, "mine=2");
                rc.defuseMine(two);
            }

            else if (rc.senseMine(three) != null && !rc.canMove(Direction.WEST)) {
                rc.setIndicatorString(2, "mine=3");
                rc.defuseMine(three);
            }

            else rc.move(Direction.WEST);

            rc.yield();
        }
    }


    public static void run(RobotController rc)
    {
        while (true) {
            try {
                if (rc.getType() == RobotType.SOLDIER) experiment(rc); // Soldier.run(rc);
                if (rc.getType() == RobotType.HQ) {

                    if (!rc.hasUpgrade(Upgrade.DEFUSION))
                        rc.researchUpgrade(Upgrade.DEFUSION);

                    else if (!rc.hasUpgrade(Upgrade.VISION))
                        rc.researchUpgrade(Upgrade.VISION);
                }
            }
            catch(Exception e) { e.printStackTrace(); }
            rc.yield();
        }
    }

}
