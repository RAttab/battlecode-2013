package fieldbot;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{


    private static void encampment(RobotController rc) throws GameActionException
    {
	while (true) {

	    rc.yield();
	}
    }


    private static void hq(RobotController rc) throws GameActionException
    {
	int nextSpawn = 0;
	MapLocation coord = rc.getLocation();

	while (true) {

	    if (nextSpawn <= Clock.getRoundNum()) {
		Direction dir = coord.directionTo(rc.senseEnemyHQLocation());

		if (rc.canMove(dir))
		    rc.spawn(dir);

		nextSpawn = Clock.getRoundNum() + GameConstants.HQ_SPAWN_DELAY;
	    }

	    rc.yield();
	}
    }


    public static void run(RobotController rc)
    {
	while (true) {
	    try {
		RobotType type = rc.getType();

		if (type == RobotType.SOLDIER) Soldier.run(rc);
		else if (type == RobotType.HQ) hq(rc);
		else encampment(rc);
	    }
	    catch(Exception e) { e.printStackTrace(); }
	    rc.yield();
	}
    }

}