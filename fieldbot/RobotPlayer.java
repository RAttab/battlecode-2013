package fieldbot;

import battlecode.common.*;

/**
 *
 */
public class RobotPlayer
{

    private static void debug_dumpStrength(RobotController rc, double[] strength)
    {
	String str = "{ ";
	for (int i = 0; i < 8; ++i) {
	    str += "(" + Direction.values()[i].name() +
		"=" + ((int)(strength[i] * 10000)) + ") ";
	}
	str += "}";
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
	final double HQ_WEIGHT = 20.0;
	final double ENEMIES_WEIGHT = 2.0;
	final double ALLIES_WEIGHT = -0.1;
	final double NEUTRAL_MINE_WEIGHT = -0.3;

	final int MAX_MINES = 10;
	final int MAX_ENEMIES = 10;
	final int MAX_ALLIES = 10;

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
		    rc.senseMineLocations(coord, 10000, Team.NEUTRAL);

		int steps = Math.max(mines.length / MAX_MINES, 1);
		for (int i = 0; i < mines.length; i += steps) {
		    Direction dir = coord.directionTo(mines[i]);

		    strengthen(strength, dir, NEUTRAL_MINE_WEIGHT,
			       coord.distanceSquaredTo(mines[i]));
		}
	    }


	    // Enemies
	    {
		Robot enemies[] =
		    rc.senseNearbyGameObjects(Robot.class, 10000, team.opponent());

		int steps = Math.max(enemies.length / MAX_ENEMIES, 1);
		for (int i = 0; i < enemies.length; i += steps) {
		    MapLocation enemyCoord = rc.senseRobotInfo(enemies[i]).location;
		    Direction dir = coord.directionTo(enemyCoord);

		    strengthen(strength, dir, ENEMIES_WEIGHT,
			       coord.distanceSquaredTo(enemyCoord));

		}
	    }


	    // Allies
	    {
		Robot allies[] =
		    rc.senseNearbyGameObjects(Robot.class, 10000, team);

		int steps = Math.max(allies.length / MAX_ALLIES, 1);
		for (int i = 0; i < allies.length; i += steps) {
		    MapLocation alliesCoord = rc.senseRobotInfo(allies[i]).location;
		    Direction dir = coord.directionTo(alliesCoord);

		    strengthen(strength, dir, ALLIES_WEIGHT,
			       coord.distanceSquaredTo(alliesCoord));

		}
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

		if (mine == null || mine == team) {
		    if (rc.canMove(finalDir))
			rc.move(finalDir);
		}
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