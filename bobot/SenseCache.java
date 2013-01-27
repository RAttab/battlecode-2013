package bobot;

import battlecode.common.*;

/** Note that while this started as a caching mechanism for the sense call it
    doesn't do much of that right now. Right now it provides a nice and clean
    interface to the RobotController's sense interface and hides away some of
    the uglyness.

    While it can still do caching just fine, make sure its really worth it
    before adding it. Cache invalidation isn't free and if it's never actually
    reused then it's wasted. I recommend only doing caching when it was tested
    (use the ByteCode class) and confirmed that there's a saving to be had.

    Feel free to abstract any other ugly interfacs using this class.
 */
public class SenseCache
{
    RobotController rc;

    SenseCache(RobotController rc)
    {
        this.rc = rc;
    }

    double sight()
    {
        return sightRadius;
    }

    boolean nonAlliedMine(MapLocation loc)
        throws GameActionException
    {
        Team mine = rc.senseMine(loc);
        return mine == Team.NEUTRAL || mine == rc.getTeam().opponent();
    }

    MapLocation[] adjacentNonAlliedMines(MapLocation loc)
        throws GameActionException
    {
        return rc.senseNonAlliedMineLocations(loc, 2);
    }

    boolean busy(RobotInfo info)
    {
        return info.roundsUntilMovementIdle > 0;
    }

    boolean robotBusy(MapLocation loc, Team team)
        throws GameActionException
    {
        RobotInfo info = robotInfo(loc, team);
        return info != null ? busy(info) : false;
    }

    boolean battleBot(RobotInfo info)
    {
        return
            info.type == RobotType.SOLDIER ||
            info.type == RobotType.ARTILLERY ||
            info.type == RobotType.MEDBAY;
    }

    RobotInfo robotInfo(MapLocation loc, Team team)
        throws GameActionException
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


    public RobotInfo[] allEnemies()
        throws GameActionException
    {
        final int SAMPLE_SIZE = 10;

        Robot[] enemies = rc.senseNearbyGameObjects(
                Robot.class, Integer.MAX_VALUE, rc.getTeam().opponent());

        int steps = Utils.ceilDiv(enemies.length, SAMPLE_SIZE);
        int length = enemies.length / steps;

        RobotInfo[] info = new RobotInfo[length];
        for (int i = info.length; --i >= 0;)
            info[i] = rc.senseRobotInfo(enemies[i*steps]);

        return info;
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


    private RobotInfo[] nearbyAlliesCache = null;
    public RobotInfo[] nearbyAllies()
        throws GameActionException
    {
        if (nearbyAlliesCache != null) return nearbyAlliesCache;

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
        nearbyAlliesCache = null;
        nearbyEnemiesCache = null;

        if (!sightAdjusted && rc.hasUpgrade(Upgrade.VISION)) {
            sightAdjusted = true;
            sightRadius += GameConstants.VISION_UPGRADE_BONUS;
        }
    }

}