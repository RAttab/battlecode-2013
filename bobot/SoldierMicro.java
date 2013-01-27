package bobot;

import battlecode.common.*;


public class SoldierMicro
{
    RobotController rc;
    Navigation nav;
    SenseCache sense;
    Defuse defuse;

    SoldierMicro(
            RobotController rc, Navigation nav, SenseCache sense, Defuse defuse)
    {
        this.rc = rc;
        this.nav = nav;
        this.sense = sense;
        this.defuse = defuse;
    }

    static boolean isMicro(SenseCache sense)
        throws GameActionException
    {
        return sense.nearbyEnemies().length > 0;
    }

    void fight() throws GameActionException
    {
        if (combat()) {
            nav.autoDefuse = false;
            return;
        }

        frontline();
        defuse.micro();
    }

    private boolean combat() throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();
        Team me = rc.getTeam();

        if (sense.adjacentRobots(myLoc, me.opponent()).length == 0)
            return false;

        // Calculate numeric advantage of every movable positions.
        // Note that buildings are included because they reduce your dps
        // Reserved BC: (3*100 + 2*25 + 25) < (500 * 9) < 4500
        for (int i = 9; --i >= 0;) {
            Direction dir = i == 8 ? null : Utils.dirByOrd[i];
            if (i < 8 && !rc.canMove(dir)) continue;

            MapLocation loc = i == 8 ? myLoc : myLoc.add(dir);
            if (sense.nonAlliedMine(loc)) continue;

            int enemies = sense.adjacentRobots(loc, me.opponent()).length;
            if (enemies == 0) {
                // Penalty for breaking an engagement
                // System.out.println("break: " + dir);
                nav.boost(dir, Weights.MICRO_COMBAT_BREAK, false);
                continue;
            }

            // are we moving into the range of other robots?
            // The enemy sub is to remove the adjacent robots.
            int strike = sense.strikeRobots(loc, me.opponent()).length - enemies;

            // Staying near allies ups our chance of survival by allowing us to
            // kill baddies faster.
            int allies = sense.adjacentRobots(loc, me).length;

            double diff =
                allies - enemies - (strike * Weights.MICRO_COMBAT_STRIKE)
                + Weights.MICRO_COMBAT_OFFSET;

            diff *= Weights.MICRO_COMBAT_MUL;

            // System.out.println("combat: " + dir + "={"
            //         + enemies + ", " + strike + ", " + allies + "} -> " + diff);
            nav.boost(dir, diff, false);
        }

        return true;
    }


    // \todo could incorporate SenseCache.busy for enemy checks to get some
    // free damage.
    private void frontline() throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();
        RobotInfo[] enemies = sense.nearbyEnemies();

        for (int i = enemies.length; --i >= 0;) {
            if (!sense.battleBot(enemies[i])) continue;

            MapLocation loc = enemies[i].location;
            Direction charge = myLoc.directionTo(loc);
            Direction retreat = charge.opposite();
            double dist = loc.distanceSquaredTo(myLoc);

            // We get first hit
            // \todo Could allow retreat if we have mines or if we have allies.
            if (dist < 9) {
                // System.out.println("first-to-hit: charge=" + charge);
                nav.boost(charge,   Weights.MICRO_FL_FIRST_STRIKE, true);
                nav.boost(retreat, -Weights.MICRO_FL_FIRST_STRIKE, true);
            }

            // They get first hit
            // \todo retreat-stand choice should be based on number of allies.
            else if (dist < 16 || dist == 18) {
                // System.out.println("second-to-hit: charge=" + charge);
                nav.boost(Weights.MICRO_FL_SECOND_STRIKE);
                nav.boost(retreat, Weights.MICRO_FL_RETREAT, true);
                nav.boost(charge, -Weights.MICRO_FL_SECOND_STRIKE, true);
            }

            // no risk, close in. Is only invoked when vision is researched.
            // \todo charge-stand choice should be based on number of allies.
            else {
                // System.out.println("close-in: charge=" + charge);
                nav.boost(charge, Weights.MICRO_FL_CLOSE_IN, true);
            }
        }

        // Try to stay near or provide cover for our allies.
        Robot[] allies = sense.adjacentRobots(myLoc, rc.getTeam());

        for (int i = allies.length; --i >= 0;) {
            RobotInfo info = rc.senseRobotInfo(allies[i]);
            if (!sense.battleBot(info)) continue;

            Direction dir = myLoc.directionTo(info.location);
            nav.boost(dir, Weights.MICRO_FL_ALLIES, true);
        }

    }

}
