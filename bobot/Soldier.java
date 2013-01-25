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

    public static boolean combat(
            RobotController rc, Navigation nav, SenseCache sense)
        throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();
        Team me = rc.getTeam();

        if (sense.adjacentRobots(myLoc, me.opponent()).length == 0)
            return false;

        // Calculate numeric advantage of every movable positions.
        // Note that buildings are included because they reduce your dps
        // Reserved BC: 3*100 + 2*25 + 25 < 500 * 9 < 4500
        for (int i = 9; --i >= 0;) {
            Direction dir = i == 8 ? Direction.OMNI : Utils.dirByOrd[i];
            if (i < 8 && !rc.canMove(dir)) continue;

            MapLocation loc = i == 8 ? myLoc : myLoc.add(dir);
            if (sense.nonAlliedMine(loc)) continue;

            int enemies = sense.adjacentRobots(loc, me.opponent()).length;
            if (enemies == 0) {
                // Penalty for breaking an engagement
                System.out.println("break: " + dir);
                nav.boost(dir, Weights.MICRO_COMBAT_BREAK, false);
                continue;
            }

            // are we moving into the range of other robots?
            // The extra diff - is to remove the adjacent robots.
            int strike = sense.strikeRobots(loc, me.opponent()).length - enemies;

            // Staying near allies ups our chance of survival.
            int allies = sense.adjacentRobots(loc, me).length;

            double diff = allies - enemies
                - (strike * Weights.MICRO_COMBAT_STRIKE)
                + Weights.MICRO_COMBAT_OFFSET;

            diff *= Weights.MICRO_COMBAT_MUL;

            System.out.println("combat: " + dir + "={"
                    + enemies + ", " + strike + ", " + allies + "} -> " + diff);
            nav.boost(dir, diff, false);
        }

        return true;
    }

    public static void frontline(
            RobotController rc, Navigation nav, SenseCache sense)
        throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();
        RobotInfo[] enemies = sense.nearbyEnemies();

        for (int i = enemies.length; --i >= 0;) {
            if (!isBattleBot(enemies[i].type)) continue;

            MapLocation loc = enemies[i].location;
            Direction dir = myLoc.directionTo(loc);
            double dist = loc.distanceSquaredTo(myLoc);

            // We get first hit
            if (dist < 9) {
                // System.out.println(
                //         "first-to-hit: me=" + myLoc
                //     + ", him=" + loc + ", dist=" + dist);
                nav.boost(dir, Weights.MICRO_ENEMIES_FAR, true);
            }

            // They get first hit
            else if (dist < 16 || dist == 18) {
                // System.out.println(
                //         "second-to-hit: me=" + myLoc
                //     + ", him=" + loc + ", dist=" + dist);
                nav.boost(dir, -Weights.MICRO_ENEMIES_FAR, true);
                nav.boost(Direction.OMNI, Weights.MICRO_ENEMIES_FAR, true);
            }

            // no risk, close in.
            else {
                // System.out.println("close-in");
                nav.boost(dir, Weights.MICRO_ENEMIES_FAR, true);
            }
        }
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

            System.out.println("startLoc=" + rc.getLocation());

            Navigation nav = new Navigation(rc, sense);
            sense.reset();

            if (isMicro(rc, sense)) {
                nav.autoDefuse = false;

                boolean isEngaged = combat(rc, nav, sense);
                if (!isEngaged) frontline(rc, nav, sense);
            }
            else {
                nav.autoDefuse = true;
            }


            nav.debug_dump();
            nav.move();
            rc.yield();
        }
    }

}
