package team216;

import battlecode.common.*;

/**
 *
 */
public class Utils
{

    public static final Direction dirByOrd[] = Direction.values();
    public static final int dirOrdMask = (8 - 1);

    public static int ceilDiv(int a, int b)
    {
        if (b == 0) return 1;
        return (a - 1) / b + 1;
    }

    public static double distToLineBetween
    (MapLocation p, MapLocation p1, MapLocation p2){
    	double v_x = p2.x - p1.x;
    	double v_y = p2.y - p1.y;
    	double w_x = p.x - p1.x;
    	double w_y = p.y - p1.y;

    	double dot1 = v_x * w_x + v_y * w_y;
    	if (dot1 <= 0)
    		return distTwoPoints(p, p1);

    	double dot2 = v_x * v_x + v_y * v_y;
    	if (dot2 <= dot1)
    		return distTwoPoints(p, p2);

    	double b = dot1 / dot2;
    	double b_x = b*v_x + p.x;
    	double b_y = b*v_y + p.y;
    	return distTwoPoints((double)p.x, (double)p.y, b_x, b_y);
    }
    public static double distTwoPoints(MapLocation p, MapLocation q){
    	return Math.sqrt((p.x - q.x)*(p.x-q.x) + (p.y-q.y)*(p.y-q.y));
    }
    public static double distTwoPoints
    (double p_x, double p_y, double q_x, double q_y){
    	return Math.sqrt((p_x - q_x)*(p_x-q_x) + (p_y-q_y)*(p_y-q_y));
    }

    public static double strategicRelevance
    (MapLocation p, MapLocation hq, MapLocation evilHq, double hqDist) {
    	double factor = hqDist / 2;
    	return (factor - distToLineBetween(p, hq, evilHq)) / factor;
    }
    public static double defensiveRelevance
    (MapLocation p, MapLocation hq, MapLocation evilHq, double hqDist) {
    	double factor = hqDist / 4;
    	return 
    		( 0.8 * (factor - distTwoPoints(p, hq)) + 
    		0.2 * (factor - distToLineBetween(p, hq, evilHq)) )
    		/ factor;
    }
}
