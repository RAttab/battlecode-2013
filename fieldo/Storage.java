package fieldo;

import battlecode.common.*;

public class Storage {

    // TODO
        // All the methods for non-constants need to check if they've been called
        // already this turn (and if so, just return the cache). Otherwise, even if
        // we're on a round % 3 (for example), we still risk doing the calculation many times.

    // TODO (MAJOR PRIORITY)
        // The method of calling Clock.roundNum() % 3 is no good, because if a method isn't called
        // for many turns (if a soldier is diffusing a few mines in a row, for example) the info
        // will be completely out-of-date, and this causes some problems.
        // Also, check out Utils.

    // Finals
    public static final int[] roundsBySuppliers = {10, 9, 8, 8, 7, 7, 6, 6, 6, 5, 5, 5, 5, 
                                                    4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 
                                                    3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 
                                                    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
                                                    2, 2, 2, 2, 2, 1};

    public static final int[] suppliersUntilJump = {1, 1, 2, 1, 2, 1, 3, 2, 1, 4, 3, 2, 1, 
                                                    6, 5, 4, 3, 2, 1, 12, 11, 10, 9, 8, 7, 6, 
                                                    5, 4, 3, 2, 1, 26, 25, 24, 23, 22, 21, 20, 19, 
                                                    18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 
                                                    5, 4, 3, 2, 1, 1};

    // Constants

    public static Team MY_TEAM;
    public static Team ENEMY_TEAM;
    public static MapLocation MY_HQ;
    public static MapLocation ENEMY_HQ;
    public static MapLocation CENTER; //center between HQs, not true center
    public static double DISTANCE_BETWEEN_HQ;
    public static double SLOPE;
    public static int MAP_HEIGHT;
    public static int MAP_WIDTH;
    public static int MAP_SIZE;
    public static RobotInfo MY_INFO;
    public static Robot ME;
    public static RobotController RC;

    public static double EST_RUSH_TIME=-1;
    public static int RUSH_TIME_CALCS=0;

    public static final int LC_RADIUS = 63;

    private static double defensive_relevance; 
    private static double strategic_relevance;
    private static Direction direction_to_enemy_hq;
    private static double distance_to_enemy_hq;

    private static Robot[][] nearby_enemies = new Robot[2][];
    private static Integer[] nearby_enemies_last_updated = new Integer[2];

    private static int number_of_nearby_enemies;

    private static Robot[][] nearby_allies = new Robot[3][];
    private static Integer[] nearby_allies_last_updated = new Integer[3];

    private static MapLocation[] nearby_friendly_mines;
    private static MapLocation[][] nearby_nonallied_mines = new MapLocation[2][];

    private static MapLocation[] allEncampments = null;
    private static MapLocation[] alliedEncampments = null;
    private static int campRadius;
    public static MapLocation[] localEncampments = null;
    public static MapLocation[] neutralEncampments = null;

    public static boolean nukePanic = false;

    public static void calculateValues(RobotController rc) {
        try {
            RC = rc;
            MY_TEAM = rc.getTeam();
            ENEMY_TEAM = MY_TEAM.opponent();
            MY_HQ = rc.senseHQLocation();
            ENEMY_HQ = rc.senseEnemyHQLocation();
            ME = rc.getRobot();
            MY_INFO = rc.senseRobotInfo(ME);
            MAP_HEIGHT = rc.getMapHeight();
            MAP_WIDTH = rc.getMapWidth();
            MAP_SIZE = MAP_HEIGHT * MAP_WIDTH;
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    // Variables

    public static double getRushTime() {
        //calculate estimated turns for rush
        if (RUSH_TIME_CALCS == 0){
            System.err.println(Clock.getBytecodeNum());
            double x_dif = ENEMY_HQ.x - MY_HQ.x;
            double y_dif = ENEMY_HQ.y - MY_HQ.y;
            double x;
            double y;
            double offset;
            double time = 0.0;
            String s = "";
            //rc.setIndicatorString(0, "c=" + CENTER + ", m=" + MY_HQ + ", e=" + ENEMY_HQ + ", xdif=" + x_dif + ", ydif" + y_dif + ", slope=" + SLOPE);
            for (int i=20; --i>0;) {
                offset = 6 * Math.random() - 3;
                x = Math.random() * x_dif + MY_HQ.x;
                y = SLOPE * x + ENEMY_HQ.y;
                s += " (" + (int)x + ", " + (int)y + ")";
                s += 1;
                if (Team.NEUTRAL.equals(RC.senseMine(new MapLocation((int)x, (int)y)))){
                    time += 12;
                    s += "!";
                }
            }
            System.err.println(s);
            time *= (distanceBetweenHQs()/20.0);
            time += distanceBetweenHQs();
            EST_RUSH_TIME = time;
            RUSH_TIME_CALCS = 2;
            System.err.println(Clock.getBytecodeNum());
        }
        return EST_RUSH_TIME;
    }
    public static void updateRushTime() {
        double x_dif = ENEMY_HQ.x - MY_HQ.x;
        double y_dif = ENEMY_HQ.y - MY_HQ.y;
        double x;
        double y;
        double offset;
        double time = 0.0;
        for (int i=10; --i>0;) {
            offset = 6 * Math.random() - 3;
            x = Math.random() * x_dif + MY_HQ.x;
            y = SLOPE * x + ENEMY_HQ.y;
            if (Team.NEUTRAL.equals(RC.senseMine(new MapLocation((int)x, (int)y)))){
                time += 12;
            }
        }
        time *= (distanceBetweenHQs()/10.0);
        time += distanceBetweenHQs();
        EST_RUSH_TIME = (EST_RUSH_TIME*RUSH_TIME_CALCS + time)/(RUSH_TIME_CALCS+1);
        RUSH_TIME_CALCS++;
    }

    public static double distanceBetweenHQs() {
        return DISTANCE_BETWEEN_HQ == 0.0 ? DISTANCE_BETWEEN_HQ = Utils.distTwoPoints(MY_HQ, ENEMY_HQ) : DISTANCE_BETWEEN_HQ;
    }

    public static double slopeBetweenHQs() {
        return SLOPE == 0.0 ? SLOPE = (double)(MY_HQ.y - ENEMY_HQ.y) / (MY_HQ.x - ENEMY_HQ.x) : SLOPE;
    }

    public static MapLocation centerBetweenHQs() {
        return CENTER == null ? CENTER = new MapLocation((MY_HQ.x + ENEMY_HQ.x)/2,(MY_HQ.y + ENEMY_HQ.y)/2) : CENTER;
    }

    public static MapLocation myLocation() {
        // No need to cache this one, it's free
        return RC.getLocation();
    }

    public static Direction directionToEnemyHQ() {
        if (direction_to_enemy_hq == null || Clock.getRoundNum() % 6 == 0)
            direction_to_enemy_hq = RC.getLocation().directionTo(ENEMY_HQ);
        return direction_to_enemy_hq;
    }

    public static double distanceToEnemyHQ() {
        if (distance_to_enemy_hq == 0.0 || Clock.getRoundNum() % 3 == 1)
            distance_to_enemy_hq = RC.getLocation().distanceSquaredTo(ENEMY_HQ);
        return distance_to_enemy_hq;
    }

    public static double defensiveRelevance() {
        if (defensive_relevance == 0.0 || Clock.getRoundNum() % 3 == 2)
            defensive_relevance = Utils.defensiveRelevance(RC.getLocation());
        return defensive_relevance;
    }

    public static double strategicRelevance() {
        if (strategic_relevance == 0.0 || Clock.getRoundNum() % 3 == 2)
            strategic_relevance = Utils.strategicRelevance(RC.getLocation());
        return strategic_relevance;
    }

    // This method doesn't cache, it simply returns the store result if we call it more than once in a turn
    public static Robot[] nearbyEnemies(int radiusSquared) {
        // Since we only have shitty fixed-sized arrays, we implement this only for common inputs:
        int i;
        switch (radiusSquared) {
            case LC_RADIUS:
                    i = 0;
                    break;
            case Soldier.GL_RADIUS:
                    i = 1;
                    break;
            // We don't cache that radius
            default: return RC.senseNearbyGameObjects(Robot.class, radiusSquared, ENEMY_TEAM);
        }

        if (nearby_enemies[i] == null || nearby_enemies_last_updated[i] < Clock.getRoundNum()) {
            nearby_enemies[i] = RC.senseNearbyGameObjects(Robot.class, radiusSquared, ENEMY_TEAM);
            nearby_enemies_last_updated[i] = Clock.getRoundNum();
        }

        return nearby_enemies[i];
    }

    public static int numberOfNearbyEnemies() {
        // Radius = 1
        if (number_of_nearby_enemies == 0 || Clock.getRoundNum() % 3 == 0)
            number_of_nearby_enemies = RC.senseNearbyGameObjects(Robot.class, 1, ENEMY_TEAM).length;
        return number_of_nearby_enemies;
    }

    public static Robot[] nearbyAllies(int radiusSquared) {
        // Since we only have shitty fixed-sized arrays, we implement caching for common inputs:
        int i;
        switch (radiusSquared) {
            case 4: 
                    i = 0;
                    break;
            case LC_RADIUS:
                    i = 1;
                    break;
            case Soldier.GL_RADIUS:
                    i = 2;
                    break;
            // We don't cache that radius
            default: return RC.senseNearbyGameObjects(Robot.class, radiusSquared, MY_TEAM);
        }

        if (nearby_allies[i] == null || nearby_allies_last_updated[i] < Clock.getRoundNum()) {
            nearby_allies[i] = RC.senseNearbyGameObjects(Robot.class, radiusSquared, MY_TEAM);
            nearby_allies_last_updated[i] = Clock.getRoundNum();
        }

        return nearby_allies[i];
    }

    public static MapLocation[] nearbyNonAlliedMines(int radiusSquared) {
        int i;
        switch (radiusSquared) {
            case LC_RADIUS:
                    i = 0;
                    break;
            case Soldier.GL_RADIUS:
                    i = 1;
                    break;
            default: return RC.senseNonAlliedMineLocations(RC.getLocation(), radiusSquared);
        }

        if(nearby_nonallied_mines[i] == null || Clock.getRoundNum() % 3 == 0)
            nearby_nonallied_mines[i] = RC.senseNonAlliedMineLocations(RC.getLocation(), radiusSquared);

        Soldier.debug_checkBc(RC, "Storage.nearbyNonAlliedMines()");
        return nearby_nonallied_mines[i];
    }

    public static MapLocation[] nearbyFriendlyMines() {
        if (nearby_friendly_mines == null || Clock.getRoundNum() % 3 == 0)
            nearby_friendly_mines = RC.senseMineLocations(RC.getLocation(), LC_RADIUS, MY_TEAM);
        return nearby_friendly_mines;
    }

    public static boolean nukePanic() throws GameActionException {
        if (Clock.getRoundNum() < 199){
            RC.setIndicatorString(2, "<199");
            return false;
        } else {
            RC.setIndicatorString(2, "200+");
            if (RC.getType().equals(RobotType.HQ)){
                nukePanic = RC.senseEnemyNukeHalfDone();
                RC.broadcast(5666, 1); //TODO : broadcasting stuff
                return nukePanic;
            } else {
                if (nukePanic)
                    return true;
                if (RC.readBroadcast(5666) == 1){
                    nukePanic = true;
                    return true;
                }
                return false;
            }
        }
    }
    public static MapLocation[] neutralEncampments() {
        if (allEncampments == null)
            allEncampments = RC.senseAllEncampmentSquares();
        return allEncampments;
    }

    public static MapLocation[] allEncampments() {
        if (allEncampments == null)
            allEncampments = RC.senseAllEncampmentSquares();
        return allEncampments;
    }

    public static MapLocation[] alliedEncampments() {
        if (alliedEncampments == null)
            alliedEncampments = RC.senseAlliedEncampmentSquares();
        return alliedEncampments;
    }

    public static MapLocation[] localEncampments() 
    throws GameActionException{
        if (localEncampments == null) {
            localEncampments = RC.senseEncampmentSquares(MY_INFO.location, LC_RADIUS, Team.NEUTRAL);
            campRadius = LC_RADIUS;
            while (localEncampments.length < 1 && campRadius < 490) {
                campRadius *= 2;
                localEncampments = RC.senseEncampmentSquares(MY_INFO.location, campRadius, Team.NEUTRAL);
            }
        }
        return localEncampments;
    }

    public static void resetTurn(){
        allEncampments = null;
        alliedEncampments = null;
        localEncampments = null;
        neutralEncampments = null;
    }

    public static int militaryEncampments(){
        return 0; // TODO: military encampments should broadcast themselves so we can keep track
                    // this method should read the channel and report the number
    }

    public static int numShields(){
        return 0; // TODO: same as above
    }

    // Estimated payoff (as # of soldiers) within a given number of turns
    public static double supplierValue(MapLocation camp, MapLocation coord, double turns){
        int currentSuppliers = alliedEncampments().length - militaryEncampments();
        int untilJump = suppliersUntilJump[currentSuppliers];
        int spawnRate = roundsBySuppliers[currentSuppliers];

        // If it'll be impossible for this supplier to have any benefit, it's worthless.
        if (untilJump > neutralEncampments().length)
            return Double.NEGATIVE_INFINITY;

        double turnsAway = Utils.distTwoPoints(camp, coord);
        double turnCost = (untilJump * (spawnRate + turnsAway));

        return (turns - turnCost * Weights.SOLDIER_VAL) / (spawnRate - 1) - 
                (turns - turnCost) / spawnRate;
    }
    public static double supplierValue(double turns){
        int currentSuppliers = alliedEncampments().length - militaryEncampments();
        int untilJump = suppliersUntilJump[currentSuppliers];
        int spawnRate = roundsBySuppliers[currentSuppliers];

        // If it'll be impossible for this supplier to have any benefit, it's worthless.
        if (untilJump > neutralEncampments().length)
            return Double.NEGATIVE_INFINITY;

        double turnCost = (untilJump * spawnRate);

        return (turns - turnCost * Weights.SOLDIER_VAL) / (spawnRate - 1) - 
                (turns - turnCost) / spawnRate;
    }

    // Estimated worth of a military encampment
    public static double militaryValue(MapLocation camp){
        double dropOff = Weights.MILITARY_DROP * militaryEncampments();
        return Utils.strategicRelevance(camp) * Weights.MIL_CAMP_VAL - 
                dropOff - EST_RUSH_TIME*Weights.MIL_MAPSIZE;
    // TODO : distance to last enemy seen should affect this value
    }
}