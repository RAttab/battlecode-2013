package bobot;

import battlecode.common.*;

public class Headquarter
{

    private static int nextSpawn = 0;
    private static int nextResearch = 0;

    private static boolean trySpawn(
            RobotController rc, SenseCache sense, int ord, boolean ignoreMines)
        throws GameActionException
    {
        Direction dir = Utils.dirByOrd[ord & 7];
        if (!rc.canMove(dir)) return false;

        if (ignoreMines || sense.nonAlliedMine(rc.getLocation().add(dir)))
            return false;

        rc.spawn(dir);
        return true;
    }


    private static boolean spawn(
            RobotController rc, SenseCache sense, boolean ignoreMines)
        throws GameActionException
    {
        if (rc.getTeamPower() < 2.0) return false;

        MapLocation myLoc = rc.getLocation();
        Direction dir = myLoc.directionTo(rc.senseEnemyHQLocation());
        int dirOrd = dir.ordinal();
        boolean spawned = false;

        for (int i = 0; i < 5; ++i) {
            spawned = trySpawn(rc, sense, dirOrd + i, ignoreMines);
            spawned = spawned || trySpawn(rc, sense, dirOrd - i, ignoreMines);

            if (spawned) break;
        }

        if (spawned) nextSpawn = -1;
        return spawned;
    }

    private static boolean research(RobotController rc, Upgrade up)
        throws GameActionException
    {
        if (rc.hasUpgrade(up)) return false;

        rc.researchUpgrade(up);
        return true;
    }

    private static boolean research(RobotController rc, boolean allowNuke)
        throws GameActionException
    {
        // This one is just for you Max <3
        return
            research(rc, Upgrade.DEFUSION) ||
            // research(rc, Upgrade.VISION) ||
            (allowNuke && research(rc, Upgrade.NUKE));
    }


    public static void run(RobotController rc)
        throws GameActionException
    {
        SenseCache sense = new SenseCache(rc);

        while (true) {
            if (rc.senseEnemyNukeHalfDone())
                Communication.broadcast(0, 1);

            if (!rc.isActive()) { rc.yield(); continue; }

            Navigation nav = new Navigation(rc, sense);
            Defuse defuse = new Defuse(rc, nav, sense);
            sense.reset();

            int round = Clock.getRoundNum();

            if (nextSpawn < 0) nextSpawn = round + 5;

            if (nextSpawn <= round || !research(rc, false)) {
                if (!spawn(rc, sense, false) && !spawn(rc, sense, true)) {
                    research(rc, true);
                }
            }

            Communication.spam();

            rc.yield();
        }
    }



}