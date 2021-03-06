package team216;

import battlecode.common.*;

public class Communication
{
    static RobotController rc;


    static final int MSK = 0xF0000000;
    static final int IND = 0x50000000;

    static final int TTL = 0x0FFF0000;

    static final int[] OFF = { 0x2E3D, 0xD3E2, 0x8B5C };

    public static void broadcast(int channel, int data)
        throws GameActionException
    {

        if (rc.getTeamPower() < 1) return;

        int round = Clock.getRoundNum();

        for (int i = OFF.length; --i >= 0;) {
            channel = (OFF[i] * round + channel)
                & GameConstants.BROADCAST_MAX_CHANNELS;

            rc.broadcast(channel, data | round << 16 | IND);
        }
    }

    public static int readBroadcast(int channel, boolean lastTurn)
        throws GameActionException
    {
        if (rc.getTeamPower() < 1) return -1;

        int round = lastTurn ? Clock.getRoundNum() - 1 : Clock.getRoundNum();

        for (int i = OFF.length; --i >= 0;) {
            channel =
                (OFF[i] * round + channel)
                & GameConstants.BROADCAST_MAX_CHANNELS;

            int val = rc.readBroadcast(channel);
            if ((val & MSK) != IND) continue;
            if ((val & TTL) >> 16 != round) continue;
            return val & ~(MSK | TTL);
        }

        return -1;
    }



    private static void trySpam(int channel)
        throws GameActionException
    {
        channel &= GameConstants.BROADCAST_MAX_CHANNELS;

        int data = rc.readBroadcast(channel);
        if (data == 0 || (data & MSK) == IND) return;

        rc.broadcast(channel, 0);
        lastSeen[lastSeenIndex++ % lastSeen.length] = channel;
    }


    static int lastSeen[] = new int[20];
    static int lastSeenIndex;

    private static void spamLastSeen()
        throws GameActionException
    {
        for (int i = lastSeen.length; --i >= 0;) {
            if (lastSeen[i] < 0) continue;
            trySpam(lastSeen[i]);
        }
    }


    private static int spamIndex = 0;
    private static int spamPattern;

    public static void spam() throws GameActionException
    {
        if (rc.getTeamPower() < 10) return;

        spamLastSeen();

        // Leave some room for hats.
        int bcThreshold =
            rc.getType() == RobotType.SOLDIER && Hat.hatless ? 4500 : 7000;

        while(Clock.getBytecodeNum() < bcThreshold) {
            switch(spamPattern) {

                // Linear scans.
            case 0: trySpam(spamIndex++); break;
            case 1: trySpam(--spamIndex); break;

                // Spiral out from the center.
            case 2:
                spamIndex = (spamIndex * -1) + 1;
                trySpam(GameConstants.BROADCAST_MAX_CHANNELS/2 + spamIndex);
                break;

            case 3:
                trySpam((int)(GameConstants.BROADCAST_MAX_CHANNELS
                                * Math.random()));
                break;

            }
        }
    }


    public static void setup(RobotController r)
    {
        rc = r;

        spamPattern = rc.getRobot().getID() % 4;
        for (int i = lastSeen.length; --i >= 0;)
            lastSeen[i] = -1;

    }

}