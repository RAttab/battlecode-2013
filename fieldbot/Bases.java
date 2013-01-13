package fieldbot;

import battlecode.common.*;

public class Bases
{

    static final int MAX_ROBOTS = 10;
    static final double ARTY_THRESHOLD = 10.0;

    private static void artillery(RobotController rc) throws GameActionException
    {
        Robot enemies[] = rc.senseNearbyGameObjects(
                Robot.class, RobotType.ARTILLERY.attackRadiusMaxSquared,
                rc.getTeam().opponent());

        MapLocation target = null;
        double targetScore = -1;

        int steps = Math.max(1, enemies.length / MAX_ROBOTS);

        for (int i = 0; i < enemies.length; i += steps) {
            MapLocation pos = rc.senseRobotInfo(enemies[i]).location;

            Robot robots[] = rc.senseNearbyGameObjects(
                    Robot.class, pos, 1, null);

            double score = 0;
            for (int j = 0; j < robots.length; ++j)
                score += rc.senseRobotInfo(robots[j]).energon;

            if (score > targetScore) {
                targetScore = score;
                target = pos;
            }
        }

        if (target != null) {
            String str = "target=" + target.toString() + ", score=" + targetScore;
            rc.setIndicatorString(0, str);

            if (targetScore >= ARTY_THRESHOLD) {
                if (rc.canAttackSquare(target)) rc.attackSquare(target);
            }
        }
    }


    private static void broadcast(RobotController rc) throws GameActionException
    {

    }


    public static void run(RobotController rc) throws GameActionException
    {
        MapLocation coord = rc.getLocation();
        boolean isArty = rc.getType() == RobotType.ARTILLERY;

        while (true) {

            if (!rc.isActive()) { rc.yield(); continue; }

            if (isArty) artillery(rc);
            else broadcast(rc);

            rc.yield();
        }
    }
}