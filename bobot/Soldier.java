package bobot;

import battlecode.common.*;


public class RobotPlayer
{

    public static MapLocation electLeader(RobotController rc)
        throws GameActionException
    {

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
            type == RobotType.MEDBARY;
    }

    // There's no logic in here yet. I just layed down some ground work.
    public static boolean micro(
            RobotController rc, Navigation nav, SenseCache sense)
        throws GameActionException
    {
        MapLocation[] enemies = sense.nearbyEnemies(0);
        if (enemies.length <= 0) return false;

        int numEnemies = 0;
        for (int i = enemies.length; i-- >= 0;) {
            RobotInfo info = sense.info(enemies[i], 0);
            if (!isBattleBot(info.type)) continue;

            numEnemies++;
        }

        MapLocation[] allies = sense.nearbyAllies(0);
        int numAllies = 0;

        for (int i = allies.length; i-- >= 0;) {
            RobotInfo info = sense.info(allies[i], 1);
            if (!isBattleBot(info.type)) continue;

            numAllies++;
        }

        bool numericMajority =
            numAllies * Weights.MICRO_ALLIES - numEnemies > 0;

        nav.canDefuse = numericMajority;

        return true;
    }


    public static void run(RobotController rc)
    {
        // Never leave home without it!
        rc.wearHat();

        SenseCache sense = new SenseCache(rc);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            Navigation nav = new Navigation(rc);
            sense.reset();

            boolean inMicro = micro(rc, nav, sense);


            nav.move(true);
            rc.yield();
        }
    }

}
