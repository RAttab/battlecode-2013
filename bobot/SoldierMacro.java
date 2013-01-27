package bobot;

import battlecode.common.*;


public class SoldierMacro
{
    RobotController rc;
    Navigation nav;
    SenseCache sense;
    Defuse defuse;


    SoldierMacro(
            RobotController rc, Navigation nav, SenseCache sense, Defuse defuse)
    {
        this.rc = rc;
        this.nav = nav;
        this.sense = sense;
        this.defuse = defuse;
    }


    private void boost(MapLocation loc, double weights)
    {
        MapLocation myLoc = rc.getLocation();
        double force = (1.0 / myLoc.distanceSquaredTo(loc)) * weights;
        nav.boost(myLoc.directionTo(loc), force, true);
    }

    void formup()
        throws GameActionException
    {
        detectNuke();

        if (readyToCharge()) charge();
        else rally();

        defuse.macro();
    }

    private static boolean nukeDetected = false;
    private void detectNuke()
    {
        if (nukeDetected) return;
        // \todo Look at comm to see if HQ detected a nuke.
    }

    private static boolean charging = false;
    private boolean readyToCharge()
        throws GameActionException
    {
        // We want to continue forming up even if there's a nuke.
        double groupSize = charging ?
            Weights.MACRO_CHARGE_GROUP_SIZE :
            Weights.MACRO_RALLY_GROUP_SIZE;

        if (nukeDetected) groupSize *= 0.5;

        charging = sense.nearbyAllies().length >= groupSize;
        return charging;
    }

    private MapLocation rallyPoint()
    {
        MapLocation myHq = rc.senseHQLocation();
        MapLocation enemyHq = rc.senseEnemyHQLocation();

        double hqDist = Math.sqrt(myHq.distanceSquaredTo(enemyHq));
        Direction hqDir = myHq.directionTo(enemyHq);

        double rallyDist = Math.max(hqDist * 0.2, 3.0);
        return myHq.add(hqDir, (int)rallyDist);
    }

    private int groupId(Robot robot)
    {
        return robot.getID() % 2;
    }

    // The idea is that we want to form multiple groups in order to create a
    // concave. so we rally both groups to the same point and yet have them push
    // away from each other. Should hopefully lead to 2-3 soldier splits.
    private void rally()
        throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();

        MapLocation rallyLoc = rallyPoint();
        Direction rallyDir =
            !myLoc.equals(rallyLoc) ?  myLoc.directionTo(rallyPoint()) : null;
        nav.boost(rallyDir, Weights.MACRO_RALLY_POINT, true);

        int myGroup = groupId(rc.getRobot());

        // Stick to your group and shun the other group.
        RobotInfo[] allies = sense.nearbyAllies();
        int myGroupCount = 0, otherGroupCount = 0;
        for (int i = allies.length; --i >= 0;) {

            if (groupId(allies[i].robot) == myGroup) {
                boost(allies[i].location, Weights.MACRO_RALLY_MY_GROUP);
                myGroupCount++;
            }
            else {
                boost(allies[i].location, Weights.MACRO_RALLY_OTHER_GROUP);
                otherGroupCount++;
            }
        }

        // This should allow our group to roughly arange themselves orthogonally
        // to the enemy hq.
        MapLocation hqLoc = rc.senseEnemyHQLocation();
        boost(hqLoc, Weights.MACRO_RALLY_HQ);

        rc.setIndicatorString(2,
                "rally(" + myGroup + ")"
                + ": point=" + rallyLoc
                + ", allies=" + allies.length
                + ", myGroup=" + myGroupCount
                + ", otherGroup=" + otherGroupCount);
    }

    private void charge()
        throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();

        // Head to the HQ.
        MapLocation hqLoc = rc.senseEnemyHQLocation();
        boost(hqLoc, Weights.MACRO_CHARGE_HQ);

        // Hunt and seek enemies
        RobotInfo[] enemies = sense.allEnemies();
        for (int i = enemies.length; --i >= 0;)
            boost(enemies[i].location, Weights.MACRO_CHARGE_ENEMIES);

        // Stay in group formation.
        int myGroup = groupId(rc.getRobot());
        RobotInfo[] allies = sense.nearbyAllies();
        int myGroupCount = 0, otherGroupCount = 0;
        for (int i = allies.length; --i >= 0;) {

            if (groupId(allies[i].robot) == myGroup) {
                boost(allies[i].location, Weights.MACRO_CHARGE_MY_GROUP);
                myGroupCount++;
            }
            else {
                boost(allies[i].location, Weights.MACRO_CHARGE_OTHER_GROUP);
                otherGroupCount++;
            }
        }

        // Avoid mines
        MapLocation[] mines = sense.adjacentNonAlliedMines(myLoc);
        for (int i = mines.length; --i >= 0;) {
            Direction dir = myLoc.directionTo(mines[i]);
            if (dir == Direction.OMNI) continue;
            nav.boost(dir, Weights.MACRO_CHARGE_MINES, false);
        }


        rc.setIndicatorString(2,
                "charge(" + myGroup + ")"
                + ": allies=" + allies.length
                + ", enemies=" + enemies.length
                + ", mines=" + mines.length
                + ", myGroup=" + myGroupCount
                + ", otherGroup=" + otherGroupCount);

    }
}
