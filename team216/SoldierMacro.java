package team216;

import battlecode.common.*;


public class SoldierMacro
{
    RobotController rc;
    Navigation nav;
    SenseCache sense;
    Defuse defuse;
    MapLocation rallyPoint;


    SoldierMacro(
            RobotController rc, Navigation nav, SenseCache sense, Defuse defuse)
    {
        this.rc = rc;
        this.nav = nav;
        this.sense = sense;
        this.defuse = defuse;
        Direction hqDir = sense.MY_HQ.directionTo(sense.ENEMY_HQ);
        double rallyDist = Math.max(sense.DISTANCE_BETWEEN_HQS * 0.1, 4.0);
        this.rallyPoint = sense.MY_HQ.add(hqDir, (int)rallyDist);
    }


    private void boost(MapLocation loc, double weights)
    {
        MapLocation myLoc = rc.getLocation();
        double force = (1.0 / Math.sqrt(myLoc.distanceSquaredTo(loc))) * weights;
        nav.boost(myLoc.directionTo(loc), force, true);
    }

    void formup()
        throws GameActionException
    {
        detectNuke();

        if (capture()) return;

        if (readyToCharge()) charge();
        else rally();

        if (!nukeDetected) {
            encamp();
            layMine();
        }
        defuse.macro();
    }

    private static boolean nukeDetected = false;

    private void detectNuke()
        throws GameActionException
    {
        if (nukeDetected) return;
        nukeDetected = Communication.readBroadcast(0, false) > 0;
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

    private MapLocation rallyPoint() throws GameActionException
    {
        MapLocation castLoc = sense.rallyBroadcast();
        if (castLoc != null)
            return castLoc;
        return rallyPoint;
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

        // Stick to your group and shun the other group.
        RobotInfo[] allies = sense.nearbyAllies();
        for (int i = allies.length; --i >= 0;)
            boost(allies[i].location, Weights.MACRO_RALLY_MY_GROUP);

        // This should allow our group to roughly arange themselves orthogonally
        // to the enemy hq.
        MapLocation hqLoc = rc.senseEnemyHQLocation();
        boost(hqLoc, Weights.MACRO_RALLY_HQ);

        rc.setIndicatorString(2,
                "rally"
                + ": point=" + rallyLoc
                + ", allies=" + allies.length);
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
        RobotInfo[] allies = sense.nearbyAllies();
        for (int i = allies.length; --i >= 0;)
            boost(allies[i].location, Weights.MACRO_CHARGE_MY_GROUP);

        // Avoid mines
        MapLocation[] mines = sense.adjacentNonAlliedMines(myLoc);
        for (int i = mines.length; --i >= 0;) {
            Direction dir = myLoc.directionTo(mines[i]);
            if (dir == Direction.OMNI) continue;
            nav.boost(dir, Weights.MACRO_CHARGE_MINES, false);
        }

        rc.setIndicatorString(2,
                "charge"
                + ": allies=" + allies.length
                + ", enemies=" + enemies.length
                + ", mines=" + mines.length);

    }

    public void layMine() throws GameActionException
    {
        MapLocation coord = rc.getLocation();

        if (rc.senseMine(coord) != null || Clock.getRoundNum() > 2000) {
            nav.layMine = Double.NEGATIVE_INFINITY;
            return;
        }

        if (Utils.distTwoPoints(coord, rc.senseHQLocation()) > 
                sense.DISTANCE_BETWEEN_HQS/5) {
            nav.layMine = Double.NEGATIVE_INFINITY;
            return;
        }


        Robot[] enemies = rc.senseNearbyGameObjects(
                Robot.class, 490, rc.getTeam().opponent());
        if (enemies.length > 0) {
            nav.layMine = Double.NEGATIVE_INFINITY;
            return;
        }

        double mineStr = sense.defensiveRelevance(coord) * Weights.LAY_MINE;

        if (rc.hasUpgrade(Upgrade.PICKAXE)) {
            int orthogonalMines = 0;
            if (rc.senseMine(coord.add(Direction.NORTH)) != null)
                orthogonalMines++;
            if (rc.senseMine(coord.add(Direction.SOUTH)) != null)
                orthogonalMines++;
            if (rc.senseMine(coord.add(Direction.EAST)) != null)
                orthogonalMines++;
            if (rc.senseMine(coord.add(Direction.WEST)) != null)
                orthogonalMines++;
            mineStr *= 5-orthogonalMines;
        }
        // TODO : make areas with encampments more enticing
        // if (rc.senseEncampmentSquare(coord)){
        //     mineStr += Weights.LAY_MINE;
        // }
        double minesNearby = rc.senseMineLocations(coord, 63, rc.getTeam()).length;
        double minesNearbyFactor = Weights.NEARBY_MINES * (30-minesNearby);

        nav.layMine =  mineStr + minesNearbyFactor;
    }

    public void encamp() throws GameActionException {
        double cost = rc.senseCaptureCost();
        if (cost >= rc.getTeamPower()) return;
        double nearRushTime = sense.est_rush_time - Clock.getRoundNum();
        if (Math.abs(nearRushTime) < 100) return;

        MapLocation[] camps = sense.localEncampments(Team.NEUTRAL);
        int step = Utils.ceilDiv(camps.length, 5);
        for (int i=0; i<camps.length; i+=step){
            if (!encampmentHack(camps[i])) {
                if (!rc.canSenseSquare(camps[i]) ||
                        rc.senseObjectAtLocation(camps[i]) == null)
                    boost(camps[i], Weights.MACRO_GET_CAMPS);
            }
        }
    }

    // TODO : add shields logic
    public boolean capture() throws GameActionException
    {
        MapLocation coord = rc.getLocation();
        if (!rc.senseEncampmentSquare(coord)) return false;
        if (encampmentHack(coord)) return false;

        double power = rc.getTeamPower();
        double cost = rc.senseCaptureCost();
        if (cost >= power) return false;

        MapLocation neutBases[] = sense.neutralEncampments();
        double supplierValue = supplierValue(sense.est_rush_time);
        // double militaryValue = militaryValue(rc.getLocation());

        rc.setIndicatorString(0, "haveShields=" + sense.haveShields() +
            ", rushtime=" + sense.est_rush_time + ", rushTimeDiff = " 
            + (sense.est_rush_time - Clock.getRoundNum()) );
        rc.setIndicatorString(1, "toRallyPt=" + Utils.distTwoPoints(rallyPoint, coord) +
            ", rushtime/5=" + sense.est_rush_time / 5 + ", supplierVal=" + 
            supplierValue);// + ", milVal=" + militaryValue);

        if (getShields())
        {
            nav.capture = RobotType.SHIELDS;
            nav.captureStrength = Weights.SHIELDS_IMPORTANCE;
            Communication.broadcast(SenseCache.NUM_SHIELDS, 20);
        } else {

            // if (supplierValue > militaryValue) {
            nav.captureStrength = supplierValue;

            int currentSuppliers =
                sense.alliedEncampments().length -
                sense.militaryEncampments();

            if (currentSuppliers % 4 == 3)
                nav.capture = RobotType.GENERATOR;
            else
                nav.capture = RobotType.SUPPLIER;
            // }

            // else {
            //     nav.captureStrength = militaryValue;
            //     double distHome = Utils.distTwoPoints(coord, sense.MY_HQ);
            //     double distThem = sense.DISTANCE_BETWEEN_HQS - distHome;

            //     if (distHome * Weights.MEDBAY > distThem * Weights.ARTILLERY)
            //         nav.capture = RobotType.MEDBAY;
            //     else
            //         nav.capture = RobotType.ARTILLERY;
            // }
        }
        return true;
    }

    private boolean getShields() throws GameActionException {
        return !sense.haveShields() && 
            (sense.est_rush_time > Weights.MIN_SHIELD_MAPSIZE || 
                rc.senseEncampmentSquares(rc.getLocation(), 63, null).length > 3)
            && 
            Utils.distTwoPoints(rallyPoint, rc.getLocation()) < 8;
    }

    private boolean encampmentHack(MapLocation camp)
        throws GameActionException
    {
        if (rc.senseEncampmentSquares(camp, 4, null).length > 4
            || rc.getLocation().isAdjacentTo(rc.senseHQLocation())) {
            if ((camp.x + camp.y) % 2 == 0)
                return true;
        }
        // TODO : add adjacent(hq) stuff
        return false;
    }

    // Estimated payoff (as # of soldiers) within a given number of turns
    public double supplierValue(MapLocation camp, MapLocation coord, double turns)
             throws GameActionException{
        int currentSuppliers = sense.alliedEncampments().length -
                sense.militaryEncampments();
        int untilJump = sense.suppliersUntilJump[currentSuppliers - 1];
        int spawnRate = sense.roundsBySuppliers[currentSuppliers - 1];

        // Check if supplier benefit is out of reach
        if (untilJump > sense.neutralEncampments().length)
            return Double.NEGATIVE_INFINITY;

        double turnsAway = Utils.distTwoPoints(camp, coord);
        double turnCost = (untilJump * (spawnRate + turnsAway));

        return (turns - turnCost * Weights.SOLDIER_VAL) / (spawnRate - 1) -
                (turns - turnCost) / spawnRate;
    }

    public double supplierValue(double turns) throws GameActionException
    {
        int currentSuppliers = sense.alliedEncampments().length -
                sense.militaryEncampments();
        int untilJump = sense.suppliersUntilJump[currentSuppliers];
        int spawnRate = sense.roundsBySuppliers[currentSuppliers];

        double nearRushTime = sense.est_rush_time - Clock.getRoundNum();
        // System.err.println(Math.abs(nearRushTime));
        if (Math.abs(nearRushTime) < 90)
            return Double.NEGATIVE_INFINITY;

        // Check if supplier benefit is out of reach
        if (untilJump > sense.neutralEncampments().length)
            return Double.NEGATIVE_INFINITY;

        double turnCost = (untilJump * spawnRate);

        return Weights.SUPPLIER_COEF *
            ( (turns - turnCost * Weights.SOLDIER_VAL) / (spawnRate - 1) -
                (turns - turnCost) / spawnRate );
    }

    // Estimated worth of a military encampment
    public double militaryValue(MapLocation camp)
             throws GameActionException{
        double dropOff = Weights.MILITARY_DROP * sense.militaryEncampments();
        return sense.strategicRelevance(camp) * Weights.MIL_CAMP_VAL -
                dropOff - sense.est_rush_time*Weights.MIL_MAPSIZE;
        // TODO : distance to last enemy seen should affect this value
    }
}
