package fieldbot;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{

    private static void debug_dumpStrength(RobotController rc, double[] strength)
    {
	String str = rc.getRobot().getID() + " { ";
	for (int i = 0; i < 8; ++i)
	    str += "(" + Direction.values()[i].name() + "=" + strength[i] + "), ";
	str += " }";
	System.out.println(str);
    }

    private static void strengthen(double[] strength,
				   Direction dir,
				   double weight,
				   double dist)
    {
	if (dir == Direction.OMNI) return;

	double force = weight * (1 / dist);

	int ord = dir.ordinal();
	final int mask = 8 - 1;

	strength[ord] += force;
	strength[(ord - 1) & mask] += force / 2;
	strength[(ord + 1) & mask] += force / 2;
    }

    private static void soldier(RobotController rc) throws GameActionException
    {
	final double HQ_WEIGHT = 1.0;
	final double NEUTRAL_MINE_WEIGHT = -0.3;

	final int MAX_MINES = 10;

	final Direction ordinals[] = Direction.values();
	final Team team = rc.getTeam();
	final Robot robot = rc.getRobot();

	double strength[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

	rc.wearHat();

	while (true) {
	    if (!rc.isActive()) {
		rc.yield();
		continue;
	    }

	    MapLocation coord = rc.getLocation();

	    for (int i = 0; i < 8; ++i) strength[i] = 0.0;


	    // Enemy HQ
	    {
		MapLocation hq = rc.senseEnemyHQLocation();
		Direction dir = coord.directionTo(hq);
		strengthen(strength, dir, HQ_WEIGHT, hq.distanceSquaredTo(coord));
	    }


	    // Mines
	    {
		MapLocation mines[] =
		    rc.senseMineLocations(coord,
					  10000,
					  Team.NEUTRAL);

		int steps = mines.length / MAX_MINES;
		for (int i = 0; i < mines.length; i += steps) {
		    Direction dir = mines[i].directionTo(coord);
		    strengthen(strength, dir,
			       NEUTRAL_MINE_WEIGHT,
			       mines[i].distanceSquaredTo(coord));
		}
	    }


	    // Enemies
	    {

	    }


	    // Allies
	    {

	    }



	    // Compute the final direction.

	    double maxStrength = strength[0];
	    Direction finalDir = ordinals[0];
	    for (int i = 1; i < 8; ++i) {
		if (maxStrength > strength[i]) continue;
		if (!rc.canMove(ordinals[i])) continue;

		maxStrength = strength[i];
		finalDir = ordinals[i];
	    }

	    debug_dumpStrength(rc, strength);


	    // Execute the move safely.
	    {
		MapLocation target = coord.add(finalDir);
		Team mine = rc.senseMine(target);

		if (mine == null || mine == team)
		    rc.move(finalDir);
		else rc.defuseMine(target);
	    }

	    rc.yield();
	}
    }


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

		if (type == RobotType.SOLDIER) soldier(rc);
		else if (type == RobotType.HQ) hq(rc);
		else encampment(rc);
	    }
	    catch(Exception e) { e.printStackTrace(); }
	    rc.yield();
	}
    }

}