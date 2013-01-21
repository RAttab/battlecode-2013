package nuclear;

import battlecode.common.*;

public class Storage {

    // Constants

    public static double DISTANCE_BETWEEN;
    public static double EST_RUSH_TIME;
    public static MapLocation MY_HQ;
    public static MapLocation ENEMY_HQ;
    public static MapLocation CENTER; //center between HQs, not true center
    public static double SLOPE;
    public static int MAP_HEIGHT;
    public static int MAP_WIDTH;
    public static int MAP_SIZE;
    public static RobotInfo MY_INFO;
    public static Robot ME;
    public static Direction TO_ENEMY;

    public static MapLocation[] mineLocs;

    public static void calculateValues(RobotController rc) {
        try {
            MY_HQ = rc.senseHQLocation();
            ENEMY_HQ = rc.senseEnemyHQLocation();
            CENTER = new MapLocation((MY_HQ.x + ENEMY_HQ.x)/2,(MY_HQ.y + ENEMY_HQ.y)/2);
            DISTANCE_BETWEEN = Math.sqrt(MY_HQ.distanceSquaredTo(ENEMY_HQ));
            SLOPE = (double)(MY_HQ.y - ENEMY_HQ.y) / (MY_HQ.x - ENEMY_HQ.x);
            ME = rc.getRobot();
            MY_INFO = rc.senseRobotInfo(ME);
            MAP_HEIGHT = rc.getMapHeight();
            MAP_WIDTH = rc.getMapWidth();
            MAP_SIZE = MAP_HEIGHT * MAP_WIDTH;
            TO_ENEMY = MY_HQ.directionTo(ENEMY_HQ);
            //EST_RUSH_TIME = getRushTime(rc);
            mineLocs = getMineLocs(rc);
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    public static MapLocation[] getMineLocs(RobotController rc) {
        MapLocation[] out = new MapLocation[25];
        Direction rightOf = TO_ENEMY.rotateRight().rotateRight();
        Direction leftOf = TO_ENEMY.rotateLeft().rotateLeft();
        out[0] = MY_HQ.add(leftOf, 1);
        out[1] = MY_HQ.add(rightOf, 1);
        out[2] = MY_HQ.add(TO_ENEMY, 1);
        out[3] = MY_HQ.add(TO_ENEMY.opposite(), 1);
        out[4] = MY_HQ.add(TO_ENEMY, 1).add(rightOf, 2);
        out[5] = MY_HQ.add(TO_ENEMY, 2).add(rightOf, 1);
        out[6] = MY_HQ.add(TO_ENEMY, 1).add(leftOf, 2);
        out[7] = MY_HQ.add(TO_ENEMY, 2).add(leftOf, 1);
        out[8] = MY_HQ.add(TO_ENEMY, 3);
        out[9] = MY_HQ.add(TO_ENEMY.opposite(), 1).add(rightOf, 2);
        out[10] = MY_HQ.add(TO_ENEMY.opposite(), 1).add(leftOf, 2);
        out[11] = MY_HQ.add(TO_ENEMY.opposite(), 2).add(rightOf);
        out[12] = MY_HQ.add(TO_ENEMY.opposite(), 2).add(leftOf);
        out[13] = MY_HQ.add(rightOf, 3);
        out[14] = MY_HQ.add(leftOf, 3);
        out[15] = MY_HQ.add(TO_ENEMY, 1).add(rightOf, 4);
        out[16] = MY_HQ.add(TO_ENEMY, 1).add(leftOf, 4);
        out[17] = MY_HQ.add(TO_ENEMY, 2).add(rightOf, 3);
        out[18] = MY_HQ.add(TO_ENEMY, 2).add(leftOf, 3);
        out[19] = MY_HQ.add(TO_ENEMY, 3).add(rightOf, 2);
        out[20] = MY_HQ.add(TO_ENEMY, 3).add(leftOf, 2);
        out[21] = MY_HQ.add(TO_ENEMY, 4).add(leftOf, 1);
        out[22] = MY_HQ.add(TO_ENEMY, 4).add(rightOf, 1);
        out[23] = MY_HQ.add(TO_ENEMY, 5);
        out[24] = MY_HQ.add(TO_ENEMY.opposite(), 3);
        return out;
    }

    // Variables


    public static double getRushTime(RobotController rc) {
        //calculate estimated turns for rush
        double x_dif = MY_HQ.x - ENEMY_HQ.x;
        double y_dif = MY_HQ.y - ENEMY_HQ.y;
        double x;
        double y;
        double offset;
        double time = 0.0;
        String s = "";
        //rc.setIndicatorString(0, "c=" + CENTER + ", m=" + MY_HQ + ", e=" + ENEMY_HQ + ", xdif=" + x_dif + ", ydif" + y_dif + ", slope=" + SLOPE);
        for (int i=0; i<20; i++) {
            offset = 6 * Math.random() - 3;
            x = Math.random() * x_dif;
            y = SLOPE * x + ENEMY_HQ.y;
            s += " (" + (int)x + ", " + (int)y + ")";
            if (Team.NEUTRAL.equals(rc.senseMine(new MapLocation((int)x, (int)y)))){
                time += 12;
                s += "!";
            }
            time += 1;
        }
        rc.setIndicatorString(2, s);
        time *= (DISTANCE_BETWEEN/20.0);
        time += DISTANCE_BETWEEN;
        return time;
    }


    // Combat methods
    // getEnemyCenter
    // getFriendlyCenter

}
