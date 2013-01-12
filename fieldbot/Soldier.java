package fieldbot;

import battlecode.common.*;

public class Soldier
{
    private static enum Weight {
	ENEMY_HQ(20.0), ALLY_HQ(-1.0),

	GL_ENEMY_SD(2.0), LC_ENEMY_SD(1.0),
	GL_ALLY_SD(-.01), LC_ALLY_SD(0.9),

	NEUTRAL_MINE(-.3), ENEMY_MINE(-.3);

	public final double value;
	Weight(double value) { this.value = value; }
    }

    private static enum Const {
	GL_RADIUS(10000), LC_RADIUS(100),
	MAX_MINES(10), MAX_ENEMIES(10), MAX_ALLIES(10);

	public final int value;
	Const(int value) { this.value = value; }
    }


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
				   double force)
    {

	int ord = dir.ordinal();
	final int mask = 8 - 1;

	strength[ord] += force;
	strength[(ord - 1) & mask] += force / 2;
	strength[(ord + 1) & mask] += force / 2;
    }

    private static void strengthen(double[] strength,
				   Direction dir,
				   Weight weight,
				   double dist)
    {
	if (dir == Direction.OMNI) return;

	// System.out.println("(" + dir.name() + ", " + weight.name() + ")=" + dist);

	strengthen(strength, dir, weight.value * (1 / dist));
    }


    public static void run(RobotController rc) throws GameActionException
    {
	final Direction ordinals[] = Direction.values();
	final Team team = rc.getTeam();
	final Robot robot = rc.getRobot();

	rc.wearHat();

	while (true) {

	    if (!rc.isActive()) {
		rc.yield();
		continue;
	    }

	    MapLocation coord = rc.getLocation();
	    double strength[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };


	    // Enemy HQ
	    {
		MapLocation hq = rc.senseEnemyHQLocation();
		Direction dir = coord.directionTo(hq);
		strengthen(strength, dir, Weight.ENEMY_HQ, hq.distanceSquaredTo(coord));
	    }


	    // Mines
	    {
		MapLocation mines[] =
		    rc.senseMineLocations(coord, 10000, Team.NEUTRAL);

		int steps = Math.max(mines.length / Const.MAX_MINES.value, 1);
		for (int i = 0; i < mines.length; i += steps) {
		    Direction dir = coord.directionTo(mines[i]);

		    strengthen(strength, dir, Weight.NEUTRAL_MINE,
			       coord.distanceSquaredTo(mines[i]));
		}
	    }


	    // Enemies
	    {
		Robot enemies[] =
		    rc.senseNearbyGameObjects(Robot.class, 10000, team.opponent());

		int steps = Math.max(enemies.length / Const.MAX_ENEMIES.value, 1);
		for (int i = 0; i < enemies.length; i += steps) {
		    MapLocation enemyCoord = rc.senseRobotInfo(enemies[i]).location;
		    Direction dir = coord.directionTo(enemyCoord);

		    strengthen(strength, dir, Weight.GL_ENEMY_SD,
			       coord.distanceSquaredTo(enemyCoord));

		}
	    }


	    // Allies
	    {
		Robot allies[] =
		    rc.senseNearbyGameObjects(Robot.class, 10000, team);

		int steps = Math.max(allies.length / Const.MAX_ALLIES.value, 1);
		for (int i = 0; i < allies.length; i += steps) {
		    MapLocation alliesCoord = rc.senseRobotInfo(allies[i]).location;
		    Direction dir = coord.directionTo(alliesCoord);

		    strengthen(strength, dir, Weight.GL_ALLY_SD,
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
}