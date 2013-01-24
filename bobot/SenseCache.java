package bobot;

import battlecode.common.*;

public class SenseCache
{
    RobotController rc;

    SenseCache(RobotController rc)
    {
        this.rc = rc;
    }



    private RobotInfo[] nearbyEnemiesCache = null;

    public RobotInfo[] nearbyEnemies()
        throws GameActionException
    {
        Robot[] enemies = rc.senseNearbyGameObjects(
                Robot.class, sightRadius, rc.getTeam());

        nearbyEnemiesCache = new RobotInfo[enemies.length];
        for (int i = enemies.length; i-- >= 0;)
            nearbyEnemiesCache[i] = rc.senseRobotInfo(enemies[i]);

        return nearbyEnemiesCache;
    }


    private int nearbyAlliesTs = 0;
    private RobotInfo[] nearbyAlliesCache = null;

    public RobotInfo[] nearbyAllies(int tolerance)
        throws GameActionException
    {
        if (tolerance > 0 && ts - nearbyAlliesTs < tolerance)
            return nearbyAlliesCache;

        nearbyAlliesTs = ts;

        Robot[] allies = rc.senseNearbyGameObjects(
                Robot.class, sightRadius, rc.getTeam());

        nearbyAlliesCache = new RobotInfo[allies.length];
        for (int i = allies.length; i-- >= 0;)
            nearbyAlliesCache[i] = rc.senseRobotInfo(allies[i]);

        return nearbyAlliesCache;
    }

    private int ts;
    private boolean sightAdjusted = false;
    private int sightRadius = RobotType.SOLDIER.sensorRadiusSquared;

    public void reset()
    {
        ts = Clock.getRoundNum();

        if (!sightAdjusted && rc.hasUpgrade(Upgrade.VISION)) {
            sightAdjusted = true;
            sightRadius += GameConstants.VISION_UPGRADE_BONUS;

            nearbyAlliesTs = 0;
        }
    }

}