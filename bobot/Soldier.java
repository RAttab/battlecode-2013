package bobot;

import battlecode.common.*;


public class Soldier
{

    public static MapLocation electLeader(RobotController rc)
        throws GameActionException
    {
        return null;
    }

    public static void lead(RobotController rc, Navigation nav)
        throws GameActionException
    {

    }

    public static void follow(RobotController rc, Navigation nav)
        throws GameActionException
    {

    }

    public static boolean isBattleBot(RobotType type)
    {
        return type == RobotType.SOLDIER ||
            type == RobotType.ARTILLERY ||
            type == RobotType.MEDBAY;
    }

    public static boolean isMicro(RobotController rc, SenseCache sense)
        throws GameActionException
    {
        return sense.nearbyEnemies().length > 0;
    }

    public static boolean microEnemies(
            RobotController rc, Navigation nav, SenseCache sense)
        throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();
        RobotInfo[] enemies = sense.nearbyEnemies();

        int numEnemies = 0;
        boolean isEngaged = false;

        for (int i = enemies.length; i-- >= 0;) {
            if (!isBattleBot(enemies[i].type)) continue;
            numEnemies++;

            MapLocation loc = enemies[i].location;
            Direction dir = myLoc.directionTo(loc);

            if (myLoc.isAdjacentTo(loc)) {
                isEngaged = true;
                nav.resetDefuse();

                nav.boost(dir, Weights.MICRO_ENEMIES_ADJ);
                nav.boost(dir.opposite(), -Weights.MICRO_ENEMIES_ADJ);

                continue;
            }

            nav.boost(dir, Weights.MICRO_ENEMIES_FAR);
            nav.boost(dir.opposite(), Weights.MICRO_ENEMIES_FAR);
        }

        return isEngaged;
    }

    public static void microAllies(
            RobotController rc,
            Navigation nav,
            SenseCache sense,
            boolean isEngaged)
        throws GameActionException
    {

    }


    public static boolean microMines(
            RobotController rc, Navigation nav, SenseCache sense)
        throws GameActionException
    {
        boolean isDefusing = false;


        return isDefusing;
    }

    public static void run(RobotController rc)
        throws GameActionException
    {
        // Never leave home without it!
        rc.wearHat();

        SenseCache sense = new SenseCache(rc);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            Navigation nav = new Navigation(rc);
            sense.reset();

            if (isMicro(rc, sense)) {
                nav.autoDefuse = false;

                boolean isEngaged = microEnemies(rc, nav, sense);
                if (isEngaged || !microMines(rc, nav, sense))
                    microAllies(rc, nav, sense, isEngaged);
            }
            else {
                nav.autoDefuse = true;
            }


            nav.move();
            rc.yield();
        }
    }

}
