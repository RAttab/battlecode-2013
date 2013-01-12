package fieldbot;

import battlecode.common.*;

public class Soldier
{
    private static enum Weight {
        ENEMY_HQ(20.0), ALLY_HQ(-1.0),

            GL_ENEMY_SD(2.0), LC_ENEMY_SD(1.0),
            GL_ALLY_SD(-.01), LC_ALLY_SD(0.7),
            LC_MUL(100.0),

            NEUTRAL_MINE(-.3), ENEMY_MINE(-.3);

        public final double value;
        Weight(double value) { this.value = value; }
    }

    private static enum Const {
        GL_RADIUS(100 * 100), LC_RADIUS(20),
            MAX_MINES(10), MAX_ROBOTS(10);

        public final int value;
        Const(int value) { this.value = value; }
    }


    private static void debug_dumpStrength(
            RobotController rc, double[] strength)
    {
        String str = "{ ";
        for (int i = 0; i < 8; ++i) {
            str += "(" + Direction.values()[i].name() +
                "=" + ((int)(strength[i] * 10000)) + ") ";
        }
        str += "}";
        System.out.println(str);
    }

    private static void strengthen(
            double[] strength, Direction dir, double force)
    {
        if (dir == Direction.OMNI) return;

        int ord = dir.ordinal();
        final int mask = 8 - 1;

        strength[ord] += force;
        strength[(ord - 1) & mask] += force / 2;
        strength[(ord + 1) & mask] += force / 2;
    }

    private static void strengthen(
            double[] strength, Direction dir, Weight weight, double dist)
    {
        strengthen(strength, dir, weight.value * (1 / dist));
    }


    /**
     */
    private static void mines(
            RobotController rc, MapLocation coord,
            double strength[], Weight w, Team team)
        throws GameActionException
    {
        MapLocation mines[] = rc.senseMineLocations(
                coord, Const.GL_RADIUS.value, team);

        int steps = Math.max(mines.length / Const.MAX_MINES.value, 1);

        for (int i = 0; i < mines.length; i += steps) {
            Direction dir = coord.directionTo(mines[i]);
            strengthen(strength, dir, w, coord.distanceSquaredTo(mines[i]));
        }
    }


    /**
     */
    private static void globalRobots(
            RobotController rc, MapLocation coord,
            double strength[], Weight w, Team team)
        throws GameActionException
    {
        Robot robots[] = rc.senseNearbyGameObjects(
                Robot.class, Const.GL_RADIUS.value, team);

        int steps = Math.max(1, robots.length / Const.MAX_ROBOTS.value);

        for (int i = 0; i < robots.length; i += steps) {
            MapLocation robotCoord = rc.senseRobotInfo(robots[i]).location;
            Direction dir = coord.directionTo(robotCoord);

            strengthen(strength, dir, w, coord.distanceSquaredTo(robotCoord));
        }
    }


    /**
     */
    private static void localRobots(
            RobotController rc, MapLocation coord, double strength[], Team team)
        throws GameActionException
    {
        Robot enemies[] = rc.senseNearbyGameObjects(
                Robot.class, Const.LC_RADIUS.value, team.opponent());

        if (enemies.length == 0) return;

        int steps = Math.max(1, enemies.length / Const.MAX_ROBOTS.value);
        int n = Math.min(enemies.length, Const.MAX_ROBOTS.value);


        int x = 0, y = 0;
        for (int i = 0; i < enemies.length; i += steps) {
            MapLocation loc = rc.senseRobotInfo(enemies[i]).location;
            x += loc.x;
            y += loc.y;
        }
        MapLocation enemyCenter = new MapLocation(x / n, y / n);


        Robot allies[] = rc.senseNearbyGameObjects(
                Robot.class, Const.LC_RADIUS.value, team);


        double force = (
                allies.length * Weight.LC_ALLY_SD.value -
                enemies.length * Weight.LC_ENEMY_SD.value) *
            Weight.LC_MUL.value;

        Direction chargeDir = coord.directionTo(enemyCenter);
        strengthen(strength, chargeDir, force);

        Direction retreatDir = enemyCenter.directionTo(coord);
        strengthen(strength, retreatDir, -force);
    }


    /**
     */
    public static void run(RobotController rc) throws GameActionException
    {
        final Direction ordinals[] = Direction.values();
        final Team team = rc.getTeam();
        final Robot robot = rc.getRobot();

        rc.wearHat();

        while (true) {

            if (!rc.isActive()) {
                rc.yield();
                continue;
            }

            MapLocation coord = rc.getLocation();
            double strength[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };


            // Enemy HQ
            {
                MapLocation hq = rc.senseEnemyHQLocation();
                Direction dir = coord.directionTo(hq);
                strengthen(
                        strength, dir, Weight.ENEMY_HQ,
                        hq.distanceSquaredTo(coord));
            }


            mines(rc, coord, strength, Weight.NEUTRAL_MINE, Team.NEUTRAL);
            mines(rc, coord, strength, Weight.ENEMY_MINE, team.opponent());

            // globalRobots(rc, coord, strength, Weight.GL_ENEMY_SD, team.opponent());
            // globalRobots(rc, coord, strength, Weight.GL_ALLY_SD, team);

            localRobots(rc, coord, strength, team);


            // Compute the final direction.

            double maxStrength = strength[0];
            Direction finalDir = ordinals[0];

            for (int i = 1; i < 8; ++i) {
                if (maxStrength > strength[i]) continue;
                if (!rc.canMove(ordinals[i])) continue;

                maxStrength = strength[i];
                finalDir = ordinals[i];
            }

            // debug_dumpStrength(rc, strength);


            // Execute the move safely.
            {
                MapLocation target = coord.add(finalDir);
                Team mine = rc.senseMine(target);

                if (mine == null || mine == team) {
                    if (rc.canMove(finalDir))
                        rc.move(finalDir);
                }
                else rc.defuseMine(target);
            }

            rc.yield();
        }
    }
}