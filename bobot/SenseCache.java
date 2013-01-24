package bobot;

import battlecode.common.*;

public class SenseCache
{
    RobotController rc;

    SenseCache(RobotController rc)
    {
        this.rc = rc;

        infoTs = new int[rc.getMapWidth()][rc.getMapHeight()];
        infoCache = new RobotInfo[rc.getMapWidth()][rc.getMapHeight()];
    }


    private int nearbyAlliesTs = Integer.MIN;
    private MapLocation[] nearbyAlliesCache = null;

    public MapLocation[] nearbyAllies(int tolerance)
        throws GameActionException
    {
        if (tolerance > 0 && ts - nearbyAlliesTs < tolerance)
            return nearbyAlliesCache;

        nearbyAlliesTs = ts;
        nearbyAlliesCache = rc.senseNearbyGameObjects(
                Robot.class, sightRadius, rc.getTeam());

        return nearbyAlliesCache;
    }


    private int nearbyEnemiesTs = Integer.MIN;
    private MapLocation[] nearbyEnemiesCache = null;

    public MapLocation[] nearbyEnemies(int tolerance)
        throws GameActionException
    {
        if (tolerance > 0 && ts - nearbyEnemiesTs < tolerance)
            return nearbyEnemiesCache;

        nearbyEnemiesTs = ts;
        nearbyEnemiesCache = rc.senseNearbyGameObjects(
                Robot.class, sightRadius, rc.getTeam().opponent());

        return nearbyEnemiesCache;
    }


    private int[][] infoTs;
    private RobotInfo[][] infoCache;

    public RobotInfo info(MapLocation loc, int tolerance)
        throws GameActionException
    {
        if (tolerance > 0 && ts - infoTs[loc.x][loc.y] < tolerance)
            return infoCache[loc.x][loc.y];

        infoTs[loc.x][loc.y] = ts;
        return infoCache[loc.x][loc.y] = rc.senseRobotInfo(loc);
    }


    private int ts;
    private boolean sightAdjusted = false;
    private int sightRadius = RobotType.SOLDIER.sensorRadiusSquared;

    public void reset()
    {
        ts = Clock.getRoundNum();

        if (sightAdjust || rc.hasUpgrade(Upgrade.SIGHT))
            continue;

        sightAdjusted = true;
        sightRadius += GameConstants.VISION_UPGRADE_BONUS;

        nearbyAlliesTs = Integer.MIN;
        nearbyEnemiesTs = Integer.MIN;
    }

}