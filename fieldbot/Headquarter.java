package fieldbot;

import battlecode.common.*;

/**
 *
 */
public class Headquarter
{

    private static final int RESEARCH_WINDOW = 2;


    private static int nextSpawn = RESEARCH_WINDOW;
    private static int nextResearch = 0;


    /**
     *
     */
    private static boolean trySpawn(RobotController rc, int ord)
        throws GameActionException
    {
        Direction dir = Utils.dirByOrd[ord & Utils.dirOrdMask];
        if (!rc.canMove(dir)) return false;

        rc.spawn(dir);
        return true;
    }


    /**
     *
     */
    private static void spawn(RobotController rc, MapLocation coord)
        throws GameActionException
    {
        Direction dir = coord.directionTo(rc.senseEnemyHQLocation());
        int dirOrd = dir.ordinal();
        boolean spawned = false;

        for (int i = 0; i < 5; ++i) {
            spawned = trySpawn(rc, dirOrd + i);
            spawned = spawned || trySpawn(rc, dirOrd - i);

            if (spawned) break;
        }

        if (spawned) {
            nextResearch =
                Clock.getRoundNum() + GameConstants.HQ_SPAWN_DELAY;

            nextSpawn = nextResearch + RESEARCH_WINDOW;
        }
    }


    /**
     *
     */
    public static void research(RobotController rc) throws GameActionException
    {
        if (!rc.hasUpgrade(Upgrade.DEFUSION))
            rc.researchUpgrade(Upgrade.DEFUSION);

        else if (!rc.hasUpgrade(Upgrade.VISION))
            rc.researchUpgrade(Upgrade.VISION);

        else if (!rc.hasUpgrade(Upgrade.FUSION))
            rc.researchUpgrade(Upgrade.FUSION);

        else rc.researchUpgrade(Upgrade.NUKE);
    }


    public static void run(RobotController rc) throws GameActionException
    {
        MapLocation coord = rc.getLocation();

        final int ordMask = (8 - 1);

        while (true) {

            if (!rc.isActive()) { rc.yield(); continue; }

            int round = Clock.getRoundNum();

            if (nextSpawn <= round) spawn(rc, coord);
            if (nextResearch <= round) research(rc);

            rc.yield();
        }
    }

}