package testplayer;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{
	public static Storage know;

    public static void run(RobotController rc)
    {
        preprocessing(rc);
        while (true) {
            try {
	            if (!rc.isActive())
	            	{ rc.yield(); continue; }

                if (know.MY_TYPE == RobotType.SOLDIER) {
                }
                else if (know.MY_TYPE == RobotType.HQ) {
                	hq(rc);
                }
                else{}
            }
            catch(Exception e) { e.printStackTrace(); }
            rc.yield();
        }
    }
    public static void soldier(RobotController rc){

    }
    public static void hq(RobotController rc){
        while (true) {
            rc.setIndicatorString(0, ""+Clock.getBytecodeNum());
            rc.setIndicatorString(1, know.DISTANCE_BETWEEN + ", " + know.EST_RUSH_TIME);
            rc.yield();
        }
    }
    public static void preprocessing(RobotController rc){
        know = new Storage(rc);
    }

}