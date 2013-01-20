package nuclear;

import battlecode.common.*;

public class RobotPlayer {

	private static void NukeHQ(RobotController rc) {
    MapLocation coord = rc.getLocation();
		try {
			if (rc.isActive()) {
				if (Clock.getRoundNum() < 50)
					spawn(rc, coord);
				else
					rc.researchUpgrade(Upgrade.NUKE);
				rc.yield();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean trySpawn(RobotController rc, int ord)
        throws GameActionException
    {
        // TODO: check for mines
        Direction dir = Utils.dirByOrd[ord & Utils.dirOrdMask];
        if (!rc.canMove(dir)) return false;

        rc.spawn(dir);
        return true;
    }


    /**
     *
     */
    private static void spawn(RobotController rc, MapLocation coord)
        throws GameActionException
    {
    	boolean spawned = false;
        Direction dir = coord.directionTo(Storage.ENEMY_HQ);
        int dirOrd = dir.ordinal();
        for (int i = 0; i < 5; ++i) {
            spawned = trySpawn(rc, dirOrd + i);
            spawned = spawned || trySpawn(rc, dirOrd - i);

            if (spawned) break;
        }
    }

	public static void run(RobotController rc) {
		Storage.calculateValues(rc);
        while (true) {
            try {
                if (rc.getType() == RobotType.SOLDIER)
                    Soldier.run(rc);
                else if (rc.getType() == RobotType.HQ)
                    NukeHQ(rc);
                else
                    Bases.run(rc);
                rc.yield();
            }
            catch(Exception e) { e.printStackTrace(); }
        }
	}
}
