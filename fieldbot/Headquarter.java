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

        if (spawned) nextSpawn = -1;
    }


    /**
     *
     */
    public static void research(RobotController rc) throws GameActionException
    {
        if (!rc.hasUpgrade(Upgrade.DEFUSION))
            rc.researchUpgrade(Upgrade.DEFUSION);

        else if (!rc.hasUpgrade(Upgrade.FUSION))
            rc.researchUpgrade(Upgrade.FUSION);

        else if (!rc.hasUpgrade(Upgrade.VISION))
            rc.researchUpgrade(Upgrade.VISION);

        else rc.researchUpgrade(Upgrade.NUKE);
    }


    public static void run(RobotController rc) throws GameActionException
    {
        MapLocation coord = rc.getLocation();

        while (true) {

            if (!rc.isActive()) { rc.yield(); continue; }

            if (nextSpawn < 0) nextSpawn = RESEARCH_WINDOW;

            int round = Clock.getRoundNum();

            if (nextSpawn <= round) spawn(rc, coord);
            else research(rc);

            rc.yield();
        }
    }

}