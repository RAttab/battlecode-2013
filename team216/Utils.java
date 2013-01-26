package team216;

import battlecode.common.*;

public class Utils
{

    public static final Direction dirByOrd[] = Direction.values();
    public static final int dirOrdMask = (8 - 1);

    // These are just arbitrary values, feel free to change
    private static final int OFFSET_CHANNEL_1 = 235;
    private static final int OFFSET_CHANNEL_2 = 61234;
    private static final int OFFSET_BACKUP_CHANNEL_1 = 9234;
    private static final int OFFSET_BACKUP_CHANNEL_2 = 40129;

    // We use this arbitrary seed to add entropy to messaging data
    private static final int SECRET_COMMUNICATION_SEED = 14212;

    public static int ceilDiv(int a, int b)
    {
        if (b == 0) return 1;
        return (a - 1) / b + 1;
    }

    public static double distToLineBetween(
    MapLocation p, MapLocation v, MapLocation w){
    	double hqDists = (v.x - w.x)*(v.x - w.x) + (v.y - w.y)*(v.y - w.y);
		double t = ((p.x - v.x)*(w.x - v.x) + (p.y - v.y)*(w.y - v.y)) / hqDists;

		if (t < 0)
			return distTwoPoints(p, v);
		if (t > 1)
			return distTwoPoints(p, w);

		double d = 
			distTwoPoints((double)p.x, (double)p.y, v.x+t*(w.x-v.x), v.y+t*(w.y-v.y) );
		return Math.sqrt(d);
    }
    public static double distTwoPoints(MapLocation p, MapLocation q){
    	return Math.sqrt((p.x - q.x)*(p.x-q.x) + (p.y-q.y)*(p.y-q.y));
    }
    public static double distTwoPoints(
    double p_x, double p_y, double q_x, double q_y){
    	return Math.sqrt((p_x - q_x)*(p_x-q_x) + (p_y-q_y)*(p_y-q_y));
    }

    public static double strategicRelevance(MapLocation p) {
        double factor = Storage.distanceBetweenHQs() / Weights.STRAT_RATIO;
    	return (factor - distToLineBetween(p, Storage.MY_HQ, Storage.ENEMY_HQ)) / factor;
    }
    public static double defensiveRelevance(MapLocation p) {
    	double factor = Storage.distanceBetweenHQs() / Weights.DEF_RATIO;
    	return 
    		( 0.8 * (factor - distTwoPoints(p, Storage.MY_HQ)) + 
    		0.2 * (factor - distToLineBetween(p, Storage.MY_HQ, Storage.ENEMY_HQ)) )
    		/ factor;
    }

    public static void broadcastMessage(RobotController rc, int channel, int data) {
        // We add a constant value (channel number times a seed) to every message and
        // broadcast the data on four distinct channels (two main and two for backup)
        // Note that the addition might cause integer overflow, but this is fine since
        // Java ints wrap around

        try {
            // We send the data on a channel at an arbitrary offset of the passed channel
            // This could potentially cause a collision with other messages, but we have a
            // fallback on backup channels
            rc.broadcast((channel + OFFSET_CHANNEL_1) % GameConstants.BROADCAST_MAX_CHANNELS, data + OFFSET_CHANNEL_1 * SECRET_COMMUNICATION_SEED);
            rc.broadcast((channel + OFFSET_CHANNEL_2) % GameConstants.BROADCAST_MAX_CHANNELS, data + OFFSET_CHANNEL_2 * SECRET_COMMUNICATION_SEED);

            //// Backup broadcast, if we detect that the main channels are getting fudged
            rc.broadcast((channel + OFFSET_BACKUP_CHANNEL_1) % GameConstants.BROADCAST_MAX_CHANNELS, data + OFFSET_BACKUP_CHANNEL_1 * SECRET_COMMUNICATION_SEED);
            rc.broadcast((channel + OFFSET_BACKUP_CHANNEL_2) % GameConstants.BROADCAST_MAX_CHANNELS, data + OFFSET_BACKUP_CHANNEL_2 * SECRET_COMMUNICATION_SEED);
        } catch(GameActionException e) { e.printStackTrace(); }
    }

    public static int readMessage(RobotController rc, int channel) {
        try {
            int data = rc.readBroadcast((channel + OFFSET_CHANNEL_1) % GameConstants.BROADCAST_MAX_CHANNELS) - OFFSET_CHANNEL_1 * SECRET_COMMUNICATION_SEED;

            if (data == rc.readBroadcast((channel + OFFSET_CHANNEL_2) % GameConstants.BROADCAST_MAX_CHANNELS) - OFFSET_CHANNEL_2 * SECRET_COMMUNICATION_SEED)
                return data;

            // somebody's been fucking with our messages, try the backup channel
            data = rc.readBroadcast((channel + OFFSET_BACKUP_CHANNEL_1) % GameConstants.BROADCAST_MAX_CHANNELS) - OFFSET_BACKUP_CHANNEL_1 * SECRET_COMMUNICATION_SEED;

            if (data == rc.readBroadcast((channel + OFFSET_BACKUP_CHANNEL_2) % GameConstants.BROADCAST_MAX_CHANNELS) - OFFSET_BACKUP_CHANNEL_2 * SECRET_COMMUNICATION_SEED)
                return data;

        } catch(GameActionException e) { e.printStackTrace(); }

        // both the main data and the backup data are corrupted, give up
        return 0;
    }
}
