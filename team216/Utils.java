package team216;

import battlecode.common.*;

public class Utils
{

    public static final Direction dirByOrd[] = Direction.values();
    public static final int dirOrdMask = (8 - 1);

    // These are just arbitrary values, feel free to change
    private static final int CHANNEL_1 = 235;
    private static final int CHANNEL_2 = 61234;
    private static final int BACKUP_CHANNEL_1 = 9234;
    private static final int BACKUP_CHANNEL_2 = 40129;

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

    public static void broadcastMessage(RobotController rc, int data) {
        // We add a constant value (channel number times a seed) to every message and
        // broadcast the data on four distinct channels (two main and two for backup)
        // Note that the addition might cause integer overflow, but this is fine since
        // Java ints wrap around

        try {
            rc.broadcast(CHANNEL_1, data + CHANNEL_1 * SECRET_COMMUNICATION_SEED);
            rc.broadcast(CHANNEL_2, data + CHANNEL_2 * SECRET_COMMUNICATION_SEED);

            // Backup broadcast, if we detect that the main channels are getting fudged
            rc.broadcast(BACKUP_CHANNEL_1, data + BACKUP_CHANNEL_1 * SECRET_COMMUNICATION_SEED);
            rc.broadcast(BACKUP_CHANNEL_2, data + BACKUP_CHANNEL_2 * SECRET_COMMUNICATION_SEED);
        } catch(GameActionException e) { e.printStackTrace(); }
    }

    public static int readMessage(RobotController rc) {
        try {
            int data = rc.readBroadcast(CHANNEL_1) - CHANNEL_1 * SECRET_COMMUNICATION_SEED;

            if (data == rc.readBroadcast(CHANNEL_2) - CHANNEL_2 * SECRET_COMMUNICATION_SEED)
                return data;

            // somebody's been fucking with our messages, try the backup channel
            data = rc.readBroadcast(BACKUP_CHANNEL_1) - BACKUP_CHANNEL_1 * SECRET_COMMUNICATION_SEED;

            if (data == rc.readBroadcast(BACKUP_CHANNEL_2) - BACKUP_CHANNEL_2 * SECRET_COMMUNICATION_SEED)
                return data;

        } catch(GameActionException e) { e.printStackTrace(); }

        // both the main data and the backup data are corrupted, give up
        return 0;
    }
}
