package team216;

import battlecode.common.*;

/**
 * Official bot of our team.o
 */
public class RobotPlayer
{

    public static void run(RobotController rc)
    {
	while (true) {
	    try {
		rc.resign();
	    }
	    catch(Exception e) { e.printStackTrace(); }
	    rc.yield();
	}
    }

}