package team216;

import battlecode.common.*;

public class Utils
{

    public static final Direction dirByOrd[] = Direction.values();

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
    public static MapLocation devsLoveTrollMaps(RobotController rc)
            throws GameActionException 
    {
        MapLocation[] mines = rc.senseNonAlliedMineLocations(rc.senseHQLocation(), 1);
        if (mines.length > 0){
            for (int i=mines.length; --i >= 0;) {
                if (mines[i].isAdjacentTo(rc.getLocation()))
                    return mines[i];
            }
        } 
        return null;
    }
}
