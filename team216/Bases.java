package team216;

import battlecode.common.*;

// TODO: add an ability to self-destruct if another base type is needed badly
public class Bases
{

    static final int MAX_ROBOTS = 30;
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


    static final double POWER_THRESHOLD = 100.0;
    static final double BC_THRESHOLD = 5000.0;

    static final int MAGIC_NUM = 0xA5A5A5A5;

    static int lastSeen[] = new int[20];
    static int lastSeenIndex = 0;

    static int probes = 0, hits = 0, rounds = 0;


    private static boolean tryBroadcast(RobotController rc, int channel)
        throws GameActionException
    {
        int data = rc.readBroadcast(channel);
        if (data == 0 || data == MAGIC_NUM) return false;

        rc.broadcast(channel, MAGIC_NUM);
        return true;
    }

    private static void broadcast(RobotController rc) throws GameActionException
    {
        int id = rc.getRobot().getID();
        rounds++;

        double power = rc.getTeamPower();
        while (power >= POWER_THRESHOLD &&
                Clock.getBytecodesLeft() >= BC_THRESHOLD)
        {
            lastSeenIndex = (lastSeenIndex + 1) % lastSeen.length;

            int channel = lastSeen[lastSeenIndex];
            if (channel < 0) {
                channel = (int)(
                        Math.random() *
                        ((double) GameConstants.BROADCAST_MAX_CHANNELS));

                // Avoids having every tower probe the same slots.
                channel = (channel + id) % GameConstants.BROADCAST_MAX_CHANNELS;
            }

            if (tryBroadcast(rc, channel)) {
                power -= GameConstants.BROADCAST_SEND_COST;
                lastSeen[lastSeenIndex] = channel;
                hits++;
            }
            else lastSeen[lastSeenIndex] = -1;

            power -= GameConstants.BROADCAST_READ_COST;
            probes++;
        };

        rc.setIndicatorString(2,
                "probes=" + probes + ", hits=" + hits +
                ", probes/rnd=" + (probes / rounds));
    }


    public static void run(RobotController rc) throws GameActionException
    {
        MapLocation coord = rc.getLocation();
        boolean isArty = rc.getType() == RobotType.ARTILLERY;

        for (int i = 0; i < lastSeen.length; ++i) lastSeen[i] = -1;

        while (true) {

            Math.random();

            if (!rc.isActive()) { rc.yield(); continue; }

            if (isArty) artillery(rc);
            else broadcast(rc);

            rc.yield();
        }
    }
}