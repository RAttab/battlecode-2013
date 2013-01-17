package testplayer;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{
	// Things that stay the same for the whole game

	// Things that should be a on a decay timer or something
	public static MapLocation[] NEUTRAL_MINES;

	// Things that should be sensed locally each turn

	// Things that will probably have to be broadcasted


    public static void run(RobotController rc)
    {
        preprocessing(rc);
        while (true) {
            try {
	            if (!rc.isActive())
	            	{ rc.yield(); continue; }
                RobotType type = rc.getType();

                if (type == RobotType.SOLDIER) {
                	soldier(rc);
                }
                else if (type == RobotType.HQ) {
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
            //rc.setIndicatorString(0, ""+Clock.getBytecodeNum());
            rc.setIndicatorString(1, DISTANCE_BETWEEN + ", " + EST_RUSH_TIME);
            rc.yield();
        }
    }
    public static void preprocessing(RobotController rc){

    }

}