package testplayer;

// going to use Marc's rusher to test out battle formation tactics

public class RobotPlayer {

	private static boolean TryMove(RobotController rc, MapLocation targetLoc, Direction dir) {
		rc.yield();
		try {
			if (rc.senseMine(targetLoc) == null) {
				rc.move(dir);
				return true;
			} else {
				rc.defuseMine(targetLoc);
				rc.move(dir);
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static void LogicHQ(RobotController rc) {
		try {
			if (rc.isActive()) {
				//Spawn a solder
				Direction optimalDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());

				while(true) {
				if (!rc.hasUpgrade(Upgrade.VISION)) {
	                rc.researchUpgrade(Upgrade.VISION);
	                break;
	            }
				if (rc.canMove(optimalDir)) {
					rc.spawn(optimalDir);
					break;
				}
					optimalDir = optimalDir.rotateLeft();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void LogicSoldier(RobotController rc) {
		try {
			if (rc.isActive()) {

				//Look for nearby enemies
				GameObject[] nearbyEnemyRobots = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 2, Team.B);

				if (nearbyEnemyRobots.length > 0) {
					//Go meet them
					Direction dir = rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemyRobots[0]));
					MapLocation targetLoc = rc.getLocation().add(dir);
					RobotPlayer.TryMove(rc, targetLoc, dir);
				} else {
					//Go to the HQ
					Direction optimalDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
					MapLocation targetLoc = rc.getLocation().add(optimalDir);
					while (!RobotPlayer.TryMove(rc, targetLoc, optimalDir)) {
						optimalDir = optimalDir.rotateRight();
						targetLoc = rc.getLocation().add(optimalDir);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void run(RobotController rc) {

		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					RobotPlayer.LogicHQ(rc);
				} else if (rc.getType() == RobotType.SOLDIER) {
					RobotPlayer.LogicSoldier(rc);
				}
				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
