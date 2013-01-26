package bobot;

import battlecode.common.*;

public class SenseCache
{
    RobotController rc;

    SenseCache(RobotController rc)
    {
        this.rc = rc;
    }

    boolean nonAlliedMine(MapLocation loc)
        throws GameActionException
    {
        Team mine = rc.senseMine(loc);
        return mine == Team.NEUTRAL || mine == rc.getTeam().opponent();
    }

    boolean busy(RobotInfo info)
    {
        return info.roundsUntilMovementIdle > 0;
    }

    boolean robotBusy(MapLocation loc, Team team)
    {
        RobotInfo info = robotInfo(loc, team);
        return info != null ? isBusy(info) : null;
    }

    RobotInfo robotInfo(MapLocation loc, Team team)
    {
        Robot r = (Robot) rc.senseObjectAtLocation(loc);
        if (r == null) return null;

        RobotInfo info = rc.senseRobotInfo(r);
        return info.team == team ? info : null;
    }

    // Includes the diagonals!
    public Robot[] adjacentRobots(MapLocation loc, Team team)
        throws GameActionException
    {
        return rc.senseNearbyGameObjects(Robot.class, loc, 2, team);
    }

    // Robots of the team x that are within striking distance.
    public Robot[] strikeRobots(MapLocation loc, Team team)
        throws GameActionException
    {
        return rc.senseNearbyGameObjects(Robot.class, loc, 8, team);
    }

    private RobotInfo[] nearbyEnemiesCache = null;
    public RobotInfo[] nearbyEnemies()
        throws GameActionException
    {
        if (nearbyEnemiesCache != null) return nearbyEnemiesCache;

        Robot[] enemies = rc.senseNearbyGameObjects(
                Robot.class, sightRadius, rc.getTeam().opponent());

        nearbyEnemiesCache = new RobotInfo[enemies.length];
        for (int i = enemies.length; --i >= 0;)
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
        for (int i = allies.length; --i >= 0;)
            nearbyAlliesCache[i] = rc.senseRobotInfo(allies[i]);

        return nearbyAlliesCache;
    }

    private int ts;
    private boolean sightAdjusted = false;
    private int sightRadius = RobotType.SOLDIER.sensorRadiusSquared;

    public void reset()
    {
        ts = Clock.getRoundNum();
        nearbyEnemiesCache = null;

        if (!sightAdjusted && rc.hasUpgrade(Upgrade.VISION)) {
            sightAdjusted = true;
            sightRadius += GameConstants.VISION_UPGRADE_BONUS;

            nearbyAlliesTs = 0;
        }
    }

}