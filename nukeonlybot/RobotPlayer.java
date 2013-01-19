package nukeonlybot;

import battlecode.common.Team;
import battlecode.common.Robot;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer {

	private static void LogicHQ(RobotController rc) {
		try {
			if (rc.isActive()) {
				if (!rc.hasUpgrade(Upgrade.NUKE)) {
					rc.researchUpgrade(Upgrade.NUKE);
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
				}
				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
